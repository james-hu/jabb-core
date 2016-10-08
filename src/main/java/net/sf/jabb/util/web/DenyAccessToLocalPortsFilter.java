/*
Copyright 2015 James Hu

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
*/
package net.sf.jabb.util.web;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;


/**
 * The filter to deny requests when the request is or is not received from some specified local ports.
 * <br>Servlet filter initialization parameters:
 * <ul>
 * 	<li>mode: either 'allow' or 'deny'. In 'allow' mode any access to the port not specified by the ports parameter is denied.
 * 		In 'deny' mode any access to the port specified by the ports parameter is denied.</li>
 * 	<li>ports: comma separated port numbers</li>
 * 	<li>statusCode: the HTTP status code to return when the request is denied</li>
 * 	<li>message: the message to send when the request is denied. 
 * 		It controls how a message is sent along with the status code: 
 * 		null/non-existing for using the requestURI; empty for using null, otherwise for using the message specified.</li>
 * </ul>
 * @author James Hu
 *
 */
public class DenyAccessToLocalPortsFilter implements Filter {
	static public final int MODE_ALLOW = 1;
	static public final int MODE_DENY = 2;
	protected int mode;
	protected int[] ports;
	protected int statusCode;
	protected String message;

	/* (non-Javadoc)
	 * @see javax.servlet.Filter#destroy()
	 */
	public void destroy() {
	}
	
	protected boolean portHit(int port){
		for (int p: ports){
			if (p == port){
				return true;
			}
		}
		return false;
	}

	/* (non-Javadoc)
	 * @see javax.servlet.Filter#doFilter(javax.servlet.ServletRequest, javax.servlet.ServletResponse, javax.servlet.FilterChain)
	 */
	public void doFilter(ServletRequest request, ServletResponse response,
			FilterChain chain) throws IOException, ServletException {
		int localPort = request.getLocalPort();
		if (mode == MODE_ALLOW && !portHit(localPort) ||
				mode == MODE_DENY && portHit(localPort)){
			if (message == null){
				((HttpServletResponse) response).sendError(statusCode, ((HttpServletRequest)request).getRequestURI());
			}else if (message.length() == 0){
				((HttpServletResponse) response).sendError(statusCode);
			}else{
				((HttpServletResponse) response).sendError(statusCode, message);
			}
		}else{
			chain.doFilter(request, response);
		}
	}

	/* (non-Javadoc)
	 * @see javax.servlet.Filter#init(javax.servlet.FilterConfig)
	 */
	public void init(FilterConfig config) throws ServletException {
		String portsString = config.getInitParameter("ports");
		if (portsString == null){
			throw new ServletException("the ports parameter must be defined");
		}
		String[] portsArray = portsString.split("[, ]");
		if (portsArray.length == 0){
			throw new ServletException("the ports parameter must contains comma separated port numbers: " + portsString);
		}
		ports = new int[portsArray.length];
		for (int i = 0; i < portsArray.length; i ++){
			String pStr = portsArray[i];
			if (!StringUtils.isNumeric(pStr)){
				throw new ServletException("the port number is not valid: " + pStr);
			}
			try{
				ports[i] = Integer.parseInt(pStr);
			}catch(NumberFormatException e){
				throw new ServletException("the port number is not valid: " + pStr);
			}
		}

		String modeString = config.getInitParameter("mode");
		if (StringUtils.isBlank(modeString)){
			throw new ServletException("the mode parameter must be defined");
		}
		modeString = modeString.trim();
		if ("allow".equalsIgnoreCase(modeString)){
			mode = MODE_ALLOW;
		}else if ("deny".equalsIgnoreCase(modeString)){
			mode = MODE_DENY;
		}else{
			throw new ServletException("the mode parameter must be either 'allow' or 'deny' : " + modeString);
		}
		
		String statusCodeString = config.getInitParameter("statusCode");
		if (!StringUtils.isNumeric(statusCodeString)){
			throw new ServletException("the statusCode parameter must be a valid number: " + statusCodeString);
		}
		try{
			statusCode = Integer.parseInt(statusCodeString);
		}catch(NumberFormatException e){
			throw new ServletException("the statusCode parameter must be a valid number: " + statusCodeString);
		}
		
		message = config.getInitParameter("message");
	}

	public int getMode() {
		return mode;
	}

	public void setMode(int mode) {
		this.mode = mode;
	}

	public int[] getPorts() {
		return ports;
	}

	public void setPorts(int[] ports) {
		this.ports = ports;
	}

	public int getStatusCode() {
		return statusCode;
	}

	public void setStatusCode(int statusCode) {
		this.statusCode = statusCode;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

}
