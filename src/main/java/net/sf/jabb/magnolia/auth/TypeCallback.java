package net.sf.jabb.magnolia.auth;

import javax.security.auth.callback.Callback;

public interface TypeCallback extends Callback {
	void setType(String type);
	String getType();
}
