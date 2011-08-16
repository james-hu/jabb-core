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
 * ����ı�����ƥ������Щ�ؼ��ʣ�ÿ���ؼ���ƥ���˶��ٴΡ�
 *  
 * @author Zhengmao HU (James)
 *
 */
public class KeywordMatcher implements Serializable{
	private static final long serialVersionUID = 4468307142195949790L;
	
	protected StringStartWithMatcher matcher;
	
	/**
	 * Constructs a copy which has exactly the same matching definition as the original one.<br>
	 * ����һ�����������������ԭ�ȵĶ��������ȫ��ͬƥ�䷽ʽ���塣
	 * 
	 * @param toBeCopied	original object<br>ԭ��
	 */
	public KeywordMatcher(KeywordMatcher toBeCopied){
		this.matcher = new StringStartWithMatcher(toBeCopied.matcher);
	}
	
	/**
	 * Constructs a matcher object with specified keywords; When creating internal
	 * data structure, choose to consume more memory for better matching speed.<br>
	 * ���ݹؼ����б�����һ��ƥ������
	 * �ڴ����ڲ����ݽṹ��ʱ��ѡ��ռ�ø����ڴ棬����ȡ�ٶ��ϵ�������
	 * 
	 * @param keywordDefinitions	Keywords and their associated attachment as identifier.<br>
	 * 								�ؼ����Լ���֮��Ӧ�Ľ����ʶ��������
	 * 								
	 */
	public KeywordMatcher(Map<String, ? extends Object> keywordDefinitions) {
		this(keywordDefinitions, true);
	}

	/**
	 * Constructs a matcher object with specified keywords.<br>
	 * ���ݹؼ����б�����һ��ƥ������
	 * 
	 * @param keywordDefinitions	Keywords and their associated attachment as identifier.<br>
	 * 								�ؼ����Լ���֮��Ӧ�Ľ����ʶ��������
	 * @param moreSpaceForSpeed		Whether or not to consume
	 * 								more memory for better matching speed.<br>
	 * 								�Ƿ�ռ�ø����ڴ棬����ȡ�ٶ��ϵ�������
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
	 * ����ƥ�䣬������ƥ���ϵĹؼ��ʣ��Լ�ƥ��Ĵ�����
	 *   
	 * @param text 	the text string to be tested<br>��ƥ����ı�
	 * @return	For all the keywords that can be found in the text, return their attachments (as the Key
	 * 			in the Map) and occurrences count (as the Value in the Map).<br>
	 * 			����ƥ���ϵ�ȫ���ؼ�������Ӧ��attachment����Map��Key�У����Լ����ǳ��ֵĴ�������Map��Value�У���
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
