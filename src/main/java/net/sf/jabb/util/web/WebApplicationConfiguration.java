/*
Copyright 2012 James Hu

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
*/
package net.sf.jabb.util.web;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.web.bind.annotation.RequestMapping;

import net.sf.jabb.util.bean.StringKeyValueBean;
import net.sf.jabb.util.col.MapValueFactory;
import net.sf.jabb.util.col.PutIfAbsentMap;

/**
 * Configuration information of a web application
 * @author James Hu
 *
 */
public class WebApplicationConfiguration implements InitializingBean, ApplicationContextAware{
	private static final Log log = LogFactory.getLog(WebApplicationConfiguration.class);
	
	protected List<String> mainMenuItems;
	protected List<StringKeyValueBean> supportedLocales;
	protected String defaultLocale;
	protected String jQueryTheme;
	protected ApplicationContext appContext;
	protected Map<String, WebMenuItem> menus;
	
	@SuppressWarnings("serial")
	static class MenuItemExt extends WebMenuItem {
		String path;
		String menuName;
		int order;

		MenuItemExt(WebMenu def, RequestMapping mapping){
			// try to get url from RequestMapping annotation
			if (mapping != null){
				String[] urls = mapping.value();
				if (urls != null && urls.length > 0){
					this.url = urls[0];
				}
			}
			
			if (def != null){
				// get url from WebMenu annotation
				String explicitUrl = def.url();
				if (explicitUrl != null && explicitUrl.length() > 0){
					this.url = def.url();
				}
				
				this.title = def.value();
				this.path = def.path();
				this.order = def.order();
				this.menuName = def.menu();
			}
			
			if (this.path == null || this.path.length() == 0){
				this.path = this.url;
			}
		}
		
		public WebMenuItem toWebMenuItem(){
			WebMenuItem result = new WebMenuItem();
			result.title = this.title;
			result.url = this.url;
			result.subMenu = this.subMenu;
			result.breadcrumbs = this.breadcrumbs;
			return result;
		}
		
		@Override
		public String toString(){
			return ToStringBuilder.reflectionToString(this);
		}

		@Override
		public int compareTo(WebMenuItem o) {
			if (o instanceof MenuItemExt){
				return order - ((MenuItemExt)o).order;
			}else{
				return 0;
			}
		}
	}
	
	@Override
	public void setApplicationContext(ApplicationContext applicationContext)
			throws BeansException {
		appContext = applicationContext;
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		scanForMenuItems();
	}

