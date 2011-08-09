/*
Copyright 2010-2011 Zhengmao HU (James)

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
 * Generic bean to contain result/response information.<br>
 * ͨ�õĴ�ŷ��ؽ����Bean��
 * 
 * @author Zhengmao HU (James)
 *
 */
public class GenericResult {
	/**
	 * Whether the request was processed successfully.<br>
	 * �Ƿ�ɹ�����������
	 */
	protected boolean successful;
	/**
	 * Detail of the error if there is any.<br>
	 * ������Ϣ������Բ������ɹ��������
	 */
	protected String errorMessage;
	/**
	 * Any further information.<be>
	 * ����һ������Ϣ��
	 */
	protected Object attachment;
	
	/**
	 * Constructor.<br>
	 * ����һ������ʵ����
	 * @param successful	Successful or not
	 * @param errorMsg		Error message
	 * @param att			Attachement
	 */
	public GenericResult(boolean successful, String errorMsg, Object att){
		this.successful = successful;
		this.errorMessage = errorMsg;
		this.attachment = att;
	}
	
	/**
	 * Constructor without the need of attachment parameter.
	 * 
	 * @param successful	Successful or not
	 * @param errorMsg		Error message
	 */
	public GenericResult(boolean successful, String errorMsg){
		this(successful, errorMsg, null);
	}

	public GenericResult(boolean successful){
		this(successful, null, null);
	}

	public GenericResult(){
	}
	
	@Override
	public String toString(){
		return "successful = " + successful + "\nerrorMessage = " + errorMessage + "\nattachment = " + attachment;
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
