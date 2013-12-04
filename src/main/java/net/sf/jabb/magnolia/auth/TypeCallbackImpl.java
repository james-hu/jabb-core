package net.sf.jabb.magnolia.auth;

public class TypeCallbackImpl implements TypeCallback {
	
	private String type;

	public TypeCallbackImpl() {
	}

	@Override
	public void setType(String type) {
		this.type = type;
	}

	@Override
	public String getType() {
		return this.type;
	}

}
