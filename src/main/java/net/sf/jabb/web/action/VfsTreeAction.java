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
package net.sf.jabb.web.action;

import java.text.DateFormat;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import net.sf.jabb.util.bean.jstree.JsTreeNodeData;
import net.sf.jabb.util.bean.jstree.JsTreeRequest;
import net.sf.jabb.util.bean.jstree.JsTreeResult;
import net.sf.jabb.util.vfs.VfsUtility;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.commons.vfs2.AllFileSelector;
import org.apache.commons.vfs2.FileContent;
import org.apache.commons.vfs2.FileObject;
import org.apache.commons.vfs2.FileSystemException;
import org.apache.commons.vfs2.FileSystemManager;
import org.apache.commons.vfs2.FileSystemOptions;
import org.apache.commons.vfs2.FileType;
import org.apache.commons.vfs2.NameScope;

import com.opensymphony.xwork2.ActionSupport;
import com.opensymphony.xwork2.ModelDriven;

/**
 * The action class to manipulate commons-vfs files.
 * @author James Hu
 *
 */
public class VfsTreeAction extends ActionSupport  implements ModelDriven<JsTreeRequest> {
	private static final long serialVersionUID = 8621358494729612212L;
	
	private static final Log log = LogFactory.getLog(VfsTreeAction.class);
	
	protected JsTreeRequest requestData;
	protected Object responseData;
	
	protected FileSystemOptions fsOptions;
	protected String rootPath;
	protected String rootNodeName;
	protected boolean sortByName = true;
	protected boolean folderFirst = true;
	
	public VfsTreeAction(){
		requestData = new JsTreeRequest();
	}

	@Override
	public JsTreeRequest getModel() {
		return requestData;
	}
	
	protected void normalizeTreeRequest(){
		if (requestData.getIsRoot() == null){
			String id = requestData.getId();
			requestData.setIsRoot(StringUtils.isEmpty(id) || "0".equals(id) || "-1".equals(id));
		}
		if (requestData.isCopy() == null){
			requestData.setCopy(Boolean.FALSE);
		}
	}
	
	protected List<JsTreeNodeData> getChildNodes(FileObject rootFile, String parentPath) throws FileSystemException{
		List<JsTreeNodeData> childNodes = new LinkedList<JsTreeNodeData>();
		FileObject parent = null;
		if (requestData.isRoot()){
			parent = rootFile;
		}else{
			parent = rootFile.resolveFile(parentPath, NameScope.DESCENDENT_OR_SELF);
		}
		for (FileObject child: parent.getChildren()){
			JsTreeNodeData childNode = populateTreeNodeData(rootFile, child);
			childNodes.add(childNode);
		}
		if (sortByName || folderFirst){
			Collections.sort(childNodes, new Comparator<JsTreeNodeData>(){

				@Override
				public int compare(JsTreeNodeData o1, JsTreeNodeData o2) {
					int result = 0;
					if (folderFirst){
						String t1 = o1.getAttr().get("rel").toString();
						String t2 = o2.getAttr().get("rel").toString();
						if (t1.equalsIgnoreCase(t2)){
							result = 0;
						}else if ("file".equalsIgnoreCase(t2)){
							result = -1;
						}else if ("file".equalsIgnoreCase(t1)){
							result = 1;
						}else{
							result = t1.compareToIgnoreCase(t2);
						}
					}
					if (result == 0 && sortByName){
						String n1 = o1.getData().toString();
						String n2 = o2.getData().toString();
						result = n1.compareToIgnoreCase(n2);
					}
					return result;
				}
				
			});
		}
		return childNodes;
	}
	
	/**
	 * Populate a node.
	 * @param root		Relative root directory.
	 * @param file		The file object.
	 * @return	The node data structure which presents the file.
	 * @throws FileSystemException
	 */
	protected JsTreeNodeData populateTreeNodeData(FileObject root, FileObject file) throws FileSystemException {
		boolean noChild = true;
		FileType type = file.getType();
		if (type.equals(FileType.FOLDER) || type.equals(FileType.FILE_OR_FOLDER)){
			noChild = file.getChildren().length == 0;
		}
		String relativePath = root.getName().getRelativeName(file.getName());
		return populateTreeNodeData(file, noChild, relativePath);
	}	
	/**
	 * It transforms FileObject into JsTreeNodeData.
	 * @param file  the file whose information will be encapsulated in the node data structure.
	 * @return	The node data structure which presents the file.
	 * @throws FileSystemException 
	 */
	protected JsTreeNodeData populateTreeNodeData(FileObject file, boolean noChild, String relativePath) throws FileSystemException {
		JsTreeNodeData node = new JsTreeNodeData();

		String baseName = file.getName().getBaseName();
		FileContent content = file.getContent();
		FileType type = file.getType();
		
		node.setData(baseName);

		Map<String, Object> attr = new HashMap<String, Object>();
		node.setAttr(attr);
		attr.put("id", relativePath);
		attr.put("rel", type.getName());
		attr.put("fileType", type.getName());
		if (content != null){
			long fileLastModifiedTime = file.getContent().getLastModifiedTime();
			attr.put("fileLastModifiedTime", fileLastModifiedTime);
			attr.put("fileLastModifiedTimeForDisplay", DateFormat.getDateTimeInstance().format(new Date(fileLastModifiedTime)));
			if (file.getType() != FileType.FOLDER){
				attr.put("fileSize", content.getSize());
				attr.put("fileSizeForDisplay", FileUtils.byteCountToDisplaySize(content.getSize()));
			}
		}

		// these fields should not appear in JSON for leaf nodes
		if (!noChild){
			node.setState(JsTreeNodeData.STATE_CLOSED);
		}
		return node;
	}
	
