/**
 * 
 */
package net.sf.jabb.util.web;

import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.ElementType;

/**
 * This annotation marks a method in Spring MVC to be associated with a menu item
 * @author james.hu
 *
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.METHOD})
@Inherited
public @interface WebMenu {
	/**
	 * Title of this menu item. It can be omitted, which means a menu item will 
	 * not be created for this method but the URL/path of this method will be
	 * used to associate it with an existing menu item. <p>
	 * For example, if GET /test already have menu item with title 'Test' defined,
	 * we can annotate POST /test as a menu item with an empty title. 
	 * @return	title of this menu item
	 */
	public String value() default "";
	
	/**
	 * URL overriding. It can be omitted, which means URL should 
	 * be read from Spring MVC RequestMapping annotation
	 * @return	URL to access this menu item.
	 */
	public String url() default "";
	
	/**
	 * Path of this menu item in the whole menu tree. 
	 * It can be omitted, which means that the path is the same as URL
	 * @return		path of this menu item in the whole menu tree
	 */
	public String path() default "";
	
	/**
	 * Display order of this item. Normally the item with smallest display order 
	 * will be shown first. 
	 * @return	the display order
	 */
	public int order() default 0;
	
	/**
	 * Name of the menu tree. There can be multiple menu trees in one application. 
	 * It can be omitted if it is okay to use an empty string as the menu name.
	 * @return	name of the menu tree
	 */
	public String menu() default "";
	
	/**
	 * Whether the menu item is dynamic or not. Normally a dynamic menu item will only
	 * be shown when current URL is associated with it.
	 * @return whether it is dynamic or not
	 */
	public boolean dynamic() default false;
	
	/**
	 * The authority associated with this menu item. Normally only users with authority granted
	 * will be able to see this menu item.
	 * @return the name of this authority
	 */
	public String authority() default "";

}
