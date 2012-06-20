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

/**
 * This class encapsulates json_data request from jsTree.
 * @author James Hu
 *
 */
public class JsTreeRequest {
	static public final String OP_GET_CHILDREN = "getChildren";
	static public final String OP_CREATE_NODE = "createNode";
	static public final String OP_REMOVE_NODE = "removeNode";
	static public final String OP_RENAME_NODE = "renameNode";
	static public final String OP_MOVE_NODE = "moveNode";
	static public final String OP_SEARCH = "search";
	
	protected String operation;
	/**
	 * for getChildren, removeNode, renameNode, moveNode
	 */
	protected String id;
	/**
	 * for getChildren
	 */
	protected boolean isRoot;
	/**
	 * for search
	 */
	protected String searchString;
	/**
	 * for createNode, moveNode
	 */
	protected int position;
	/**
	 * for createNode, renameNode
	 */
	protected String title;
	/**
	 * for createNode
	 */
	protected String type;
	/**
	 * for moveNode
	 */
	protected boolean isCopy;
	/**
	 * for createNode, moveNode
	 */
	protected String referenceId;
	/**
	 * for rename
	 */
	protected String language;
	
	
	public String getOperation() {
		return operation;
	}
	
	public void setOperation(String operation) {
		this.operation = operation;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public boolean getIsRoot() {
		return isRoot;
	}
	public void setIsRoot(boolean isRoot) {
		this.isRoot = isRoot;
	}
	public boolean isRoot() {
		return isRoot;
	}
	public void setRoot(boolean isRoot) {
		this.isRoot = isRoot;
	}
	public String getSearchString() {
		return searchString;
	}
	public void setSearchString(String searchString) {
		this.searchString = searchString;
	}
	public int getPosition() {
		return position;
	}
	public void setPosition(int position) {
		this.position = position;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public boolean isCopy() {
		return isCopy;
	}
	public void setCopy(boolean isCopy) {
		this.isCopy = isCopy;
	}
	public boolean getIsCopy() {
		return isCopy;
	}
	public void setIsCopy(boolean isCopy) {
		this.isCopy = isCopy;
	}
	public String getReferenceId() {
		return referenceId;
	}
	public void setReferenceId(String referenceId) {
		this.referenceId = referenceId;
	}

	public String getLanguage() {
		return language;
	}

	public void setLanguage(String language) {
		this.language = language;
	}
	
	
}
