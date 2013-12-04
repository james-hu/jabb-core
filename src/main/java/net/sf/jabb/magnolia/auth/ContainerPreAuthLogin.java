/**
 * 
 */
package net.sf.jabb.magnolia.auth;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import info.magnolia.cms.security.SecuritySupportBase;
import info.magnolia.cms.security.auth.callback.CredentialsCallbackHandler;
import info.magnolia.cms.security.auth.callback.PlainTextCallbackHandler;
import info.magnolia.cms.security.auth.login.LoginHandlerBase;
import info.magnolia.cms.security.auth.login.LoginResult;

/**
 * Utilizing container (e.g. Tomcat) pre-authenticated user information.
 * 
 * @author James Hu
 *
 */
public class ContainerPreAuthLogin extends LoginHandlerBase {
	private static final Logger logger = LoggerFactory.getLogger(ContainerPreAuthLogin.class);
	

	/* (non-Javadoc)
	 * @see info.magnolia.cms.security.auth.login.LoginHandler#handle(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
	 */
	@Override
	public LoginResult handle(HttpServletRequest request, HttpServletResponse response) {
		String userName = request.getRemoteUser();
		if (userName != null && userName.trim().length() > 0) {
			LoginResult result;
			ContainerPreAuthCallbackHandler callbackHandler = new ContainerPreAuthCallbackHandler(userName);
			result = authenticate(callbackHandler, null);
			//result = new LoginResult(LoginResult.STATUS_SUCCEEDED);
			logger.debug("handle() called for user '" + userName 
					+ "', LoginResult.statues=" + result.getStatus() 
					+ ", LoginResult.subject=" + result.getSubject());
			
			/*
			logger.debug("using PlainTextCallbackHandler");
			CredentialsCallbackHandler callbackHandler2 = new PlainTextCallbackHandler(userName, "Test123Test".toCharArray());
			authenticate(callbackHandler2, SecuritySupportBase.DEFAULT_JAAS_LOGIN_CHAIN);
			LoginResult result = new LoginResult(LoginResult.STATUS_SUCCEEDED);
			*/
			return result;
		}else{
			logger.debug("handle() called but the user has not been authenticated by the container.");
		}
		return LoginResult.NOT_HANDLED;
	}

}
