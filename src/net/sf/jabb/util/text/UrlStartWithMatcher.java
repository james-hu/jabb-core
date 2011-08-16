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
 * Given a text string to be tested, and list of matching strings, find out which matching string the
 * text string starts with.<br>
 * 给定一个待检查的URL字符串，以及一批开头匹配字符串，看看待检查的URL字符串以哪个匹配字符串开头。
 * <p>
 * The matching is case sensitive. If one matching string starts with another,
 * and the text string starts with them, then the longer one will be considered to be matched. 
 * <p>
 * 匹配时对大小写敏感。如果匹配字符串之间互相饱含，则匹配其中最长的。
 * 
 * <p>
 * <ul>
 * 	<li>The URL to be tested can start with protocol string (such as "http://"), or not.</li>
 * 	<li>Matching strings should not contain protocol string.</li>
 * 	<li>"*" can be used at the beginning of matching string as wide card to match one or more
 * 		domain segments. For example, *.sina.com matches
 * 		www.sina.com, news.sina.com, image.news.sina.com, h1.image.news.sina.com。</li>
 * </ul>
 * <p>
 * <ul>
 * 	<li>被匹配的URL可以包含协议头（比如“http://”），也可以不包含。</li>
 * 	<li>匹配用的字符串不应包含协议头。</li>
 * 	<li>匹配用的字符串在可开头使用通配符“*.”，表示匹配一个或多个域名段。比如，*.sina.com可以匹配
 * 		www.sina.com, news.sina.com, image.news.sina.com, h1.image.news.sina.com。</li>
 * </ul>
 * 
 * @author Zhengmao HU (James)
 *
 */
public class UrlStartWithMatcher extends StartWithMatcher {
	private static final long serialVersionUID = 5100527858549916995L;

	/**
	 * Create a new instance according to matching strings and their corresponding attachment objects.<br>
	 * 根据匹配字符串、匹配字符串所对应的附件对象，创建一个新的实例。
	 * <p>
	 * When initializing internal data structure, choose to consume more memory for better matching speed.
	 * <p>
	 * 在创建内部数据结构的时候，选择占用更多内存，而换取速度上的提升。
	 * 
	 * @param matchingDefinitions	Key是匹配字符串，Value是附件对象。
	 * 					当进行匹配检查的时候，返回附件对象来标识哪一个匹配字符串被匹配上了。
	 * 					<p>
	 * 					Key is the matching string, Value is its associated attachment object.
	 * 					When the heading string is matched, the attachment object will be returned
	 * 					as identifier.
	 */
	public UrlStartWithMatcher(Map<String, ? extends Object> matchingDefinitions) {
		super(normalizeMatchingDefinitions(matchingDefinitions, true));
	}

	/**
	 * Create a new instance according to matching strings and their corresponding attachment objects.<br>
	 * 根据匹配字符串、匹配字符串所对应的附件对象，创建一个新的实例。
	 * 
	 * @param matchingDefinitions	Key是匹配字符串，Value是附件对象。
	 * 					当进行匹配检查的时候，返回附件对象来标识哪一个匹配字符串被匹配上了。
	 * 					<p>
	 * 					Key is the matching string, Value is its associated attachment object.
	 * 					When the heading string is matched, the attachment object will be returned
	 * 					as identifier.
	 * @param moreSpaceForSpeed  是否占用更多内存，而换取速度上的提升。
	 * 								<br>Whether or not to consume
	 * 								more memory for better matching speed.
	 */
	public UrlStartWithMatcher(Map<String, ? extends Object> matchingDefinitions, boolean moreSpaceForSpeed) {
		super(normalizeMatchingDefinitions(matchingDefinitions, moreSpaceForSpeed), moreSpaceForSpeed);
	}
	
	/**
	 * Create a copy, the copy will have exactly the same matching 
	 * definitions as the original copy.<br>
	 * 创建一个副本，这个副本与原先的对象具有完全相同匹配方式。
	 * 
	 * @param toBeCopied	原本。<br>The original copy.
	 */
	public UrlStartWithMatcher(UrlStartWithMatcher toBeCopied) {
		super(toBeCopied);
	}

