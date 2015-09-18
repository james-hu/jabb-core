/*
Copyright 2012 James Hu

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
 * The filter to send a specific status code when a request is received on a specific local port.
 * The message parameter controls how a message is sent along with the status code: 
 * null/nonexisting for using the requestURI; empty for using null, otherwise for using the message specified.
 * @author James Hu
 *
 */
public class DenyAccessToLocalPortFilter implements Filter {
	
	protected int port;
	protected int statusCode;
	protected String message;

	/* (non-Javadoc)
	 * @see javax.servlet.Filter#destroy()
	 */
	public void destroy() {
	}

	/* (non-Javadoc)
	 * @see javax.servlet.Filter#doFilter(javax.servlet.ServletRequest, javax.servlet.ServletResponse, javax.servlet.FilterChain)
	 */
	public void doFilter(ServletRequest request, ServletResponse response,
			FilterChain chain) throws IOException, ServletException {
		if (port == request.getLocalPort()){
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
		String portString = config.getInitParameter("port");
		if (!StringUtils.isNumeric(portString)){
			throw new ServletException("the port parameter must be a valid number: " + portString);
		}
		try{
			port = Integer.parseInt(portString);
		}catch(NumberFormatException e){
			throw new ServletException("the port parameter must be a valid number: " + portString);
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

}
