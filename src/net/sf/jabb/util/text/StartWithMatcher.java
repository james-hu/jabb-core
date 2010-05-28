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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import dk.brics.automaton.Automaton;
import dk.brics.automaton.BasicOperations;
import dk.brics.automaton.RegExp;
import dk.brics.automaton.RunAutomaton;

/**
 * �������ĳһ���ַ�����ͬһ��������ʽ�е���һ�����ϡ��Դ˿�ͷ����ʽ��ƥ�䡣
 * ƥ����ַ����ĵ�һ���ַ���ʼ��ֻҪ��ͷһ������ƥ��ĳ��������ʽ��������ƥ��ɹ���
 * <p>
 * To test which regular expression among several others can be matched by a given string
 * in the manner of "start with". If one of the regular expression can be matched from 
 * the first character of the string, they are considered to be matched.
 * 
 * <p>
 * ���磺
 * <br>For example:
 * <br>
 * <br>I like eating Chinese food --- matched by ---> I .*
 * <br>I like eating Chinese food --- matched by ---> I like eating.*
 * <br>I like eating Chinese food --- matched by ---> I
 * <br>I like eating Chinese food --- matched by ---> I li
 * <br>I like eating Chinese food --- matched by ---> I like
 * <br>I like eating Chinese food --- matched by ---> I like eating
 * <br>I like eating Chinese food --- matched by ---> .* like
 * <br>I like eating Chinese food --- matched by ---> .* food
 * <br>I like eating Chinese food --- matched by ---> I .* eating
 *
 * <p>
 * һ����˵����Ҫֱ��������࣬Ӧ��ʹ���������࣬����{@link StringStartWithMatcher}��
 * {@link UrlStartWithMatcher}��
 * <p>
 * Normally you don't need to use this class directly, using of its subclasses like
 * {@link StringStartWithMatcher} or {@link UrlStartWithMatcher} is preferred.
 * 
 * <p>
 * �ײ�ʵ�ֻ���dk.brics.automaton.RunAutomaton��
 * <p>
 * It's underlying implementation is based on dk.brics.automaton.RunAutomaton
 * 
 * @author Zhengmao HU (James)
 *
 */
public class StartWithMatcher implements Serializable{
	private static final long serialVersionUID = -6180680492778552560L;
	/**
	 * ʵ��ƥ��״̬��������
	 */
	protected RunAutomaton runAutomaton;
	/**
	 * runAutomation��ÿһ��state��Ӧһ��attachment����
	 */
	protected Object[] attachments;
	
	/**
	 * ����һ�����������������ԭ�ȵĶ��������ȫ��ͬƥ�䷽ʽ��
	 * <p>
	 * Create a copy, the copy will have exactly the same matching 
	 * definitions as the original copy.
	 * 
	 * @param toBeCopied	ԭ����<br>The original copy.
	 */
	public StartWithMatcher(StartWithMatcher toBeCopied){
		this.attachments = toBeCopied.attachments;
		try {
			this.runAutomaton = toBeCopied.copyRunAutomaton();
		} catch (Exception e) {
			throw new UnsupportedOperationException(e);
		}
	}

	/**
	 * ����ƥ�䷽ʽ���崴��һ���µĶ���ʵ����
	 * �ڴ����ڲ����ݽṹ��ʱ��ѡ��ռ�ø����ڴ棬����ȡ�ٶ��ϵ�������
	 * <p>
	 * Create an instance according to matching definitions, when creating internal
	 * data structure, choose to consume more memory for better matching speed.
	 * 
	 * @param definitionList  һ��ƥ�䷽ʽ���塣<br>Matching definitions
	 */
	public StartWithMatcher(Collection<MatchingDefinition> definitionList){
		this.initialize(definitionList, true);
	}