	/**
	 * Normalize matching definitions according to requirements of {@link StartWithMatcher}.<br>
	 * 根据{@link StartWithMatcher}的需要来规范化匹配条件定义。
	 * 
	 * @param matchingDefinitions	Key是匹配字符串，Value是附件对象。
	 * 					当进行匹配检查的时候，返回附件对象来标识哪一个匹配字符串被匹配上了。
	 * 					<p>
	 * 					Key is the matching string, Value is its associated attachment object.
	 * 					When the matching string is matched, the attachment object will be returned
	 * 					as identifier.
	 * @return	{@link StartWithMatcher}所需的匹配条件定义。
	 * 			<br>Matching definitions for usage of {@link StartWithMatcher}.
	 * @param moreSpaceForSpeed  是否占用更多内存，而换取速度上的提升。
	 * 								<br>Whether or not to consume
	 * 								more memory for better matching speed.
	 * @return	Normalized matching definitions<br>规范化了的匹配条件定义
	 */
	static protected List<MatchingDefinition> normalizeMatchingDefinitions(Map<String, ? extends Object> matchingDefinitions, boolean moreSpaceForSpeed){
		//先分成两个匹配步骤
		Map<String, UrlStartWithMatcherStep2> step1 = new HashMap<String, UrlStartWithMatcherStep2>();
		for (Map.Entry<String, ? extends Object> e: matchingDefinitions.entrySet()){
			String[] splited = splitURL(e.getKey());
			String reversedBeforePart = splited[0];
			String afterPart = splited[1];
			Object attachment = e.getValue();
			
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
			
			UrlStartWithMatcherStep2 step2;
			step2 = step1.get(step1Pattern);
			if (step2 == null){
				step2 = new UrlStartWithMatcherStep2(moreSpaceForSpeed);
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
		for (UrlStartWithMatcherStep2 step2 : step1.values()){
			String example = step2.step1Example;
				for (UrlStartWithMatcherStep2 otherStep2: step1.values()){
					//UrlStartWithMatcherStep2 otherStep2 = step1.get(otherStep2Pattern);
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
		

		List<MatchingDefinition> l = new ArrayList<MatchingDefinition>(matchingDefinitions.size());
		for (Map.Entry<String, UrlStartWithMatcherStep2> e: step1.entrySet()){
			UrlStartWithMatcherStep2 step2 = e.getValue();
			step2.buildMatcher();
			
			MatchingDefinition c = new MatchingDefinition();
			c.setRegularExpression(e.getKey());
			c.setAttachment(e.getValue());
			
			List<String> examples = new ArrayList<String>(1);
			examples.add(e.getValue().step1Example);
			c.setExactMatchExamples(examples);
			l.add(c);
			
			//System.out.println(p + "<===>" + step1.get(p));
		}
		
		return l;
	}


	/**
	 * Find out which matching string matches the URL. URL can start with protocol (such as "http://"), or not.<br>
	 * 进行匹配判断，URL可以包含协议头，也可以不包含，匹配时对大小写不敏感。
	 * <p>
	 * Matching is case insensitive.
	 * 
	 * @param url	需要进行匹配判断的URL<br>The URL need to be tested.
	 * @return	匹配到的字符串所对应的附件，如果找不到任何匹配，则返回null
	 * 			<br>The corresponding attachment object of the matching string that matches the URL.
	 * 			Return null if no matching found.
	 */
	public Object match(String url){
		String[] splited = splitURL(url);
		String reversedBeforePart = splited[0];
		String afterPart = splited[1];
		
		UrlStartWithMatcherStep2 step2 = (UrlStartWithMatcherStep2) super.match(reversedBeforePart + "$");
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
	static protected String[] splitURL(String url){
		String beforePart;
		String afterPart;
		String[] l = new String[2];
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
		l[0] = reversedBeforePart;
		l[1] = afterPart;
		return l;
	}

}

/**
 * 第二阶段的匹配。第一阶段是对斜线前面的进行匹配，第二阶段是针对斜线后面的。
 *
 * @author Zhengmao HU (James)
 */
class UrlStartWithMatcherStep2 implements Serializable{
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
	
	UrlStartWithMatcherStep2(boolean moreSpaceForSpeed){
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

