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
 * 检查文本当中匹配了哪些关键词，每个关键词匹配了多少次。
 * 
 * <p>To check which keywords a text matches, and for each keyword how many occurrences can
 * be found.
 *  
 * @author Zhengmao HU (James)
 *
 */
public class KeywordMatcher implements Serializable{
	private static final long serialVersionUID = 4468307142195949790L;
	
	protected StringStartWithMatcher matcher;
	
	/**
	 * 创建一个副本，这个副本与原先的对象具有完全相同匹配方式。
	 * 由于这个类的对象不是线程安全的，所以如果要在多线程下用，每个线程
	 * 必须使用单独的副本。
	 * @param toBeCopied	原本
	 */
	public KeywordMatcher(KeywordMatcher toBeCopied){
		this.matcher = new StringStartWithMatcher(toBeCopied.matcher);
	}
	
	public KeywordMatcher(Map<String, Object> patterns) {
		this(patterns, true);
	}

	public KeywordMatcher(Map<String, Object> patterns, boolean moreSpaceForSpeed) {
		Map<String, Object> newPatterns = new HashMap<String, Object>(patterns.size());
		for (Map.Entry<String, Object> entry : patterns.entrySet()){
			newPatterns.put(entry.getKey(), new WrappedPattern(entry.getKey(), entry.getValue()));
		}
		matcher = new StringStartWithMatcher(newPatterns, moreSpaceForSpeed);
	}

	/**
	 * 进行匹配，返回所匹配上的关键词，以及匹配的次数。
	 * @param s 待匹配的文本
	 * @return	返回匹配上的关键词所对应的attachment（在Map的key中），以及它们出现的次数（在Map的value中）
	 */
	public Map<Object, MutableInt> match(CharSequence s){
		Map<Object, MutableInt> result = null;
		if (s != null && s.length() > 0){
			int i = 0;
			while (i < s.length()){
				Object o = matcher.match(s, i);
				if (o == null){
					i ++;
				}else{
					WrappedPattern wrappedPattern = (WrappedPattern) o;
					String word = wrappedPattern.getPattern();
					Object attachment = wrappedPattern.getAttachement();
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

class WrappedPattern implements Serializable{
	private static final long serialVersionUID = 2347734264779990056L;

	String pattern;
	Object attachement;
	
	WrappedPattern(String p, Object a){
		this.pattern = p;
		this.attachement = a;
	}
	
	public String getPattern() {
		return pattern;
	}

	public Object getAttachement() {
		return attachement;
	}
}
