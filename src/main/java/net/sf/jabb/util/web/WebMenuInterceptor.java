/**
 * 
 */
package net.sf.jabb.util.web;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.jabb.stdr.StdrUtil;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

/**
 * To find out current WebMenuItem and set to request context, and also to set WebApplicationConfiguration into request context. 
 * Values set by this interceptor can be retrieved by STDR's &lt;stdr:set&gt; tag.
 * @author james.hu
 *
 */
public class WebMenuInterceptor extends HandlerInterceptorAdapter {
	private static final Log log = LogFactory.getLog(WebMenuInterceptor.class);
	
	protected static WebMenuItem NOT_FOUND = new WebMenuItem();	// use this instance to represent "found a null value"
	
	protected WebApplicationConfiguration webApplicationConfiguration;
	
	protected Map<Object, WebMenuItem> webMenuItemCache 
		= new ConcurrentHashMap<Object, WebMenuItem>();

	@Override
	public void postHandle(HttpServletRequest request,
			HttpServletResponse response, Object handler,
			ModelAndView modelAndView) throws Exception {

		WebMenuItem menuItem = null;
		// try to get from cache first
		menuItem = webMenuItemCache.get(handler);
		
		if (menuItem == null){
			menuItem = findMenuItem(handler);
			if (menuItem == null){
				menuItem = NOT_FOUND;
			}
			webMenuItemCache.put(handler, menuItem);
		}
		
		if (menuItem != NOT_FOUND){
			StdrUtil.getParameters(request).put(StdrUtil.CURRENT_MENU_ITEM_PARAMETER, menuItem);
			if (log.isDebugEnabled()){
				log.debug("WebMenuItem set into request for " + handler + " : " + menuItem);
			}
		}
	}
	
	protected WebMenuItem findMenuItem(Object handler){
		WebMenuItem menuItem = NOT_FOUND;
		if (handler instanceof HandlerMethod){
			HandlerMethod handlerMethod = (HandlerMethod) handler;
			
			RequestMapping methodRequestMapping = handlerMethod.getMethodAnnotation(RequestMapping.class);
			WebMenu methodWebMenu = handlerMethod.getMethodAnnotation(WebMenu.class);
			
			if (methodRequestMapping != null && methodWebMenu != null){
				Class<?> controllerClass = handlerMethod.getBeanType();	// in case the method is declared in parent class, the child class is got here

				RequestMapping classRequestMapping = AnnotationUtils.findAnnotation(controllerClass, RequestMapping.class);
				WebMenu classWebMenu = AnnotationUtils.findAnnotation(controllerClass, WebMenu.class);
				
				
				MenuItemExt classMenuItem = new MenuItemExt(classWebMenu, classRequestMapping);

				MenuItemExt methodMenuItem = new MenuItemExt(methodWebMenu, methodRequestMapping, classMenuItem);
				
				String menuName = methodMenuItem.menuName;
				String menuPath = methodMenuItem.path;
				
				menuItem = webApplicationConfiguration.getMenuItem(menuName, menuPath);

				if (menuItem != null){
					log.info("WebMenuItem found for " 
							+ controllerClass.getName() + "." + handlerMethod.getMethod().getName() + " : " + menuItem);
				}
			}
			
		}else{
			if (log.isDebugEnabled()){
				log.debug("The handler is not of type HandlerMethod. WebMenuInterceptor ignores it.");
			}
		}
		
		return menuItem;
	}

	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
		    throws Exception {
		StdrUtil.getParameters(request).put(StdrUtil.WEB_APP_CONFIG_PARAMETER, webApplicationConfiguration);
		return true;
	}


	public void setWebApplicationConfiguration(
			WebApplicationConfiguration webApplicationConfiguration) {
		this.webApplicationConfiguration = webApplicationConfiguration;
	}

}
