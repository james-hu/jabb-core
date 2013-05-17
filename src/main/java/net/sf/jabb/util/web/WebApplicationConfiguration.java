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
	protected Map<String, Map<String, WebMenuItem>> menuItemPaths;		// <menuName, <menuPath, WebMenuItem>>
	
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
			if (classWebMenu.value().length() != 0){	// not hidden
				MenuItemExt classMenuItem = new MenuItemExt(classWebMenu, classRequestMapping);
				
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
					if (methodWebMenu.value().length() != 0){	// not hidden
						MenuItemExt methodMenuItem = new MenuItemExt(methodWebMenu, methodRequestMapping, classMenuItem);
						
						if (methodMenuItem.menuName != null){
							MenuItemExt existing = allMenuItems.get(methodMenuItem.menuName).put(methodMenuItem.path, methodMenuItem);
							if (existing != null){
								log.error("Duplicated web menu item definitions in " + beanClass.getName() + "." + method.toGenericString() 
										+ ".\n\tExisting: " + existing + "\n\tCurrent: " + methodMenuItem);
							}
						}
					}
				}
			}
		}
		
		// construct menu trees
		menus = new HashMap<String, WebMenuItem>();
		menuItemPaths = new HashMap<String, Map<String, WebMenuItem>>();
		
		for (Map.Entry<String, Map<String, MenuItemExt>> menuItems: allMenuItems.entrySet()){
			String menuName = menuItems.getKey();
			Map<String, MenuItemExt> items = menuItems.getValue();
			WebMenuItem root = new WebMenuItem();
			root.title = menuName;			// for the root, set its title as menu name
			root.breadcrumbs = new ArrayList<WebMenuItem>(1);
			root.breadcrumbs.add(root);		// root is the first in breadcrumbs
			menus.put(menuName, root);
			menuItemPaths.put(menuName, new HashMap<String, WebMenuItem>());
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
			cleanUpMenuTree(root, menuName);
			log.info("Menu '" + menuName + "' loaded:\n" + root);
		}
	}
	
	/**
	 * Convert MenuItemExt to WebMenuItem, and clean up all data
	 * @param root
	 */
	protected void cleanUpMenuTree(WebMenuItem root, String menuName){
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
					menuItemPaths.get(menuName).put(itemExt.path, item);
				}
				item.breadcrumbs = new ArrayList<WebMenuItem>();
				if (rootBreadcrumbs != null){
					item.breadcrumbs.addAll(rootBreadcrumbs);
				}
				item.breadcrumbs.add(item);
				cleanUpMenuTree(item, menuName);
			}
		}
	}
	
	/**
	 * Get the menu tree by the menu's name
	 * @param menuName
	 * @return
	 */
	public WebMenuItem getMenu(String menuName){
		return menus.get(menuName);
	}
	
	/**
	 * Get the default menu tree
	 * @return
	 */
	public WebMenuItem getMenu(){
		return getMenu("");
	}
	
	/**
	 * Get the menu item by menu and path
	 * @param menuName
	 * @param path
	 * @return
	 */
	public WebMenuItem getMenuItem(String menuName, String path){
		try{
			return menuItemPaths.get(menuName).get(path);
		}catch(Exception e){
			log.error("Error when getting menu item for: menuName='" + menuName + "', path='" + path + "'");
			return null;
		}
	}
	
	/**
	 * Get the menu item by path in default menu
	 * @param path
	 * @return
	 */
	public WebMenuItem getMenuItem(String path){
		return getMenuItem("", path);
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
