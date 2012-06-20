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
 * ����һ��������URL�ַ������Լ�һ����ͷƥ���ַ���������������URL�ַ������ĸ�ƥ���ַ�����ͷ��
 * <p>
 * The matching is case sensitive. If one matching string starts with another,
 * and the text string starts with them, then the longer one will be considered to be matched. 
 * <p>
 * ƥ��ʱ�Դ�Сд���С����ƥ���ַ���֮�以�౥������ƥ��������ġ�
 * 
 * <p>
 * <ul>
 * 	<li>The URL to be tested can start with protocol string (such as "http://"), or not.</li>
 * 	<li>Matching strings should not contain protocol string.</li>
 * 	<li>"*" can be used at the beginning of matching string as wide card to match one or more
 * 		domain segments. For example, *.sina.com matches
 * 		www.sina.com, news.sina.com, image.news.sina.com, h1.image.news.sina.com��</li>
 * </ul>
 * <p>
 * <ul>
 * 	<li>��ƥ���URL���԰���Э��ͷ�����硰http://������Ҳ���Բ�������</li>
 * 	<li>ƥ���õ��ַ�����Ӧ����Э��ͷ��</li>
 * 	<li>ƥ���õ��ַ����ڿɿ�ͷʹ��ͨ�����*.������ʾƥ��һ�����������Ρ����磬*.sina.com����ƥ��
 * 		www.sina.com, news.sina.com, image.news.sina.com, h1.image.news.sina.com��</li>
 * </ul>
 * 
 * @author Zhengmao HU (James)
 *
 */
public class UrlStartWithMatcher extends StartWithMatcher {
	private static final long serialVersionUID = 5100527858549916995L;

	/**
	 * Create a new instance according to matching strings and their corresponding attachment objects.<br>
	 * ����ƥ���ַ�����ƥ���ַ�������Ӧ�ĸ������󣬴���һ���µ�ʵ����
	 * <p>
	 * When initializing internal data structure, choose to consume more memory for better matching speed.
	 * <p>
	 * �ڴ����ڲ����ݽṹ��ʱ��ѡ��ռ�ø����ڴ棬����ȡ�ٶ��ϵ�������
	 * 
	 * @param matchingDefinitions	Key��ƥ���ַ�����Value�Ǹ�������
	 * 					������ƥ�����ʱ�򣬷��ظ�����������ʶ��һ��ƥ���ַ�����ƥ�����ˡ�
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
	 * ����ƥ���ַ�����ƥ���ַ�������Ӧ�ĸ������󣬴���һ���µ�ʵ����
	 * 
	 * @param matchingDefinitions	Key��ƥ���ַ�����Value�Ǹ�������
	 * 					������ƥ�����ʱ�򣬷��ظ�����������ʶ��һ��ƥ���ַ�����ƥ�����ˡ�
	 * 					<p>
	 * 					Key is the matching string, Value is its associated attachment object.
	 * 					When the heading string is matched, the attachment object will be returned
	 * 					as identifier.
	 * @param moreSpaceForSpeed  �Ƿ�ռ�ø����ڴ棬����ȡ�ٶ��ϵ�������
	 * 								<br>Whether or not to consume
	 * 								more memory for better matching speed.
	 */
	public UrlStartWithMatcher(Map<String, ? extends Object> matchingDefinitions, boolean moreSpaceForSpeed) {
		super(normalizeMatchingDefinitions(matchingDefinitions, moreSpaceForSpeed), moreSpaceForSpeed);
	}
	
	/**
	 * Create a copy, the copy will have exactly the same matching 
	 * definitions as the original copy.<br>
	 * ����һ�����������������ԭ�ȵĶ��������ȫ��ͬƥ�䷽ʽ��
	 * 
	 * @param toBeCopied	ԭ����<br>The original copy.
	 */
	public UrlStartWithMatcher(UrlStartWithMatcher toBeCopied) {
		super(toBeCopied);
	}

