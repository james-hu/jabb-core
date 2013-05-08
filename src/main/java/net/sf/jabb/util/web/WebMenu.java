/**
 * 
 */
package net.sf.jabb.util.web;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.ElementType;

/**
 * This annotation marks a method in Spring MVC to be a menu item
 * @author james.hu
 *
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.METHOD})
public @interface WebMenu {
	/**
	 * Title of this menu item
	 * @return	title of this menu item
	 */
	public String value();
	
	/**
	 * URL overriding. Null means URL should be read from Spring MVC RequestMapping annotation
	 * @return	URL to access this menu item.
	 */
	public String url() default "";
	
	/**
	 * Path of this menu item in the whole menu tree. Null means that the path is the same as URL
	 * @return		path of this menu item in the whole menu tree
	 */
	public String path() default "";
	
	/**
	 * Display order of this item 
	 * @return	the display order
	 */
	public int order() default 0;
	
	/**
	 * Name of the menu tree. There could be multiple menu trees in one application. Default value is "DEFAULT"
	 * @return	name of the manu tree
	 */
	public String menu() default "";
}
