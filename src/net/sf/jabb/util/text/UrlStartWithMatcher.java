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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * 快速对“URL以什么开头”进行匹配。
 * 具体使用方法可参考main()方法的代码和输出。
 * 
 * 匹配用的字符串支持在可开头使用通配符“*.”，表示匹配一个或多个域名段。
 * 比如，*.sina.com可以匹配www.sina.com, news.sina.com, image.news.sina.com, h1.image.news.sina.com。
 * 匹配用的字符串不应包含协议头（比如“http://”），而且不支持正则表达式语法。
 * 被匹配的URL可以包含协议头，也可以不包含。
 * @author Zhengmao HU (James)
 *
 */
public class UrlStartWithMatcher extends StartWithMatcher {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 5100527858549916995L;

	public UrlStartWithMatcher(Map<String, Object> patterns) {
		super(normalizeMatchingDefinitions(patterns, true));
	}

	public UrlStartWithMatcher(Map<String, Object> patterns, boolean moreSpaceForSpeed) {
		super(normalizeMatchingDefinitions(patterns, moreSpaceForSpeed), moreSpaceForSpeed);
	}
	
	/**
	 * 创建一个副本，这个副本与原先的对象具有完全相同匹配方式。
	 * 由于这个类的对象不是线程安全的，所以如果要在多线程下用，每个线程
	 * 必须使用单独的副本。
	 * @param toBeCopied	原本
	 */
	public UrlStartWithMatcher(UrlStartWithMatcher toBeCopied) {
		super(toBeCopied);
	}

	static protected List<MatchingDefinition> normalizeMatchingDefinitions(Map<String, Object> patterns, boolean moreSpaceForSpeed){
		//先分成两个匹配步骤
		Map<String, MatchingStep2> step1 = new HashMap<String, MatchingStep2>();
		for (String p: patterns.keySet()){
			List<String> splited = splitURL(p);
			String reversedBeforePart = splited.get(0);
			String afterPart = splited.get(1);
			Object attachment = patterns.get(p);
			
			String step1Pattern;
			String step1PatternUnescaped;
			String step1Example;
			int k = reversedBeforePart.length();
			char c1 = k >=1 ? reversedBeforePart.charAt(k-1) : 0;
			char c2 = k >=2 ? reversedBeforePart.charAt(k-2) : 0;
			if (c1 == '*' && c2 == '.'){
				step1Example = reversedBeforePart.substring(0, k - 1);	//点号要留下
				step1Pattern = escapeForRegExp(step1Example); 
				step1PatternUnescaped = step1Example;
			}else{
				step1Example = reversedBeforePart + "$";
				step1Pattern = escapeForRegExp(reversedBeforePart) + "$"; //加一个终结符，实现精确匹配
				step1PatternUnescaped = step1Example;
			}
			
			MatchingStep2 step2;
			step2 = step1.get(step1Pattern);
			if (step2 == null){
				step2 = new MatchingStep2(moreSpaceForSpeed);
				step1.put(step1Pattern, step2);
				step2.step1Example = step1Example;
				step2.step1PatternUnescapged = step1PatternUnescaped;
			}
			if (afterPart == null || afterPart.length() == 0){
				step2.noStep2 = true;
				step2.step1Attachment = attachment;
			}else{
				if (! step2.patterns.containsKey(afterPart)){
					step2.patterns.put(afterPart, attachment);
				}
			}
		}
		
		// 如果本pattern能被其他pattern所匹配，则要额外设置
		for (MatchingStep2 step2 : step1.values()){
			String example = step2.step1Example;
				for (String otherStep2Pattern: step1.keySet()){
					MatchingStep2 otherStep2 = step1.get(otherStep2Pattern);
					if (step2 != otherStep2){
						String otherExample = otherStep2.step1Example;
						boolean matched = false; //这里无需考虑性能
						if (example.endsWith(".")){		//比如moc.anis.swen.
							if (otherExample.endsWith(".")){	//比如moc.anis.
								if (example.startsWith(otherExample)){
									matched = true;
								}
							}
						} else {	//比如 moc.anis.swen$
							if (example.startsWith(otherExample)){	//比如moc.anis.
								matched = true;
							}
						}
						if (matched){
							if (step2.step1Attachment != null){
								step2.patterns.putAll(otherStep2.patterns);
							}else{
								step2.additionalStep1Patterns.put(otherStep2.step1PatternUnescapged, otherStep2.step1Attachment);
							}
						}
					}
				}
		}
		

		List<MatchingDefinition> l = new ArrayList<MatchingDefinition>(patterns.size());
		for (String p: step1.keySet()){
			MatchingStep2 step2 = step1.get(p);
			step2.buildMatcher();
			
			MatchingDefinition c = new MatchingDefinition();
			c.setRegularExpression(p);
			c.setAttachment(step1.get(p));
			
			List<String> examples = new ArrayList<String>(1);
			examples.add(step1.get(p).step1Example);
			c.setExactMatchExamples(examples);
			l.add(c);
			
			//System.out.println(p + "<===>" + step1.get(p));
		}
		
		return l;
	}


