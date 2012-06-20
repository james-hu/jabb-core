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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Given a text string to be tested, and list of matching strings, find out which matching string the
 * text string starts with.<br>
 * ����һ���������ı��ַ������Լ�һ����ͷƥ���ַ����������������ı��ַ������ĸ�ƥ���ַ�����ͷ��
 * <p>
 * The matching is case sensitive. 
 * If one matching string starts with another,
 * and the text string starts with them, then the longer one will be considered to be matched. 
 * <p>
 * ƥ��ʱ�Դ�Сд���С����ƥ���ַ���֮�以�౥������ƥ��������ġ�
 * 
 * <p>
 * If the matching need to be checked upon number segments (start number ~ end number) represented 
 * as strings, {@link #expandNumberMatchingRange(Map, String, String, Object)} method can be used to
 * expand number segments to heading number strings.
 * <p>
 * �����Ҫ�Դ������ֺ��루��ʼ����~�������룩���ַ�������ƥ�䣬��ʹ��
 * {@link #expandNumberMatchingRange(Map, String, String, Object)} ����
 * ��������ַ�����һ����ʼ���룬һ���������룩ת��Ϊ����ͷ�ַ�����
 * 
 * @author Zhengmao HU (James)
 *
 */
public class StringStartWithMatcher extends StartWithMatcher {

	private static final long serialVersionUID = -2501231925022032723L;

	/**
	 * Create a new instance according to heading strings and their corresponding attachment objects.<br>
	 * ���ݿ�ͷƥ���ַ�������ͷƥ���ַ�������Ӧ�ĸ������󣬴���һ���µ�ʵ����
	 * <p>
	 * When initializing internal data structure, choose to consume more memory for better matching speed.
	 * <p>
	 * �ڴ����ڲ����ݽṹ��ʱ��ѡ��ռ�ø����ڴ棬����ȡ�ٶ��ϵ�������
	 * 
	 * @param headingDefinitions	Key is the heading string, Value is its associated attachment object.
	 * 					When the heading string is matched, the attachment object will be returned
	 * 					as identifier.<p>
	 * 					Key��ƥ���ַ�����Value�Ǹ�������
	 * 					������ƥ�����ʱ�򣬷��ظ�����������ʶ��һ��ƥ���ַ�����ƥ�����ˡ�
	 */
	public StringStartWithMatcher(Map<String, ? extends Object> headingDefinitions) {
		super(normalizeMatchingDefinitions(headingDefinitions));
	}

	/**
	 * Create a new instance according to heading strings and their corresponding attachment objects.<br>
	 * ���ݿ�ͷƥ���ַ�������ͷƥ���ַ�������Ӧ�ĸ������󣬴���һ���µ�ʵ����
	 * 
	 * @param headingDefinitions	Key��ƥ���ַ�����Value�Ǹ�������
	 * 					������ƥ�����ʱ�򣬷��ظ�����������ʶ��һ��ƥ���ַ�����ƥ�����ˡ�
	 * 					<p>
	 * 					Key is the heading string, Value is its associated attachment object.
	 * 					When the heading string is matched, the attachment object will be returned
	 * 					as identifier.
	 * @param moreSpaceForSpeed  �Ƿ�ռ�ø����ڴ棬����ȡ�ٶ��ϵ�������
	 * 								<p>Whether or not to consume
	 * 								more memory for better matching speed.
	 */
	public StringStartWithMatcher(Map<String, ? extends Object> headingDefinitions, boolean moreSpaceForSpeed) {
		super(normalizeMatchingDefinitions(headingDefinitions), moreSpaceForSpeed);
	}
	
	/**
	 * Create a copy, the copy will have exactly the same matching 
	 * definitions as the original copy.<br>
	 * ����һ�����������������ԭ�ȵĶ��������ȫ��ͬƥ�䷽ʽ��
	 * 
	 * @param toBeCopied	ԭ����<br>The original copy.
	 */
	public StringStartWithMatcher(StringStartWithMatcher toBeCopied) {
		super(toBeCopied);
	}

	/**
	 * Normalize matching definitions according to requirements of {@link StartWithMatcher}.<br>
	 * ����{@link StartWithMatcher}����Ҫ���淶��ƥ���������塣
	 * 
	 * @param headingDefinitions	Key��ƥ���ַ�����Value�Ǹ�������
	 * 					������ƥ�����ʱ�򣬷��ظ�����������ʶ��һ��ƥ���ַ�����ƥ�����ˡ�
	 * 					<p>
	 * 					Key is the heading string, Value is its associated attachment object.
	 * 					When the heading string is matched, the attachment object will be returned
	 * 					as identifier.
	 * @return	{@link StartWithMatcher}�����ƥ���������塣
	 * 			<br>Matching definitions for usage of {@link StartWithMatcher}.
	 */
	static protected List<MatchingDefinition> normalizeMatchingDefinitions(Map<String, ? extends Object> headingDefinitions){
		// exactMatchExample�Զ�����Ϊ��regularExpression��ͬ
		List<MatchingDefinition> l = new ArrayList<MatchingDefinition>(headingDefinitions.size());
		for (Map.Entry<String, ? extends Object> e: headingDefinitions.entrySet()){
			MatchingDefinition c = new MatchingDefinition();
			c.setRegularExpression(escapeForRegExp(e.getKey()));
			c.setAttachment(e.getValue());
			c.setExactMatchExample(e.getKey());
			l.add(c);
		}
		return l;
	}
	
	/**
	 * Expand number segments (such as 138000~138999 or 138000~138029) into number headings
	 * (such as 138 or {13800,13801,13802}).<br>
	 * �Ѻ���Σ����ƣ�138000~138999��138000~138029��չ���ɺ���ͷ�����ƣ�138��13800,13801,13802����
	 * 
	 * @param headingDefinitions	��������{@link StringStartWithMatcher}���г�ʼ����չ�����ƥ������
	 * 			�ᱻ�ŵ����Map�
	 * 			<br> Equivalent heading definitions that could be used to 
	 * 			create instance of {@link StringStartWithMatcher} will be put into this Map.
	 * @param start	��ʼ����	<br> first/starting number
	 * @param end	�������� <br> last/ending number
	 * @param attachment	ƥ�丽��<br>attachment to identify that the segment matches a string
	 */
	public static <T> void expandNumberMatchingRange(Map<String, T> headingDefinitions, String start, String end, T attachment){
		int firstDiff; //��һ������ͬ�ַ���λ��
		int lastDiff;  //ĩβ0:9��Ӧ�ο�ʼ��λ��
		
		// ��ǿ�б�֤��ʼ������������볤����ͬ
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
		
		// Ȼ��Ѱ�ҵ�һ������ͬ�ַ���λ��
		for (firstDiff = 0; firstDiff < start.length(); firstDiff++){
			if (start.charAt(firstDiff) != end.charAt(firstDiff)){
				break;
			}
		}
		
		// ��Ѱ��ĩβ0:9��Ӧ�ο�ʼ��λ��
		for (lastDiff = start.length() - 1; lastDiff >= 0; lastDiff--){
			if (start.charAt(lastDiff) != '0' || end.charAt(lastDiff) != '9'){
				break;
			}
		}
		lastDiff++;
		
		if (firstDiff == lastDiff){ // ���ʾ�ɺϲ�Ϊһ��
			headingDefinitions.put(start.substring(0, firstDiff), attachment);
		} else { // ���ʾҪ��չΪ����
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
