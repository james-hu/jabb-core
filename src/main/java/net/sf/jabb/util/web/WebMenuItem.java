/**
 * 
 */
package net.sf.jabb.util.web;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Data structure that holds menu item information.
 * @author james.hu
 *
 */
public class WebMenuItem implements Serializable, Comparable<WebMenuItem>{
	private static final long serialVersionUID = -591680373175438668L;

	protected String title;
	protected String url;
	protected boolean dynamic;
	protected String authority;
	protected List<WebMenuItem> subMenu;
	protected List<WebMenuItem> breadcrumbs;
	
	/**
	 * Add a MenuItem as the last one in its sub-menu.
	 * @param item
	 */
	public void addSubItem(WebMenuItem item){
		if (subMenu == null){
			subMenu = new ArrayList<WebMenuItem>();
		}
		subMenu.add(item);
	}
	
	@Override
	public String toString(){
		StringBuilder sb = new StringBuilder();
		if (breadcrumbs == null || breadcrumbs.get(0) == this){	// it is root node
			if (subMenu != null && subMenu.size() > 0){
				for (WebMenuItem subItem: subMenu){
					sb.append(subItem.toString());
					sb.append('\n');
				}
			}
		}else{
			sb.append('"').append(title).append('"');
			sb.append('(').append(url).append(')');
			if (dynamic){
				sb.append(" [Dynamic]");
			}
			if (authority != null && authority.length() > 0){
				sb.append(" {" + authority + "}");
			}
			if (subMenu != null && subMenu.size() > 0){
				sb.append('\n');
				for (WebMenuItem subItem: subMenu){
					sb.append("  ");
					sb.append(subItem.toString().replaceAll("\n", "\n  "));
					sb.append('\n');
				}
			}
		}
		if (sb.charAt(sb.length() - 1) == '\n'){	// last '\n' needs to be removed
			sb.setLength(sb.length() - 1);
		}
		return sb.toString();
	}
	
	@Override
	public int compareTo(WebMenuItem o) {
		// subclass will handle
		return 0;
	}
	
	/**
	 * Get the highest level of menu items.
	 * @return
	 */
	public List<WebMenuItem> getTopMenuItems(){
		return breadcrumbs.get(0).subMenu;
	}
	
	/**
	 * Is this menu item a leaf node in the menu tree?
	 * @return
	 */
	public boolean isLeaf(){
		return subMenu == null || subMenu.size() == 0;
	}

	/**
	 * Is this menu item a leaf node in the menu tree?
	 * @return
	 */
	public boolean getIsLeaf(){
		return isLeaf();
	}

	public String getTitle() {
		return title;
	}

	public String getUrl() {
		return url;
	}

	public List<WebMenuItem> getSubMenu() {
		return subMenu;
	}

	public List<WebMenuItem> getBreadcrumbs() {
		return breadcrumbs;
	}

	public void setBreadcrumbs(List<WebMenuItem> breadcrumbs) {
		this.breadcrumbs = breadcrumbs;
	}

	public boolean isDynamic() {
		return dynamic;
	}

	public void setDynamic(boolean dynamic) {
		this.dynamic = dynamic;
	}

	public String getAuthority() {
		return authority;
	}

	public void setAuthority(String authority) {
		this.authority = authority;
	}


}