	/**
	 * AJAX tree functions
	 */
	public String execute(){
		normalizeTreeRequest();
		JsTreeResult result = new JsTreeResult();
		
		final AllFileSelector ALL_FILES = new AllFileSelector();
		FileSystemManager fsManager = null;
		FileObject rootFile = null;
		FileObject file = null;
		FileObject referenceFile = null;
		try{
			fsManager = VfsUtility.getManager();
			rootFile = fsManager.resolveFile(rootPath, fsOptions);
			
			if (JsTreeRequest.OP_GET_CHILDREN.equalsIgnoreCase(requestData.getOperation())){
				String parentPath = requestData.getId();
				List<JsTreeNodeData> nodes = null;
				try{
					nodes = getChildNodes(rootFile, parentPath);
					if (requestData.isRoot() && rootNodeName != null){ // add root node
						JsTreeNodeData rootNode = new JsTreeNodeData();
						rootNode.setData(rootNodeName);
						Map<String, Object> attr = new HashMap<String, Object>();
						rootNode.setAttr(attr);
						attr.put("id", ".");
						attr.put("rel", "root");
						attr.put("fileType", FileType.FOLDER.toString());
						rootNode.setChildren(nodes);
						rootNode.setState(JsTreeNodeData.STATE_OPEN);
						nodes = new LinkedList<JsTreeNodeData>();
						nodes.add(rootNode);
					}
				}catch(Exception e){
					log.error("Cannot get child nodes for: " + parentPath, e);
					nodes = new LinkedList<JsTreeNodeData>();
				}
				responseData = nodes;
			} else if (JsTreeRequest.OP_REMOVE_NODE.equalsIgnoreCase(requestData.getOperation())){
				String path = requestData.getId();
				try{
					file = rootFile.resolveFile(path, NameScope.DESCENDENT);
					boolean wasDeleted = false;
					if (file.getType() == FileType.FILE){
						wasDeleted = file.delete();
					}else{
						wasDeleted = file.delete(ALL_FILES) > 0;
					}
					result.setStatus(wasDeleted);
				} catch (Exception e){
					result.setStatus(false);
					log.error("Cannot delete: " + path, e);
				}
				responseData = result;
			} else if (JsTreeRequest.OP_CREATE_NODE.equalsIgnoreCase(requestData.getOperation())){
				String parentPath = requestData.getReferenceId();
				String name = requestData.getTitle();
				try{
					referenceFile = rootFile.resolveFile(parentPath, NameScope.DESCENDENT_OR_SELF);
					file = referenceFile.resolveFile(name, NameScope.CHILD);
					file.createFolder();
					result.setStatus(true);
					result.setId(rootFile.getName().getRelativeName(file.getName()));
				} catch (Exception e){
					result.setStatus(false);
					log.error("Cannot create folder '" + name + "' under '" + parentPath + "'", e);
				}
				responseData = result;
			} else if (JsTreeRequest.OP_RENAME_NODE.equalsIgnoreCase(requestData.getOperation())){
				String path = requestData.getId();
				String name = requestData.getTitle();
				try{
					referenceFile = rootFile.resolveFile(path, NameScope.DESCENDENT);
					file = referenceFile.getParent().resolveFile(name, NameScope.CHILD);
					referenceFile.moveTo(file);
					result.setStatus(true);
				}catch(Exception e){
					result.setStatus(false);
					log.error("Cannot rename '" + path + "' to '" + name + "'", e);
				}
				responseData = result;
			} else if (JsTreeRequest.OP_MOVE_NODE.equalsIgnoreCase(requestData.getOperation())){
				String newParentPath = requestData.getReferenceId();
				String originalPath = requestData.getId();
				try{
					referenceFile = rootFile.resolveFile(originalPath, NameScope.DESCENDENT);
					file = rootFile.resolveFile(newParentPath, NameScope.DESCENDENT_OR_SELF)
							.resolveFile(referenceFile.getName().getBaseName(), NameScope.CHILD);
					if (requestData.isCopy()){
						file.copyFrom(referenceFile, ALL_FILES);
					}else{
						referenceFile.moveTo(file);
					}
					result.setStatus(true);
				}catch(Exception e){
					result.setStatus(false);
					log.error("Cannot move '" + originalPath + "' to '" + newParentPath + "'", e);
				}
				responseData = result;
			}
		} catch (FileSystemException e) {
			log.error("Cannot perform file operation.", e);
		}finally{
			VfsUtility.close(fsManager, file, referenceFile, rootFile);
		}
		return SUCCESS;
	}

	public String getRootPath() {
		return rootPath;
	}

	public void setRootPath(String rootPath) {
		this.rootPath = rootPath;
	}

	public String getRootNodeName() {
		return rootNodeName;
	}

	public void setRootNodeName(String rootNodeName) {
		this.rootNodeName = rootNodeName;
	}

	public Object getResponseData() {
		return responseData;
	}

	public void setResponseData(Object responseData) {
		this.responseData = responseData;
	}

	public FileSystemOptions getFsOptions() {
		return fsOptions;
	}

	public void setFsOptions(FileSystemOptions fsOptions) {
		this.fsOptions = fsOptions;
	}

	public boolean isSortByName() {
		return sortByName;
	}

	public void setSortByName(boolean sortByName) {
		this.sortByName = sortByName;
	}

	public boolean isFolderFirst() {
		return folderFirst;
	}

	public void setFolderFirst(boolean folderFirst) {
		this.folderFirst = folderFirst;
	}

}
