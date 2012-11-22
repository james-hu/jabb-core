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
 * 词典里的中文词条。
 * <p>
 * Chinese word defined in dictionary.
 * 
 * @author Zhengmao HU (James)
 *
 */
public class Word {
	/**
	 * 正常词汇<br>Normal word
	 */
	public static final int TYPE_NORMAL = 1;
	/**
	 * 需先剔除然后再进行分词处理的字符
	 * <br>Characters that should be removed before word identifying
	 */
	public static final int TYPE_IGNORE = 2;
	/**
	 * 词与词之间的分隔字符
	 * <br>Characters that separate adjacent words
	 */
	public static final int TYPE_SEPARATOR = 4;
	/**
	 * 需要提取出来的关键词
	 * <br>Keywords that need to be identified
	 */
	public static final int TYPE_KEYWORD = 8;
	
	private String word;
	private int types = 0;
	private Object keywordAttachment;
	
	public Word(){
		
	}
	
	/**
	 * 创建一个实例，其内容是另一个实例的拷贝。
	 * 注意，keywordAttachment不会被拷贝，它会被引用。
	 * <p>
	 * Create an instance which is a copy of another instance.
	 * Note that keywordAttachment will not be copied, it will be referenced.
	 * 
	 * @param original
	 */
	public Word(Word original){
		this.word = original.word;  // 共用一个String的实例不会有问题
		this.types = original.types;
		this.keywordAttachment = original.keywordAttachment;
	}
	
	
	/**
	 * @return 代表这个词的字符串
	 * 		<br>String that represents the word
	 */
	public String getWord() {
		return word;
	}
	/**
	 * @param word 代表这个词的字符串
	 * 		<br>String that represents the word
	 */
	public void setWord(String word) {
		this.word = word;
	}
	
	/**
	 * 标志这个词条属于某种类型。注意同一个词条可能同时属于多种类型。
	 * <p>
	 * Set the flag to indicate that this word belongs to a specified type.
	 * Note that one word can belong to many types in one time.
	 * @param type	类型<br>the type
	 */
	public void setType(int type){
		types |= type;
	}

	/**
	 * 标志这个词条不属于某种类型。
	 * <p>
	 * Clear the flag to indicate this word does not belong to a specified type.
	 * @param type	类型<br>the type
	 */
	public void clearType(int type){
		types &= ~type;
	}
	
	/**
	 * 标示这个词条不属于任何一种类型。
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
	 * @return 进行关键词匹配时这个词所对应的附件对象
	 * 		<br>Attachment object associated with this word when performing keyword matching 
	 */
	public Object getKeywordAttachment() {
		return keywordAttachment;
	}
	/**
	 * @param keywordAttachment 进行关键词匹配时这个词所对应的附件对象
	 * 		<br>Attachment object associated with this word when performing keyword matching 
	 */
	public void setKeywordAttachment(Object keywordAttachment) {
		this.keywordAttachment = keywordAttachment;
	}
	

}
