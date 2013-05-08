/**
 * 
 */
package net.sf.jabb.stdr;

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

    /**
     * Set template parameters to an attribute of HTTP request.
     * @param request
     * @param parameters
     */
    static public void setParameters(ServletRequest request, Map<String, String> parameters){
    	request.setAttribute(TEMPLATE_PARAMETER_MAP, parameters);
    }
    
    /**
     * Get parameters from the attribute of HTTP request.
     * @param request
     * @return
     */
    @SuppressWarnings("unchecked")
	static public Map<String, String> getParameters(ServletRequest request){
    	return (Map<String, String>) request.getAttribute(TEMPLATE_PARAMETER_MAP);
    }
}
