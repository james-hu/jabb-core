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

package net.sf.jabb.util.text;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.mutable.MutableInt;

/**
 * To check which keywords a text matches, and for each keyword how many occurrences can
 * be found.<br>
 * 检查文本当中匹配了哪些关键词，每个关键词匹配了多少次。
 *  
 * @author Zhengmao HU (James)
 *
 */
public class KeywordMatcher implements Serializable{
	private static final long serialVersionUID = 4468307142195949790L;
	
	protected StringStartWithMatcher matcher;
	
	/**
	 * Constructs a copy which has exactly the same matching definition as the original one.<br>
	 * 创建一个副本，这个副本与原先的对象具有完全相同匹配方式定义。
	 * 
	 * @param toBeCopied	original object<br>原本
	 */
	public KeywordMatcher(KeywordMatcher toBeCopied){
		this.matcher = new StringStartWithMatcher(toBeCopied.matcher);
	}
	
	/**
	 * Constructs a matcher object with specified keywords; When creating internal
	 * data structure, choose to consume more memory for better matching speed.<br>
	 * 根据关键词列表，创建一个匹配器；
	 * 在创建内部数据结构的时候，选择占用更多内存，而换取速度上的提升。
	 * 
	 * @param keywordDefinitions	Keywords and their associated attachment as identifier.<br>
	 * 								关键词以及与之对应的结果标识附件对象。
	 * 								
	 */
	public KeywordMatcher(Map<String, ? extends Object> keywordDefinitions) {
		this(keywordDefinitions, true);
	}

	/**
	 * Constructs a matcher object with specified keywords.<br>
	 * 根据关键词列表，创建一个匹配器。
	 * 
	 * @param keywordDefinitions	Keywords and their associated attachment as identifier.<br>
	 * 								关键词以及与之对应的结果标识附件对象。
	 * @param moreSpaceForSpeed		Whether or not to consume
	 * 								more memory for better matching speed.<br>
	 * 								是否占用更多内存，而换取速度上的提升。
	 */
	public KeywordMatcher(Map<String, ? extends Object> keywordDefinitions, boolean moreSpaceForSpeed) {
		Map<String, Object> newDefinitions = new HashMap<String, Object>(keywordDefinitions.size());
		for (Map.Entry<String, ? extends Object> entry : keywordDefinitions.entrySet()){
			newDefinitions.put(entry.getKey(), new KeywordDefinition(entry.getKey(), entry.getValue()));
		}
		matcher = new StringStartWithMatcher(newDefinitions, moreSpaceForSpeed);
	}

	/**
	 * Do the matching test, find out which keywords can be matched, and how many occurrences of each
	 * keyword can be found.<br>
	 * 进行匹配，返回所匹配上的关键词，以及匹配的次数。
	 *   
	 * @param text 	the text string to be tested<br>待匹配的文本
	 * @return	For all the keywords that can be found in the text, return their attachments (as the Key
	 * 			in the Map) and occurrences count (as the Value in the Map).<br>
	 * 			返回匹配上的全部关键词所对应的attachment（在Map的Key中），以及它们出现的次数（在Map的Value中）。
	 */
	public Map<Object, MutableInt> match(CharSequence text){
		Map<Object, MutableInt> result = null;
		if (text != null && text.length() > 0){
			int i = 0;
			while (i < text.length()){
				Object o = matcher.match(text, i);
				if (o == null){
					i ++;
				}else{
					KeywordDefinition keyword = (KeywordDefinition) o;
					String word = keyword.getKeyword();
					Object attachment = keyword.getAttachement();
					i += word.length();
					if (result == null){
						result = new HashMap<Object, MutableInt>(); 
					}
					if (result.containsKey(attachment)){
						result.get(attachment).increment();
					}else{
						result.put(attachment, new MutableInt(1));
					}
				}
			}
		}
		return result;
	}
	
}

/**
 * For internal usage only.
 * 
 * @author Zhengmao HU (James)
 *
 */
class KeywordDefinition implements Serializable{
	private static final long serialVersionUID = 2347734264779990056L;

	String keyword;
	Object attachement;
	
	KeywordDefinition(String keyword, Object attachment){
		this.keyword = keyword;
		this.attachement = attachment;
	}
	
	public String getKeyword() {
		return keyword;
	}

	public Object getAttachement() {
		return attachement;
	}
}
