/**
 * 
 */
package net.sf.jabb.magnolia.auth;

import java.io.IOException;
import java.util.Map;

import javax.security.auth.Subject;
import javax.security.auth.callback.Callback;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.callback.UnsupportedCallbackException;
import javax.security.auth.login.LoginException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import info.magnolia.jaas.sp.jcr.JCRAuthenticationModule;

/**
 * Utilizing container pre-authenticated user name.
 * @author james.hu
 *
 */
public class ContainerPreAuthJCRAuthenticationModule extends
		JCRAuthenticationModule {
	private static final long serialVersionUID = -2650427136884000105L;

	private static final Logger logger = LoggerFactory.getLogger(ContainerPreAuthJCRAuthenticationModule.class);
	
	protected boolean isPreAuthenticated = false;

	@Override
	public void initialize(Subject subject, CallbackHandler callbackHandler, Map sharedState, final Map options) {
		super.initialize(subject, callbackHandler, sharedState, options);
		
		TypeCallback typeCallback = new TypeCallbackImpl();
		Callback[] callbacks = new Callback[]{typeCallback};

		try {
			callbackHandler.handle(callbacks);
			if (ContainerPreAuthCallbackHandler.class.getName().equals(typeCallback.getType())){
				isPreAuthenticated = true;
			}else{
				isPreAuthenticated = false;
				logger.debug("The CallbackHandler is not for pre-authenticated user.");
			}
		} catch (Exception e) {
			logger.error("Error when getting CallbackHandler type.", e);
		}
	}

	@Override
	protected void matchPassword() throws LoginException {
		if (isPreAuthenticated){
			logger.debug("matchPassword() will not be called for pre-authenticated user: " + user.getName());
		}else{
			super.matchPassword();
		}
	}

}
