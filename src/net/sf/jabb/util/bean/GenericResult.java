/*
Copyright 2010 Zhengmao HU (James)

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

package net.sf.jabb.util.bean;

/**
 * 一般的返回结果。
 * @author Zhengmao HU (James)
 *
 */
public class GenericResult {
	/**
	 * 是否操作成功。
	 */
	protected boolean successful;
	/**
	 * 出错消息（仅针对操作不成功的情况）
	 */
	protected String errorMessage;
	/**
	 * 附件对象。
	 */
	protected Object attachment;
	
	/**
	 * 创建一个对象实例。
	 * @param successful
	 * @param errorMsg
	 * @param att
	 */
	public GenericResult(boolean successful, String errorMsg, Object att){
		this.successful = successful;
		this.errorMessage = errorMsg;
		this.attachment = att;
	}
	
	public boolean isSuccessful() {
		return successful;
	}
	public void setSuccessful(boolean successful) {
		this.successful = successful;
	}
	public String getErrorMessage() {
		return errorMessage;
	}
	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}
	public Object getAttachment() {
		return attachment;
	}
	public void setAttachment(Object attachment) {
		this.attachment = attachment;
	}

}
