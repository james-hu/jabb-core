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
		if (title == null && url == null){	// it is root node
			if (subMenu != null && subMenu.size() > 0){
				for (WebMenuItem subItem: subMenu){
					sb.append(subItem.toString());
					sb.append('\n');
				}
			}
		}else{
			sb.append('"').append(title).append('"');
			sb.append('(').append(url).append(')');
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


}
