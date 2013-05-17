/**
 * 
 */
package net.sf.jabb.util.web;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.jabb.stdr.StdrUtil;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

/**
 * To find out current WebMenuItem and set to request context, and also to set WebApplicationConfiguration into request context. 
 * Values set by this interceptor can be retrieved by STDR's &lt;stdr:set&gt; tag.
 * @author james.hu
 *
 */
public class WebMenuInterceptor extends HandlerInterceptorAdapter {
	private static final Log log = LogFactory.getLog(HandlerInterceptor.class);
	
	protected WebApplicationConfiguration webApplicationConfiguration;

	@Override
	public void postHandle(HttpServletRequest request,
			HttpServletResponse response, Object handler,
			ModelAndView modelAndView) throws Exception {
		
		if (handler instanceof HandlerMethod){
			HandlerMethod handlerMethod = (HandlerMethod) handler;
			
			RequestMapping methodRequestMapping = handlerMethod.getMethodAnnotation(RequestMapping.class);
			WebMenu methodWebMenu = handlerMethod.getMethodAnnotation(WebMenu.class);
			
			if (methodRequestMapping != null && methodWebMenu != null){
				Class<?> controllerClass = handlerMethod.getMethod().getDeclaringClass();
				
				RequestMapping classRequestMapping = controllerClass.getAnnotation(RequestMapping.class);
				WebMenu classWebMenu = controllerClass.getAnnotation(WebMenu.class);
				MenuItemExt classMenuItem = new MenuItemExt(classWebMenu, classRequestMapping);

				MenuItemExt methodMenuItem = new MenuItemExt(methodWebMenu, methodRequestMapping, classMenuItem);
				
				String menuName = methodMenuItem.menuName;
				String menuPath = methodMenuItem.path;
				
				WebMenuItem menuItem = webApplicationConfiguration.getMenuItem(menuName, menuPath);
				StdrUtil.getParameters(request).put(StdrUtil.CURRENT_MENU_ITEM_PARAMETER, menuItem);
				log.debug("WebMenuItem set into request for " 
						+ controllerClass.getName() + "." + handlerMethod.getMethod().getName() + " : " + menuItem);
			}
		}else{
			log.error("The handler is not of type HandlerMethod. WebMenuInterceptor skipped processing.");
		}
		
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