	/**
	 * 进行匹配判断，URL可以包含协议头，也可以不包含，匹配时对大小写不敏感。
	 * @param url	需要进行匹配判断的URL
	 * @return	匹配了哪一个条件
	 */
	public Object match(String url){
		List<String> splited = splitURL(url);
		String reversedBeforePart = splited.get(0);
		String afterPart = splited.get(1);
		
		MatchingStep2 step2 = (MatchingStep2) super.match(reversedBeforePart + "$");
		if (step2 == null){
			return null;
		}else{
			return step2.match(reversedBeforePart, afterPart);
		}
	}
	
	/**
	 * 将URL拆成两块：反序后的斜线前面的主机、端口和帐号；斜线后面的路径和参数。
	 * 比如对于http://www.news.com/read/daily/headline.html，返回的是：
	 * moc.swen.www和read/daily/headline.html两项。
	 *
	 * @param url	URL，可以带http://，也可不带
	 * @return	第一项是反序后的斜线前面的，第二项是斜线后面的。而且已经被转为小写。
	 */
	static protected List<String> splitURL(String url){
		String beforePart;
		String afterPart;
		List<String> l = new ArrayList<String>(2);
		int protocolEnd = url.indexOf("://");
		if (protocolEnd == -1){	//如果没有协议信息
			protocolEnd = -3;
		}
		int pathStart = url.indexOf("/", protocolEnd + 3);
		if (pathStart == -1){//没路径信息
			pathStart = url.length();
			afterPart = "";
		}else{
			afterPart = url.substring(pathStart+1).toLowerCase();		// 斜线后的路径和参数
		}
		beforePart = url.substring(protocolEnd + 3, pathStart).toLowerCase();	// 斜线前的主机、端口和帐号
		
		String reversedBeforePart = new StringBuffer(beforePart).reverse().toString();
		l.add(reversedBeforePart);
		l.add(afterPart);
		return l;
	}

}

/**
 * 第二阶段的匹配。第一阶段是对斜线前面的进行匹配，第二阶段是针对斜线后面的。
 * @author zhengmah
 *
 */
class MatchingStep2 implements Serializable{
	private static final long serialVersionUID = 2909200683816055940L;
	boolean moreSpaceForSpeed;
	boolean noStep2;	// 是否不再需要进行第二步匹配
	String step1PatternUnescapged;
	String step1Example;
	Object step1Attachment; 	//仅当noStep2为true时有意义
	Map<String, Object> patterns;	// 仅当noStep2为false时有意义
	Map<String, Object> additionalStep1Patterns;	// 仅当step1的pattern能被其他step1 pattern匹配上的时候
	
	StringStartWithMatcher matcher;
	StringStartWithMatcher additionalStep1Matcher;
	
	MatchingStep2(boolean moreSpaceForSpeed){
		this.moreSpaceForSpeed = moreSpaceForSpeed;
		noStep2 = false;
		patterns = new HashMap<String, Object>();
		additionalStep1Patterns = new HashMap<String, Object>();
	}
	
	public void buildMatcher(){
		matcher = new StringStartWithMatcher(patterns, moreSpaceForSpeed);
		additionalStep1Matcher = new StringStartWithMatcher(additionalStep1Patterns, moreSpaceForSpeed);
		patterns = null;	// free space
		additionalStep1Patterns = null;	// free space
	}
	
	public Object match(String reversedBeforePart, String afterPart){
		Object o;
		o = matcher.match(afterPart);
		if (o == null){
			if (step1Attachment != null){
				o = step1Attachment;
			}else{
				o = additionalStep1Matcher.match(reversedBeforePart + "$");
			}
		}
		return o;
	}
	
	public String toString(){
		StringBuffer buff = new StringBuffer();
		buff.append("[(");
		buff.append(step1Attachment);
		buff.append("), (");
		for (String p: patterns.keySet()){
			buff.append(p + "-->");
			Object o = patterns.get(p);
			buff.append(o == null ? "null" : o.toString());
			buff.append(", ");
		}
		buff.append("), (");
		for (String p : additionalStep1Patterns.keySet()){
			buff.append(p + "->");
			Object o = additionalStep1Patterns.get(p);
			buff.append(o == null ? "null" : o.toString());
		}
		buff.append(")]");
		return buff.toString();
	}
	
}

