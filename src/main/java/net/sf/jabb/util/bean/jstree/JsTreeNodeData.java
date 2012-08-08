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

import java.util.Collection;
import java.util.HashMap;
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
	 * For i18n, the entries of the Map are: title, language.
	 */
	protected Object data;
	protected Map<String, Object> attr;
	/**
	 * "closed" or "open", it should be null for those without child nodes"
	 */
	protected String state;
	protected Collection<JsTreeNodeData> children;
	
	public void setState(boolean isOpen){
		state = isOpen ? STATE_OPEN : STATE_CLOSED;
	}
	
	public boolean stateIsOpen(){
		return STATE_OPEN.equals(state);
	}
	
	public boolean stateIsClosed(){
		return STATE_CLOSED.equals(state);
	}
	
	public String dataString(){
		return data == null ? null : data.toString();
	}
	
	public void setAttr(String name, Object value){
		if (attr == null){
			attr = new HashMap<String, Object>();
		}
		attr.put(name, value);
	}
	
	public Object getAttr(String name){
		return attr == null ? null : attr.get(name);
	}
	
	public Object getAttrAsString(String name){
		return getAttr(name).toString();
	}
	
	public <T> T getAttr(String name, Class<T> claz){
		return attr == null ? null : claz.cast(attr.get(name));
	}
	
	public void setAttrId(Object id){
		setAttr("id", id);
	}
	
	public Object attrId(){
		return getAttr("id");
	}
	
	public String attrIdString(){
		Object id = attrId();
		return id == null ? null : id.toString();
	}
	
	public void setAttrRel(Object rel){
		setAttr("rel", rel);
	}
	
	public Object attrRel(){
		return getAttr("rel");
	}
	
	public String attrRelString(){
		Object rel = attrRel();
		return rel == null ? null : rel.toString();
	}
	
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
	public Collection<JsTreeNodeData> getChildren() {
		return children;
	}
	public void setChildren(Collection<JsTreeNodeData> children) {
		this.children = children;
	}

}