	/**
	 * ����ƥ�䷽ʽ���崴��һ���µĶ���ʵ����
	 * <p>
	 * Create an instance according to matching definitions.
	 * 
	 * @param definitionList һ��ƥ�䷽ʽ���塣<br>Matching definitions
	 * @param moreSpaceForSpeed  �Ƿ�ռ�ø����ڴ棬����ȡ�ٶ��ϵ�������
	 * 								<br>Whether or not to consume
	 * 								more memory for better matching speed.
	 */
	public StartWithMatcher(Collection<MatchingDefinition> definitionList, boolean moreSpaceForSpeed){
		this.initialize(definitionList, moreSpaceForSpeed);
	}

	
	/**
	 * ��ʼ��״̬����dk.brics.automaton.RunAutomaton����
	 * <p>
	 * Initialize the state machine (dk.brics.automaton.RunAutomaton).
	 * 
	 * @param definitionList	һ��ƥ�䷽ʽ���塣<br>Matching definitions
	 * @param moreSpaceForSpeed	�Ƿ�ռ�ø����ڴ棬����ȡ�ٶ��ϵ�������
	 * 								<br>Whether or not to consume
	 * 								more memory for better matching speed.
	 */
	protected void initialize(Collection<MatchingDefinition> definitionList, boolean moreSpaceForSpeed){
		// ������������ʽunion����
		List<Automaton> list = new ArrayList<Automaton>(definitionList.size());
		for (MatchingDefinition c: definitionList) {
			Automaton a = new RegExp(c.getRegularExpression()).toAutomaton();
			list.add(a);
		}
		Automaton a = BasicOperations.union(list);
		runAutomaton = new RunAutomaton(a, moreSpaceForSpeed);
		
		// ���þ�ȷƥ��ʾ���ַ������ҵ���Ӧ��state����������������֮����
		attachments = new Object[runAutomaton.getSize()];
		for (MatchingDefinition c: definitionList) {
			String exampleString = c.getExactMatchExample();
			if (exampleString != null){
				setAttachmentByExample(exampleString, c.getAttachment(), c.getRegularExpression());
			}
			List<String> exampleList = c.getExactMatchExamples();
			if (exampleList != null){
				for (String example: exampleList){
					setAttachmentByExample(example, c.getAttachment(), c.getRegularExpression());
				}
			}
		}
		
		/*
		// ����Ƿ�ÿ��״̬���и���
		for (int i = 0; i < runAutomaton.getSize(); i ++){
			if (runAutomaton.isAccept(i) && attachments[i] != null){
				// it is okay
			}else{
				// not covered
				System.out.println(i);
				
				//throw new IllegalArgumentException("State " + i + " can be accepted but has no attachment. " 
				//		+ "Please check if all states can be covered by example strings provided.");
				
			}
		}
		*/
	}
	
	/**
	 * ����ʾ���ַ��������ø�״̬����Ӧ�ĸ�������
	 * <p>
	 * Set attachment object for each state, by testing which state the example string can run into.
	 * 
	 * @param example	ʾ���ַ���	<br>Example string.
	 * @param att		��������		<br>Attachment object.
	 * @param exp		������ʽ�ַ��������������׳����쳣��Ϣ�У�
	 * 					<br>Regular expression which will only be used within the exception message. 
	 */
	protected void setAttachmentByExample(String example, Object att, String exp){
		int p = getLastAcceptedState(example, 0);
		if (p != -1){
			this.attachments[p] = att;
		}else{
			// ˵����ȷƥ��ʾ���ַ����д���
			throw new IllegalArgumentException("\"" + example + "\" can not match \"" + exp + "\"");
		}
	}

	/**
	 * ����״̬����dk.brics.automaton.RunAutomaton����ȡ�����һ��ƥ�䵽��״̬��
	 * <p>
	 * By utilizing the state machine (dk.brics.automaton.RunAutomaton), get the last accepted matching state.
	 * 
	 * @param text	������ƥ������ı���<br>The text to be tested.
	 * @return	ƥ�䵽��״̬��ţ�����-1��ʾû�����κ�һ��������ʽ��ƥ�䡣
	 * 			<br>The state number that matched, return -1 if no expression can match the text. 
	 */
	protected int getLastAcceptedState(CharSequence text){
		return getLastAcceptedState(text, 0);
	}

