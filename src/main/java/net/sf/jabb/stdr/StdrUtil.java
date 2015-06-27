/**
 * 
 */
package net.sf.jabb.stdr;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletRequest;

/**
 * @author James Hu
 *
 */
public class StdrUtil {
    /**
     * The attribute name in request context to pass the parameters: net_sf_jabb_stdr_templateParameterMap
     */
    static public final String TEMPLATE_PARAMETER_MAP = "net_sf_jabb_stdr_templateParameterMap";
    static public final String URL_PREFIX_PARAMETER = "net_sf_jabb_stdr_urlPrefixParameter";
    static public final String URL_POSTFIX_PARAMETER = "net_sf_jabb_stdr_urlPostfixParameter";
    static public final String WEB_APP_CONFIG_PARAMETER = "net_sf_jabb_stdr_webAppConfigParameter";
    static public final String CURRENT_MENU_ITEM_PARAMETER = "net_sf_jabb_stdr_currentMenuItemParameter";
    static public final String TYPE_WEB_APP_CONFIG = "WebAppConfig";
    static public final String TYPE_CURRENT_MENU_ITEM = "CurrentMenuItem";
    static public final String TYPE_SPRING_BEAN_PREFIX = "SpringBean:";
    static public final String PAGE_ATTR_NAME_PREFIX_FOR_SPRING_BEAN = "net_sf_jabb_stdr_springBean_";

    /**
     * Get parameters from the attribute of servlet request.
     * @param request	the servlet request
     * @return	StdrUtil template parameters retrieved from request context
     */
    @SuppressWarnings("unchecked")
	static public Map<String, Object> getParameters(ServletRequest request){
    	Map<String, Object> params = (Map<String, Object>) request.getAttribute(TEMPLATE_PARAMETER_MAP);
    	if (params == null){							// no need to check thread-safe?
    		params = new HashMap<String, Object>();
    		request.setAttribute(TEMPLATE_PARAMETER_MAP, params);
    	}
    	return params;
    }
}
