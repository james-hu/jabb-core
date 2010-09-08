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

package net.sf.jabb.util.text.word;


/**
 * �ʵ�������Ĵ�����
 * <p>
 * Chinese word defined in dictionary.
 * 
 * @author Zhengmao HU (James)
 *
 */
public class Word {
	/**
	 * �����ʻ�<br>Normal word
	 */
	public static final int TYPE_NORMAL = 1;
	/**
	 * �����޳�Ȼ���ٽ��зִʴ�����ַ�
	 * <br>Characters that should be removed before word identifying
	 */
	public static final int TYPE_IGNORE = 2;
	/**
	 * �����֮��ķָ��ַ�
	 * <br>Characters that separate adjacent words
	 */
	public static final int TYPE_SEPARATOR = 4;
	/**
	 * ��Ҫ��ȡ�����Ĺؼ���
	 * <br>Keywords that need to be identified
	 */
	public static final int TYPE_KEYWORD = 8;
	
	private String word;
	private int types = 0;
	private Object keywordAttachment;
	
	public Word(){
		
	}
	
	/**
	 * ����һ��ʵ��������������һ��ʵ���Ŀ�����
	 * ע�⣬keywordAttachment���ᱻ���������ᱻ���á�
	 * <p>
	 * Create an instance which is a copy of another instance.
	 * Note that keywordAttachment will not be copied, it will be referenced.
	 * 
	 * @param original
	 */
	public Word(Word original){
		this.word = original.word;  // ����һ��String��ʵ������������
		this.types = original.types;
		this.keywordAttachment = original.keywordAttachment;
	}
	
	
	/**
	 * @return ��������ʵ��ַ���
	 * 		<br>String that represents the word
	 */
	public String getWord() {
		return word;
	}
	/**
	 * @param word ��������ʵ��ַ���
	 * 		<br>String that represents the word
	 */
	public void setWord(String word) {
		this.word = word;
	}
	
	/**
	 * ��־�����������ĳ�����͡�ע��ͬһ����������ͬʱ���ڶ������͡�
	 * <p>
	 * Set the flag to indicate that this word belongs to a specified type.
	 * Note that one word can belong to many types in one time.
	 * @param type	����<br>the type
	 */
	public void setType(int type){
		types |= type;
	}

	/**
	 * ��־�������������ĳ�����͡�
	 * <p>
	 * Clear the flag to indicate this word does not belong to a specified type.
	 * @param type	����<br>the type
	 */
	public void clearType(int type){
		types &= ~type;
	}
	
	/**
	 * ��ʾ��������������κ�һ�����͡�
	 * <p>
	 * Clear all flags to indicate that this word does not belong to any type.
	 */
	public void clearTypes(){
		types = 0;
	}
	
	/**
	 * @return the types
	 */
	public int getTypes() {
		return types;
	}
	/**
	 * @param types the types to set
	 */
	public void setTypes(int types) {
		this.types = types;
	}
	
	/**
	 * @return ���йؼ���ƥ��ʱ���������Ӧ�ĸ�������
	 * 		<br>Attachment object associated with this word when performing keyword matching 
	 */
	public Object getKeywordAttachment() {
		return keywordAttachment;
	}
	/**
	 * @param keywordAttachment ���йؼ���ƥ��ʱ���������Ӧ�ĸ�������
	 * 		<br>Attachment object associated with this word when performing keyword matching 
	 */
	public void setKeywordAttachment(Object keywordAttachment) {
		this.keywordAttachment = keywordAttachment;
	}
	

}