	/**
	 * ����״̬����dk.brics.automaton.RunAutomaton����ȡ�����һ��ƥ�䵽��״̬����ָ��λ�ÿ�ʼ����ƥ���顣
	 * <p>
	 * By utilizing the state machine (dk.brics.automaton.RunAutomaton), get the last accepted matching state.
	 * The matching test starts at specified position.
	 * 
	 * @param text	������ƥ������ı���<br>The text to be tested.
	 * @param startIndex		���ı������λ�ÿ�ʼƥ�䡣<br>The position to start matching test.
	 * @return	ƥ�䵽��״̬��ţ�����-1��ʾû�����κ�һ��������ʽ��ƥ�䡣
	 * 			<br>The state number that matched, return -1 if no expression can match the text. 
	 */
	protected int getLastAcceptedState(CharSequence text, int startIndex){
		int lastAcceptedState = -1;
		
		int p = runAutomaton.getInitialState();
		int l = text.length();
		for (int i = startIndex; i < l; i++) {
			p = runAutomaton.step(p, text.charAt(i));
			if (p == -1) {
				if (lastAcceptedState == -1){
					return -1;
				}else{
					break;
				}
			}
			if (runAutomaton.isAccept(p)){
				lastAcceptedState = p;
			}
		}
		return lastAcceptedState;
	}
	
	/**
	 * ��ָ��λ�ÿ�ʼ�ж��ı��ַ����Ƿ���Ա�����һ��������ʽ��ƥ�䣬ע�⣺ֻҪ��ͷƥ���˾���ƥ�䡣
	 * <p>
	 * Test if the text string can be matched by any of the regular expression. The test
	 * starts from the specified position.
	 * Caution: if the beginning part matches, the whole text is considered to match. 
	 * 
	 * @param text	������ַ�������ƥ���жϡ�<br>Text string to be tested for matching.
	 * @param startIndex ���ı������λ�ÿ�ʼƥ�䡣<br>The starting position of the testing.
	 * @return	ƥ�䵽���Ǹ�������ʱ����Ӧ�ĸ���������null��ʾû��ƥ�䵽�κ�һ��������ʽ��
	 * 			<br>Attachment object of the regular expression that matches the text string.
	 * 				null is returned if no matching can be found.
	 */
	public Object match(CharSequence text, int startIndex){
		int p = getLastAcceptedState(text, startIndex);
		return (p == -1) ? null : attachments[p];
	}

	/**
	 * �ж��ı��ַ����Ƿ���Ա�����һ��������ʽ��ƥ�䣬ע�⣺ֻҪ��ͷƥ���˾���ƥ�䡣
	 * <p>
	 * Test if the text string can be matched by any of the regular expression. 
	 * Caution: if the beginning part matches, the whole text is considered to match. 
	 * 
	 * @param text	������ַ�������ƥ���жϡ�<br>Text string to be tested for matching.
	 * @return	ƥ�䵽���Ǹ�������ʱ����Ӧ�ĸ���������null��ʾû��ƥ�䵽�κ�һ��������ʽ��
	 * 			<br>Attachment object of the regular expression that matches the text string.
	 * 				null is returned if no matching can be found.
	 */
	public Object match(CharSequence text){
		return this.match(text, 0);
	}

	/**
	 * ��������ʽ�﷨�����ַ�������escape��
	 * <p>
	 * Escape special characters according to syntax of regular expression.
	 * 
	 * @param s	The string to be escaped
	 * @return	The result of escaping
	 */
	static protected String escapeForRegExp(String s){
		String r = s; 
		if (r != null && r.length() > 0){
			for (String c: new String[]{"\\", "|", "&", "?", "*", "+", "{", "}",
					"[", "]", "~", ".", "#", "@", "\"", "(", ")", "<", ">",
					"^"}){
				r = r.replace(c, "\\" + c);
			}
		}
		return r;
	}
	
	/**
	 * ���Լ��ڲ���״̬����dk.brics.automaton.RunAutomaton��������һ�ݡ�
	 * <p>
	 * Make a copy of the internal state machine (dk.brics.automaton.RunAutomaton) of this instance.
	 * 
	 * @return	a copy made by serializing and then de-serializing
	 * @throws IOException
	 * @throws ClassNotFoundException
	 */
	protected RunAutomaton copyRunAutomaton() throws IOException, ClassNotFoundException{
		RunAutomaton result;
		
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		ObjectOutputStream oos = new ObjectOutputStream(baos);
		oos.writeObject(this.runAutomaton);
		byte[] binary = baos.toByteArray();
		ByteArrayInputStream bais = new ByteArrayInputStream(binary);
		ObjectInputStream ois = new ObjectInputStream(bais);
		
		result = (RunAutomaton) ois.readObject();
		oos.close();
		baos.close();
		ois.close();
		bais.close();
		
		return result;
	}
	
}
