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
package net.sf.jabb.util.bean.jstree;

import java.util.List;
import java.util.Map;

/**
 * Response to JsTreeRequest, expected by jsTree as JSON data.
 * @author James Hu
 *
 */
public class JsTreeNodeData {
	static public final String STATE_CLOSED = "closed";
	static public final String STATE_OPEN = "open";
	/**
	 * Can be a String for non-i18n, or a Map<String, String> for i18n.
	 * For i18n, the keys of the Map are: title, language.
	 */
	protected Object data;
	protected Map<String, Object> attr;
	/**
	 * "closed" or "open", defaults to "closed"
	 */
	protected String state;
	protected List<JsTreeNodeData> children;
	
	
	public Object getData() {
		return data;
	}
	public void setData(Object data) {
		this.data = data;
	}
	public Map<String, Object> getAttr() {
		return attr;
	}
	public void setAttr(Map<String, Object> attr) {
		this.attr = attr;
	}
	public String getState() {
		return state;
	}
	public void setState(String state) {
		this.state = state;
	}
	public List<JsTreeNodeData> getChildren() {
		return children;
	}
	public void setChildren(List<JsTreeNodeData> children) {
		this.children = children;
	}

}
