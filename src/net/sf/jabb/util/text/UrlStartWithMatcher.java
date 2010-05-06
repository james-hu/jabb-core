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
 * ���ٶԡ�URL��ʲô��ͷ������ƥ�䡣
 * ����ʹ�÷����ɲο�main()�����Ĵ���������
 * 
 * ƥ���õ��ַ���֧���ڿɿ�ͷʹ��ͨ�����*.������ʾƥ��һ�����������Ρ�
 * ���磬*.sina.com����ƥ��www.sina.com, news.sina.com, image.news.sina.com, h1.image.news.sina.com��
 * ƥ���õ��ַ�����Ӧ����Э��ͷ�����硰http://���������Ҳ�֧��������ʽ�﷨��
 * ��ƥ���URL���԰���Э��ͷ��Ҳ���Բ�������
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
	 * ����һ�����������������ԭ�ȵĶ��������ȫ��ͬƥ�䷽ʽ��
	 * ���������Ķ������̰߳�ȫ�ģ��������Ҫ�ڶ��߳����ã�ÿ���߳�
	 * ����ʹ�õ����ĸ�����
	 * @param toBeCopied	ԭ��
	 */
	public UrlStartWithMatcher(UrlStartWithMatcher toBeCopied) {
		super(toBeCopied);
	}

	static protected List<MatchingDefinition> normalizeMatchingDefinitions(Map<String, Object> patterns, boolean moreSpaceForSpeed){
		//�ȷֳ�����ƥ�䲽��
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
				step1Example = reversedBeforePart.substring(0, k - 1);	//���Ҫ����
				step1Pattern = escapeForRegExp(step1Example); 
				step1PatternUnescaped = step1Example;
			}else{
				step1Example = reversedBeforePart + "$";
				step1Pattern = escapeForRegExp(reversedBeforePart) + "$"; //��һ���ս����ʵ�־�ȷƥ��
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
		
		// �����pattern�ܱ�����pattern��ƥ�䣬��Ҫ��������
		for (MatchingStep2 step2 : step1.values()){
			String example = step2.step1Example;
				for (String otherStep2Pattern: step1.keySet()){
					MatchingStep2 otherStep2 = step1.get(otherStep2Pattern);
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
	 * ����ƥ���жϣ�URL���԰���Э��ͷ��Ҳ���Բ�������ƥ��ʱ�Դ�Сд�����С�
	 * @param url	��Ҫ����ƥ���жϵ�URL
	 * @return	ƥ������һ������
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
	 * ��URL������飺������б��ǰ����������˿ں��ʺţ�б�ߺ����·���Ͳ�����
	 * �������http://www.news.com/read/daily/headline.html�����ص��ǣ�
	 * moc.swen.www��read/daily/headline.html���
	 *
	 * @param url	URL�����Դ�http://��Ҳ�ɲ���
	 * @return	��һ���Ƿ�����б��ǰ��ģ��ڶ�����б�ߺ���ġ������Ѿ���תΪСд��
	 */
	static protected List<String> splitURL(String url){
		String beforePart;
		String afterPart;
		List<String> l = new ArrayList<String>(2);
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
		l.add(reversedBeforePart);
		l.add(afterPart);
		return l;
	}

}

/**
 * �ڶ��׶ε�ƥ�䡣��һ�׶��Ƕ�б��ǰ��Ľ���ƥ�䣬�ڶ��׶������б�ߺ���ġ�
 * @author zhengmah
 *
 */
class MatchingStep2 implements Serializable{
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

