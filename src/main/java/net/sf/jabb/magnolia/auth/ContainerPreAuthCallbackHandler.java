/**
 * 
 */
package net.sf.jabb.magnolia.auth;

import java.io.IOException;

import javax.security.auth.callback.Callback;
import javax.security.auth.callback.UnsupportedCallbackException;

import info.magnolia.cms.security.auth.callback.CredentialsCallbackHandler;

/**
 * It holds pre-authenticated remote user name.
 * @author james.hu
 *
 */
public class ContainerPreAuthCallbackHandler extends CredentialsCallbackHandler {

	/**
	 * @param name
	 * @param pswd
	 */
	public ContainerPreAuthCallbackHandler(String name) {
		super(name, null);
	}

	/**
	 * @param name
	 * @param pswd
	 * @param realm
	 */
	public ContainerPreAuthCallbackHandler(String name, String realm) {
		super(name, null, realm);
	}
	
	@Override
	public void handle(Callback[] callbacks) throws IOException, UnsupportedCallbackException {
		super.handle(callbacks);
		for (int i = 0; i < callbacks.length; i++) {
			if (callbacks[i] instanceof TypeCallback) {
				((TypeCallback) callbacks[i]).setType(this.getClass().getName());
			}
		}
	}
}
