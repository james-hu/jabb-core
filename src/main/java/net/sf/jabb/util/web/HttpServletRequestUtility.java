/**
 * 
 */
package net.sf.jabb.util.web;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.jboss.util.Base64;

/**
 * Utility class for HttpServletRequest related functionalities.
 * @author James Hu (Zhengmao Hu)
 *
 */
public abstract class HttpServletRequestUtility {
	/**
	 * Get the user name in the basic authentication header
	 * @param request	the http servlet request
	 * @return	the user name, or null if not found
	 */
	static public String getBasicAuthUsername(HttpServletRequest request){
		String[] userAndPassword = getBasicAuthUsernameAndPassword(request);
		return userAndPassword == null || userAndPassword.length == 0 ? null : userAndPassword[0];
	}
	
	/**
	 * Get the user name and password passed in the basic authentication header.
	 * @param request	the http servlet request
	 * @return	user name and password, or null if not found. If there is no password, 
	 * 	the result may contain only one element, or two elements and the second element is null or empty.
	 */
	static public String[] getBasicAuthUsernameAndPassword(HttpServletRequest request){
		String authorization = request.getHeader("Authorization");
	    if (authorization != null && authorization.startsWith("Basic")) {
	    	try{
		        // Authorization: Basic base64credentials
		        String base64Credentials = authorization.substring("Basic".length()).trim();
		        String credentials = new String(Base64.decode(base64Credentials), "UTF-8");
		        // credentials = username:password
		        String[] values = StringUtils.split(credentials, ':');
		        return values;
	    	}catch(Exception e){
	    		// ignore
	    	}
	    }
	    return null;
	}

	/**
	 * Check to see if the request is from localhost
	 * @param request	the http request
	 * @return	true if it is from localhost, false if not.
	 */
	static public boolean isFromLocalhost(HttpServletRequest request){
		String addr = request.getRemoteAddr();
		return "0:0:0:0:0:0:0:1".equals(addr) || "::1".equals(addr)
				|| (addr != null && addr.startsWith("127."));
	}

}