	/**
	 * Normalize matching definitions according to requirements of {@link StartWithMatcher}.<br>
	 * ����{@link StartWithMatcher}����Ҫ���淶��ƥ���������塣
	 * 
	 * @param matchingDefinitions	Key��ƥ���ַ�����Value�Ǹ�������
	 * 					������ƥ�����ʱ�򣬷��ظ�����������ʶ��һ��ƥ���ַ�����ƥ�����ˡ�
	 * 					<p>
	 * 					Key is the matching string, Value is its associated attachment object.
	 * 					When the matching string is matched, the attachment object will be returned
	 * 					as identifier.
	 * @return	{@link StartWithMatcher}�����ƥ���������塣
	 * 			<br>Matching definitions for usage of {@link StartWithMatcher}.
	 * @param moreSpaceForSpeed  �Ƿ�ռ�ø����ڴ棬����ȡ�ٶ��ϵ�������
	 * 								<br>Whether or not to consume
	 * 								more memory for better matching speed.
	 * @return	Normalized matching definitions<br>�淶���˵�ƥ����������
	 */
	static protected List<MatchingDefinition> normalizeMatchingDefinitions(Map<String, ? extends Object> matchingDefinitions, boolean moreSpaceForSpeed){
		//�ȷֳ�����ƥ�䲽��
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
				step1Example = reversedBeforePart.substring(0, k - 1);	//���Ҫ����
				step1Pattern = escapeForRegExp(step1Example); 
				step1PatternUnescaped = step1Example;
			}else{
				step1Example = reversedBeforePart + "$";
				step1Pattern = escapeForRegExp(reversedBeforePart) + "$"; //��һ���ս����ʵ�־�ȷƥ��
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
		
		// �����pattern�ܱ�����pattern��ƥ�䣬��Ҫ��������
		for (UrlStartWithMatcherStep2 step2 : step1.values()){
			String example = step2.step1Example;
				for (UrlStartWithMatcherStep2 otherStep2: step1.values()){
					//UrlStartWithMatcherStep2 otherStep2 = step1.get(otherStep2Pattern);
					if (step2 != otherStep2){
						String otherExample = otherStep2.step1Example;
						boolean matched = false; //�������迼������
						if (example.endsWith(".")){		//����moc.anis.swen.
							if (otherExample.endsWith(".")){	//����moc.anis.
								if (example.startsWith(otherExample)){
									matched = true;
								}
							}
						} else {	//���� moc.anis.swen$
							if (example.startsWith(otherExample)){	//����moc.anis.
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
	 * ����ƥ���жϣ�URL���԰���Э��ͷ��Ҳ���Բ�������ƥ��ʱ�Դ�Сд�����С�
	 * <p>
	 * Matching is case insensitive.
	 * 
	 * @param url	��Ҫ����ƥ���жϵ�URL<br>The URL need to be tested.
	 * @return	ƥ�䵽���ַ�������Ӧ�ĸ���������Ҳ����κ�ƥ�䣬�򷵻�null
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
	 * ��URL������飺������б��ǰ����������˿ں��ʺţ�б�ߺ����·���Ͳ�����
	 * �������http://www.news.com/read/daily/headline.html�����ص��ǣ�
	 * moc.swen.www��read/daily/headline.html���
	 *
	 * @param url	URL�����Դ�http://��Ҳ�ɲ���
	 * @return	��һ���Ƿ�����б��ǰ��ģ��ڶ�����б�ߺ���ġ������Ѿ���תΪСд��
	 */
	static protected String[] splitURL(String url){
		String beforePart;
		String afterPart;
		String[] l = new String[2];
		int protocolEnd = url.indexOf("://");
		if (protocolEnd == -1){	//���û��Э����Ϣ
			protocolEnd = -3;
		}
		int pathStart = url.indexOf("/", protocolEnd + 3);
		if (pathStart == -1){//û·����Ϣ
			pathStart = url.length();
			afterPart = "";
		}else{
			afterPart = url.substring(pathStart+1).toLowerCase();		// б�ߺ��·���Ͳ���
		}
		beforePart = url.substring(protocolEnd + 3, pathStart).toLowerCase();	// б��ǰ���������˿ں��ʺ�
		
		String reversedBeforePart = new StringBuffer(beforePart).reverse().toString();
		l[0] = reversedBeforePart;
		l[1] = afterPart;
		return l;
	}

}

/**
 * �ڶ��׶ε�ƥ�䡣��һ�׶��Ƕ�б��ǰ��Ľ���ƥ�䣬�ڶ��׶������б�ߺ���ġ�
 *
 * @author Zhengmao HU (James)
 */
class UrlStartWithMatcherStep2 implements Serializable{
	private static final long serialVersionUID = 2909200683816055940L;
	boolean moreSpaceForSpeed;
	boolean noStep2;	// �Ƿ�����Ҫ���еڶ���ƥ��
	String step1PatternUnescapged;
	String step1Example;
	Object step1Attachment; 	//����noStep2Ϊtrueʱ������
	Map<String, Object> patterns;	// ����noStep2Ϊfalseʱ������
	Map<String, Object> additionalStep1Patterns;	// ����step1��pattern�ܱ�����step1 patternƥ���ϵ�ʱ��
	
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