	/**
	 * Scan all the beans for menu items
	 */
	public void scanForMenuItems(){
		Map<String, Map<String, MenuItemExt>> allMenuItems = new PutIfAbsentMap<String, Map<String, MenuItemExt>>(
				new HashMap<String, Map<String, MenuItemExt>>(), 
				new MapValueFactory<String, Map<String, MenuItemExt>>(){
					@Override
					public Map<String, MenuItemExt> createValue(String key) {
						return new TreeMap<String, MenuItemExt>();
					}
				});
				//new HashMap<String, Map<String, MenuItemExt>>();	// <menuName, <path, MenuItemExt>>
		
		// Get all beans that may have menu items defined
		Map<String, Object> beans =  appContext.getBeansWithAnnotation(WebMenu.class);
		beans.putAll(appContext.getBeansWithAnnotation(RequestMapping.class));
		
		// Find all menu items 
		for (Object bean: beans.values()){
			Class<? extends Object> beanClass = bean.getClass();
			// Check class level annotations first
			RequestMapping classRequestMapping = beanClass.getAnnotation(RequestMapping.class);
			WebMenu classWebMenu = beanClass.getAnnotation(WebMenu.class);
			MenuItemExt classMenuItem = new MenuItemExt(classWebMenu, classRequestMapping);
			
			String baseMenuName = classMenuItem.menuName;
			String baseUrl = classMenuItem.getUrl() != null ? classMenuItem.getUrl() : "";
			String basePath = classMenuItem.path != null ? classMenuItem.path : "";
			
			if(classMenuItem.menuName != null){		// it is also a menu item
				MenuItemExt existing = allMenuItems.get(classMenuItem.menuName).put(basePath, classMenuItem);
				if (existing != null){
					log.error("Duplicated web menu item definitions in " + beanClass.getName() 
							+ ".\n\tExisting: " + existing + "\n\tCurrent: " + classMenuItem);
				}
			}
			
			// Then look into all the methods
			for(Method method : beanClass.getDeclaredMethods()) {
				RequestMapping methodRequestMapping = method.getAnnotation(RequestMapping.class);
				WebMenu methodWebMenu = method.getAnnotation(WebMenu.class);
				MenuItemExt methodMenuItem = new MenuItemExt(methodWebMenu, methodRequestMapping);
				
				if (methodMenuItem.menuName != null){
					if ("".equals(methodMenuItem.menuName)){
						methodMenuItem.menuName = baseMenuName;
					}
					methodMenuItem.url = baseUrl + methodMenuItem.getUrl();
					methodMenuItem.path = basePath + methodMenuItem.path;
					MenuItemExt existing = allMenuItems.get(methodMenuItem.menuName).put(methodMenuItem.path, methodMenuItem);
					if (existing != null){
						log.error("Duplicated web menu item definitions in " + beanClass.getName() + "." + method.toGenericString() 
								+ ".\n\tExisting: " + existing + "\n\tCurrent: " + methodMenuItem);
					}
				}
			}
		}
		
		// construct menu trees
		menus = new HashMap<String, WebMenuItem>();
		
		for (Map.Entry<String, Map<String, MenuItemExt>> menuItems: allMenuItems.entrySet()){
			String menuName = menuItems.getKey();
			Map<String, MenuItemExt> items = menuItems.getValue();
			WebMenuItem root = new WebMenuItem();
			menus.put(menuName, root);
			for (MenuItemExt itemExt: items.values()){
				String path = itemExt.path;
				WebMenuItem parent = null;
				do{
					path = StringUtils.substringBeforeLast(path, "/");
					if (path == null || path.indexOf('/') == -1){
						parent = root;
						break;
					}
					parent = items.get(path);
				}while (parent == null);
				parent.addSubItem(itemExt);
			}
			
			// clean up the tree
			cleanUpMenuTree(root);
			log.info("Menu '" + menuName + "' loaded:\n" + root);
		}
	}
	
	/**
	 * Convert MenuItemExt to WebMenuItem, and clean up all data
	 * @param root
	 */
	protected void cleanUpMenuTree(WebMenuItem root){
		List<WebMenuItem> subMenu = root.getSubMenu();
		List<WebMenuItem> rootBreadcrumbs = root.getBreadcrumbs();
		if (subMenu != null && subMenu.size() > 0){
			Collections.sort(subMenu);
			for (int i = 0; i < subMenu.size(); i++){
				WebMenuItem item = subMenu.get(i);
				if (item instanceof MenuItemExt){
					MenuItemExt itemExt = (MenuItemExt)item;
					item = itemExt.toWebMenuItem();
					subMenu.set(i, item);
				}
				item.breadcrumbs = new ArrayList<WebMenuItem>();
				if (rootBreadcrumbs != null){
					item.breadcrumbs.addAll(rootBreadcrumbs);
				}
				item.breadcrumbs.add(item);
				cleanUpMenuTree(item);
			}
		}
	}

	/**
	 * @deprecated
	 * @return
	 */
	public List<String> getMainMenuItems() {
		return mainMenuItems;
	}

	/**
	 * @deprecated
	 * @param mainMenuItems
	 */
	public void setMainMenuItems(List<String> mainMenuItems) {
		this.mainMenuItems = mainMenuItems;
	}

	public List<StringKeyValueBean> getSupportedLocales() {
		return supportedLocales;
	}

	public void setSupportedLocales(List<StringKeyValueBean> supportedLocales) {
		this.supportedLocales = supportedLocales;
	}

	public String getDefaultLocale() {
		return defaultLocale;
	}

	public void setDefaultLocale(String defaultLocale) {
		this.defaultLocale = defaultLocale;
	}

	public String getjQueryTheme() {
		return jQueryTheme;
	}

	public void setjQueryTheme(String jQueryTheme) {
		this.jQueryTheme = jQueryTheme;
	}



}
