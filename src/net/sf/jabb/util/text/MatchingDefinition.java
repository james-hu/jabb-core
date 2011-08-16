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
 * 匹配方式定义。
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
 * 它由三项数据组成：
 * <ul>
 * 	<li><code>regularExpression</code>: 用作匹配的正则表达式。</li>
 * 	<li><code>exactMatchExamples</code>: 能够刚好匹配此正则表达式的一组示范字符串。
 * 		这些字符串被用来“训练”匹配引擎，所以它们需要能覆盖全部的匹配状态。</li>
 * 	<li><code>exactMatchExample</code>: 能够刚好匹配此正则表达式的一个示范字符串。
 * 		这个字符串被用来“训练”匹配引擎， 所以它需要能覆盖全部的匹配状态。</li>
 * 	<li><code>attachment</code>: 与此匹配条件相关联的一个附件，可以是任意对象。
 * 		如果匹配，它会被作为匹配的结果返回。</li>
 * </ul>
 * <code>exactMatchExamples</code>和<code>exactMatchExample</code>只需要有一个为非null。
 * 
 * 
 * @author Zhengmao HU (James)
 *
 */
public class MatchingDefinition {
	/**
	 * The regular expression used for matching.
	 * <br>用作匹配的正则表达式。
	 */
	private String regularExpression;
	/**
	 * A list of example strings that could and only could match the regular expression.<br>
	 * 能够刚好匹配此正则表达式的一组示范字符串。
	 * <p>
	 * These example strings will be used to exercise the matching engine, so they should
	 * cover all the possibilities of matching.
	 * <p>
	 * 这些字符串被用来“训练”匹配引擎，所以它们需要能覆盖全部的匹配状态。
	 */
	private List<String> exactMatchExamples;
	
	/**
	 * An example string that could and only could match the regular expression.<br>
	 * 能够刚好匹配此正则表达式的一个示范字符串。
	 * <p>
	 * The example string will be used to exercise the matching engine, so it should
	 * cover all the possibilities of matching.
	 * <p>
	 * 这个字符串被用来“训练”匹配引擎，所以它需要能覆盖全部的匹配状态。
	 */
	private String exactMatchExample;
	/**
	 * An attachment object that should be associated with this matching.<br>
	 * 与此匹配条件相关联的一个附件，可以是任意对象。
	 * <p>
	 * If matched, this object will be returned.
	 * <p>
	 * 如果匹配，它会被作为匹配的结果返回。
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
