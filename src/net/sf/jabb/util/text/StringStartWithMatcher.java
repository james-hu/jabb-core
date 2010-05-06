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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 给定一个待检查的文本字符串，以及一批开头匹配字符串，看看待检查的文本字符串以哪个匹配字符串开头。
 * 匹配时对大小写敏感。如果匹配字符串之间互相饱含，则匹配其中最长的。
 * <p>
 * 如果需要对代表数字号码（开始号码~结束号码）的字符串进行匹配，可使用
 * {@link #expandNumberMatchingRange(String, String, Object)} 方法
 * 将号码段字符串（一个开始号码，一个结束号码）转换为号码头字符串。
 * 
 * <p>
 * Given a text string to be tested, and list of matching strings, find out which matching string the
 * text string starts with. The matching is case sensitive. If one matching string starts with another,
 * and the text string starts with them, then the longer one will be considered to be matched. 
 * 
 * <p>
 * If the matching need to be checked upon number segments (start number ~ end number) represented 
 * as strings, {@link #expandNumberMatchingRange(String, String, Object)} method can be used to
 * expand number segments to heading number strings.
 * 
 * @author Zhengmao HU (James)
 *
 */
public class StringStartWithMatcher extends StartWithMatcher {

	private static final long serialVersionUID = -2501231925022032723L;

	/**
	 * 根据开头匹配字符串、开头匹配字符串所对应的附件对象，创建一个新的实例。
	 * 在创建内部数据结构的时候，选择占用更多内存，而换取速度上的提升。
	 * <p>
	 * Create a new instance according to heading strings and their corresponding attachment objects.
	 * When initializing internal data structure, choose to consume more memory for better matching speed.
	 * 
	 * @param headingDefinitions	Key是匹配字符串，Value是附件对象。
	 * 					当进行匹配检查的时候，返回附件对象来标识哪一个匹配字符串被匹配上了。
	 * 					<br>
	 * 					Key is the heading string, Value is its associated attachment object.
	 * 					When the heading string is matched, the attachment object will be returned
	 * 					as identifier.
	 */
	public StringStartWithMatcher(Map<String, Object> headingDefinitions) {
		super(normalizeMatchingDefinitions(headingDefinitions));
	}

	/**
	 * 根据开头匹配字符串、开头匹配字符串所对应的附件对象，创建一个新的实例。
	 * <p>
	 * Create a new instance according to heading strings and their corresponding attachment objects.
	 * 
	 * @param headingDefinitions	Key是匹配字符串，Value是附件对象。
	 * 					当进行匹配检查的时候，返回附件对象来标识哪一个匹配字符串被匹配上了。
	 * 					<br>
	 * 					Key is the heading string, Value is its associated attachment object.
	 * 					When the heading string is matched, the attachment object will be returned
	 * 					as identifier.
	 * @param moreSpaceForSpeed  是否占用更多内存，而换取速度上的提升。
	 * 								<br>Whether or not to consume
	 * 								more memory for better matching speed.
	 */
	public StringStartWithMatcher(Map<String, Object> headingDefinitions, boolean moreSpaceForSpeed) {
		super(normalizeMatchingDefinitions(headingDefinitions), moreSpaceForSpeed);
	}
	
	/**
	 * 创建一个副本，这个副本与原先的对象具有完全相同匹配方式。
	 * <p>
	 * Create a copy, the copy will have exactly the same matching 
	 * definitions as the original copy.
	 * 
	 * @param toBeCopied	原本。<br>The original copy.
	 */
	public StringStartWithMatcher(StringStartWithMatcher toBeCopied) {
		super(toBeCopied);
	}

	/**
	 * 根据{@link StartWithMatcher}的需要来规范化匹配条件定义。
	 * 
	 * <p>
	 * Normalize matching definitions according to requirements of {@link StartWithMatcher}.
	 * 
	 * @param headingDefinitions	Key是匹配字符串，Value是附件对象。
	 * 					当进行匹配检查的时候，返回附件对象来标识哪一个匹配字符串被匹配上了。
	 * 					<br>
	 * 					Key is the heading string, Value is its associated attachment object.
	 * 					When the heading string is matched, the attachment object will be returned
	 * 					as identifier.
	 * @return	{@link StartWithMatcher}所需的匹配条件定义。
	 * 			<br>Matching definitions for usage of {@link StartWithMatcher}.
	 */
	static protected List<MatchingDefinition> normalizeMatchingDefinitions(Map<String, Object> headingDefinitions){
		// exactMatchExample自动设置为与regularExpression相同
		List<MatchingDefinition> l = new ArrayList<MatchingDefinition>(headingDefinitions.size());
		for (String p: headingDefinitions.keySet()){
			MatchingDefinition c = new MatchingDefinition();
			c.setRegularExpression(escapeForRegExp(p));
			c.setAttachment(headingDefinitions.get(p));
			c.setExactMatchExample(p);
			l.add(c);
		}
		return l;
	}
	
	/**
	 * 把号码段（类似：138000~138999或138000~138029）展开成号码头（类似：138或13800,13801,13802）。
	 * <p>
	 * Expand number segments (such as 138000~138999 or 138000~138029) into number headings
	 * (such as 138 or {13800,13801,13802}).
	 * 
	 * @headingDefinitions	可用来对{@link StringStartWithMatcher}进行初始化的展开后的匹配条件
	 * 			会被放到这个Map里。
	 * 			<br> Equivalent heading definitions that could be used to 
	 * 			create instance of {@link StringStartWithMatcher} will be put into this Map.
	 * @param start	起始号码	<br> first/starting number
	 * @param end	结束号码 <br> last/ending number
	 * @param attachment	匹配附件<br>attachment to identify that the segment matches a string
	 */
	public static void expandNumberMatchingRange(Map<String, Object> headingDefinitions, String start, String end, Object attachment){
		int firstDiff; //第一个不相同字符的位置
		int lastDiff;  //末尾0:9对应段开始的位置
		
		// 先强行保证起始号码与结束号码长度相同
		if (start.length() > end.length()){
			StringBuilder sb = new StringBuilder(end);
			while (start.length() > sb.length()){
				sb.append("9");
			}
			end = sb.toString();
		} else if (end.length() > start.length()){
			StringBuilder sb = new StringBuilder(start);
			while (end.length() > sb.length()){
				sb.append("0");
			}
			start = sb.toString();
		}
		
		// 然后寻找第一个不相同字符的位置
		for (firstDiff = 0; firstDiff < start.length(); firstDiff++){
			if (start.charAt(firstDiff) != end.charAt(firstDiff)){
				break;
			}
		}
		
		// 再寻找末尾0:9对应段开始的位置
		for (lastDiff = start.length() - 1; lastDiff >= 0; lastDiff--){
			if (start.charAt(lastDiff) != '0' || end.charAt(lastDiff) != '9'){
				break;
			}
		}
		lastDiff++;
		
		if (firstDiff == lastDiff){ // 则表示可合并为一条
			headingDefinitions.put(start.substring(0, firstDiff), attachment);
		} else { // 则表示要扩展为多条
			int j = Integer.parseInt(start.substring(firstDiff, lastDiff));
			int k = Integer.parseInt(end.substring(firstDiff, lastDiff));
			String head = start.substring(0, firstDiff);
			String f = "%" + (lastDiff-firstDiff) + "d";
			StringBuilder sb = new StringBuilder();
			for (int i = j; i <= k; i++){
				sb.setLength(0);
				sb.append(head);
				sb.append(String.format(f, i));
				headingDefinitions.put(sb.toString(), attachment);
			}
		}
	}

}
