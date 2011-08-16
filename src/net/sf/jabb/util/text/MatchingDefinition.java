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

import java.util.List;

/**
 * Definition of how the matching will should be done. <br>
 * ƥ�䷽ʽ���塣
 * <p>
 * It consists of three parts:
 * <ul>
 * 	<li><code>regularExpression</code>: The regular expression used for matching.</li>
 * 	<li><code>exactMatchExamples</code>: A list of example strings that could and 
 * 		only could match the regular expression. These example strings will 
 * 		be used to exercise the matching engine, so they should	cover all the 
 * 		possibilities of matching.</li>
 * 	<li><code>exactMatchExample</code>: An example string that could and only could 
 * 		match the regular expression. The example string will be used to exercise the 
 * 		matching engine, so it should cover all the possibilities of matching.</li>
 * 	<li><code>attachment</code>: An attachment object that should be associated 
 * 		with this matching. If matched, this object will be returned.</li>
 * </ul>
 * Only one of <code>exactMatchExamples</code> and <code>exactMatchExample</code>
 * need to be not null.
 * 
 * <p>
 * ��������������ɣ�
 * <ul>
 * 	<li><code>regularExpression</code>: ����ƥ���������ʽ��</li>
 * 	<li><code>exactMatchExamples</code>: �ܹ��պ�ƥ���������ʽ��һ��ʾ���ַ�����
 * 		��Щ�ַ�����������ѵ����ƥ�����棬����������Ҫ�ܸ���ȫ����ƥ��״̬��</li>
 * 	<li><code>exactMatchExample</code>: �ܹ��պ�ƥ���������ʽ��һ��ʾ���ַ�����
 * 		����ַ�����������ѵ����ƥ�����棬 ��������Ҫ�ܸ���ȫ����ƥ��״̬��</li>
 * 	<li><code>attachment</code>: ���ƥ�������������һ���������������������
 * 		���ƥ�䣬���ᱻ��Ϊƥ��Ľ�����ء�</li>
 * </ul>
 * <code>exactMatchExamples</code>��<code>exactMatchExample</code>ֻ��Ҫ��һ��Ϊ��null��
 * 
 * 
 * @author Zhengmao HU (James)
 *
 */
public class MatchingDefinition {
	/**
	 * The regular expression used for matching.
	 * <br>����ƥ���������ʽ��
	 */
	private String regularExpression;
	/**
	 * A list of example strings that could and only could match the regular expression.<br>
	 * �ܹ��պ�ƥ���������ʽ��һ��ʾ���ַ�����
	 * <p>
	 * These example strings will be used to exercise the matching engine, so they should
	 * cover all the possibilities of matching.
	 * <p>
	 * ��Щ�ַ�����������ѵ����ƥ�����棬����������Ҫ�ܸ���ȫ����ƥ��״̬��
	 */
	private List<String> exactMatchExamples;
	
	/**
	 * An example string that could and only could match the regular expression.<br>
	 * �ܹ��պ�ƥ���������ʽ��һ��ʾ���ַ�����
	 * <p>
	 * The example string will be used to exercise the matching engine, so it should
	 * cover all the possibilities of matching.
	 * <p>
	 * ����ַ�����������ѵ����ƥ�����棬��������Ҫ�ܸ���ȫ����ƥ��״̬��
	 */
	private String exactMatchExample;
	/**
	 * An attachment object that should be associated with this matching.<br>
	 * ���ƥ�������������һ���������������������
	 * <p>
	 * If matched, this object will be returned.
	 * <p>
	 * ���ƥ�䣬���ᱻ��Ϊƥ��Ľ�����ء�
	 */
	private Object attachment;
	
	public String toString(){
		StringBuilder sb = new StringBuilder();
		sb.append("[");
		sb.append(regularExpression);
		sb.append(" -> ");
		sb.append(attachment);
		sb.append(" | ");
		if (exactMatchExample != null){
			sb.append(exactMatchExample);
		}
		List<String> exampleList = exactMatchExamples;
		if (exampleList != null){
			sb.append(exactMatchExamples);
		}
		sb.append("]\n");

		return sb.toString();
		
	}

	public String getRegularExpression() {
		return regularExpression;
	}
	public void setRegularExpression(String regularExpression) {
		this.regularExpression = regularExpression;
	}
	public List<String> getExactMatchExamples() {
		return exactMatchExamples;
	}
	public void setExactMatchExamples(List<String> exactExample) {
		this.exactMatchExamples = exactExample;
	}
	public String getExactMatchExample() {
		return exactMatchExample;
	}
	public void setExactMatchExample(String exactMatchExample) {
		this.exactMatchExample = exactMatchExample;
	}
	public Object getAttachment() {
		return attachment;
	}
	public void setAttachment(Object attachment) {
		this.attachment = attachment;
	}

}
