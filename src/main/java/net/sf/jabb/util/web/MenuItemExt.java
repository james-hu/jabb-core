package net.sf.jabb.util.web;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.springframework.web.bind.annotation.RequestMapping;

@SuppressWarnings("serial") 
class MenuItemExt extends WebMenuItem {
	String path;
	String menuName;
	int order;
	boolean dynamic;

	/**
	 * Construct an instance from annotations on method level.
	 * @param def
	 * @param mapping
	 */
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
			this.dynamic = def.dynamic();
		}else{
			this.menuName = "";
		}
		
		if (this.path == null || this.path.length() == 0){
			this.path = this.url;
		}
	}
	
	/**
	 * Construct an instance from annotations on both method and class level.
	 * @param def
	 * @param mapping
	 * @param classMenuItem		class level MenuItemExt
	 */
	MenuItemExt(WebMenu def, RequestMapping mapping, MenuItemExt classMenuItem){
		this (def, mapping);
		String baseMenuName = classMenuItem.menuName;
		String baseUrl = classMenuItem.getUrl() != null ? classMenuItem.getUrl() : "";
		String basePath = classMenuItem.path != null ? classMenuItem.path : "";

		if ("".equals(menuName)){
			menuName = baseMenuName;
		}
		url = (baseUrl + url);//.replace("//", "/");  // replace possible duplicated //
		path = (basePath + path);//.replace("//", "/");  // replace possible duplicated //
	}
	
	public WebMenuItem toWebMenuItem(){
		WebMenuItem result = new WebMenuItem();
		result.title = this.title;
		result.url = this.url;
		result.dynamic = this.dynamic;
		result.subMenu = this.subMenu;
		result.breadcrumbs = this.breadcrumbs;
		if (result.subMenu != null && result.subMenu.size() == 0){
			result.subMenu = null;
		}
		if (result.subMenu != null){
			result.url = null;				// if there is sub menu, then no url needed
		}else{
			// make the url look better by removing "//" from the beginning
			if (result.url.startsWith("//")){
				result.url = result.url.substring(1);
			}
		}
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