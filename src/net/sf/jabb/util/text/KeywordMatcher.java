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

package net.sf.jabb.util.text;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.mutable.MutableInt;

/**
 * ����ı�����ƥ������Щ�ؼ��ʣ�ÿ���ؼ���ƥ���˶��ٴΡ�
 * <p>
 * To check which keywords a text matches, and for each keyword how many occurrences can
 * be found.
 *  
 * @author Zhengmao HU (James)
 *
 */
public class KeywordMatcher implements Serializable{
	private static final long serialVersionUID = 4468307142195949790L;
	
	protected StringStartWithMatcher matcher;
	
	/**
	 * ����һ�����������������ԭ�ȵĶ��������ȫ��ͬƥ�䷽ʽ���塣
	 * <p>
	 * Create a copy which has exactly the same matching definition as original one.
	 * 
	 * @param toBeCopied	ԭ��<br>original object
	 */
	public KeywordMatcher(KeywordMatcher toBeCopied){
		this.matcher = new StringStartWithMatcher(toBeCopied.matcher);
	}
	
	/**
	 * ���ݹؼ����б�����һ��ƥ������
	 * �ڴ����ڲ����ݽṹ��ʱ��ѡ��ռ�ø����ڴ棬����ȡ�ٶ��ϵ�������
	 * <p>
	 * Create a matcher object with specified keywords, when creating internal
	 * data structure, choose to consume more memory for better matching speed
	 * 
	 * @param keywordDefinitions	�ؼ����Լ���֮��Ӧ�Ľ����ʶ��������
	 * 								<br>Keywords and their associated attachment as identifier.
	 */
	public KeywordMatcher(Map<String, Object> keywordDefinitions) {
		this(keywordDefinitions, true);
	}

	/**
	 * ���ݹؼ����б�����һ��ƥ������
	 * <p>
	 * Create a matcher object with specified keywords.
	 * 
	 * @param keywordDefinitions	�ؼ����Լ���֮��Ӧ�Ľ����ʶ��������
	 * 								<br>Keywords and their associated attachment as identifier.
	 * @param moreSpaceForSpeed		�Ƿ�ռ�ø����ڴ棬����ȡ�ٶ��ϵ�������
	 * 								<br>Whether or not to consume
	 * 								more memory for better matching speed.
	 */
	public KeywordMatcher(Map<String, Object> keywordDefinitions, boolean moreSpaceForSpeed) {
		Map<String, Object> newDefinitions = new HashMap<String, Object>(keywordDefinitions.size());
		for (Map.Entry<String, Object> entry : keywordDefinitions.entrySet()){
			newDefinitions.put(entry.getKey(), new KeywordDefinition(entry.getKey(), entry.getValue()));
		}
		matcher = new StringStartWithMatcher(newDefinitions, moreSpaceForSpeed);
	}

	/**
	 * ����ƥ�䣬������ƥ���ϵĹؼ��ʣ��Լ�ƥ��Ĵ�����
	 * <p>
	 * Do the matching test, find out which keywords can be matched, and how many occurrences of each
	 * keyword can be found.
	 *   
	 * @param text ��ƥ����ı�<br>the text string to be tested
	 * @return	����ƥ���ϵĹؼ�������Ӧ��attachment����Map��Key�У����Լ����ǳ��ֵĴ�������Map��Value�У�
	 * 			<br>For each keywords that find in the text, return its attachment (as the Key
	 * 			in the Map) and occurrences count (as the Value in the Map).
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
