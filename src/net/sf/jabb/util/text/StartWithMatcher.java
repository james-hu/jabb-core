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
 * To test which regular expression among several others can be matched by a given string
 * in the manner of "start with".<br>
 * �������ĳһ���ַ�����ͬһ��������ʽ�е���һ�����ϡ��Դ˿�ͷ����ʽ��ƥ�䡣
 * <p>
 * If one of the regular expression can be matched from 
 * the first character of the string, they are considered to be matched.
 * <br>For example:
 * <p>
 * ƥ����ַ����ĵ�һ���ַ���ʼ��ֻҪ��ͷһ������ƥ��ĳ��������ʽ��������ƥ��ɹ���
 * <br>���磺
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
 * Normally you don't need to use this class directly, using of its subclasses like
 * {@link StringStartWithMatcher} or {@link UrlStartWithMatcher} is preferred.
 * <p>
 * һ����˵����Ҫֱ��������࣬Ӧ��ʹ���������࣬����{@link StringStartWithMatcher}��
 * {@link UrlStartWithMatcher}��
 * 
 * <p>
 * It's underlying implementation is based on dk.brics.automaton.RunAutomaton
 * <p>
 * �ײ�ʵ�ֻ���dk.brics.automaton.RunAutomaton��
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
	 * Create a copy, the copy will have exactly the same matching 
	 * definitions as the original copy.<br>
	 * ����һ�����������������ԭ�ȵĶ��������ȫ��ͬƥ�䷽ʽ��
	 * 
	 * @param toBeCopied	The original copy.<br>ԭ����
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
	 * Create an instance according to matching definitions, when creating internal
	 * data structure, choose to consume more memory for better matching speed.<br>
	 * ����ƥ�䷽ʽ���崴��һ���µĶ���ʵ����
	 * �ڴ����ڲ����ݽṹ��ʱ��ѡ��ռ�ø����ڴ棬����ȡ�ٶ��ϵ�������
	 * 
	 * @param definitionList  Matching definitions<br>һ��ƥ�䷽ʽ���塣
	 */
	public StartWithMatcher(Collection<MatchingDefinition> definitionList){
		this.initialize(definitionList, true);
	}

	/**
	 * Create an instance according to matching definitions.<br>
	 * ����ƥ�䷽ʽ���崴��һ���µĶ���ʵ����
	 * 
	 * @param definitionList 	Matching definitions<br>һ��ƥ�䷽ʽ���塣
	 * @param moreSpaceForSpeed  Whether or not to consume more memory for better matching speed.<br>
	 * 							�Ƿ�ռ�ø����ڴ棬����ȡ�ٶ��ϵ�������
	 */
	public StartWithMatcher(Collection<MatchingDefinition> definitionList, boolean moreSpaceForSpeed){
		this.initialize(definitionList, moreSpaceForSpeed);
	}

	
	/**
	 * Initialize the state machine (dk.brics.automaton.RunAutomaton).<br>
	 * ��ʼ��״̬����dk.brics.automaton.RunAutomaton����
	 * 
	 * @param definitionList 	Matching definitions<br>һ��ƥ�䷽ʽ���塣
	 * @param moreSpaceForSpeed  Whether or not to consume more memory for better matching speed.<br>
	 * 							�Ƿ�ռ�ø����ڴ棬����ȡ�ٶ��ϵ�������
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
	 * Set attachment object for each state, by testing which state the example string can run into.<br>
	 * ����ʾ���ַ��������ø�״̬����Ӧ�ĸ�������
	 * 
	 * @param example	Example string.<br>ʾ���ַ���
	 * @param att		Attachment object.<br>��������
	 * @param exp		Regular expression which will only be used within the exception message.<br>
	 * 					������ʽ�ַ��������������׳����쳣��Ϣ�У�
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
	 * By utilizing the state machine (dk.brics.automaton.RunAutomaton), get the last accepted matching state.<br>
	 * ����״̬����dk.brics.automaton.RunAutomaton����ȡ�����һ��ƥ�䵽��״̬��
	 * 
	 * @param text	The text to be tested.<br>������ƥ������ı���
	 * @return	The state number that matched, return -1 if no expression can match the text.<br>
	 * 			ƥ�䵽��״̬��ţ�����-1��ʾû�����κ�һ��������ʽ��ƥ�䡣
	 */
	protected int getLastAcceptedState(CharSequence text){
		return getLastAcceptedState(text, 0);
	}

	/**
	 * By utilizing the state machine (dk.brics.automaton.RunAutomaton), get the last accepted matching state.
	 * The matching test starts at specified position.<br>
	 * ����״̬����dk.brics.automaton.RunAutomaton����ȡ�����һ��ƥ�䵽��״̬����ָ��λ�ÿ�ʼ����ƥ���顣
	 * 
	 * @param text				The text to be tested.<br>������ƥ������ı���
	 * @param startIndex		The position to start matching test.<br>���ı������λ�ÿ�ʼƥ�䡣
	 * @return	The state number that matched, return -1 if no expression can match the text.<br>
	 * 			ƥ�䵽��״̬��ţ�����-1��ʾû�����κ�һ��������ʽ��ƥ�䡣
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
	 * By utilizing the state machine (dk.brics.automaton.RunAutomaton), get all the accepted matching state.
	 * The matching test starts at specified position.<br>
	 * ����״̬����dk.brics.automaton.RunAutomaton����ȡ������ƥ�䵽��״̬����ָ��λ�ÿ�ʼ����ƥ���顣
	 * 
	 * @param text				The text to be tested.<br>������ƥ������ı���
	 * @param startIndex		The position to start matching test.<br>���ı������λ�ÿ�ʼƥ�䡣
	 * @return	List of the state numbers that are matched, return null if no expression can match the text.<br>
	 * 			ƥ�䵽������״̬��ŵ��б�����null��ʾû�����κ�һ��������ʽ��ƥ�䡣
	 */
	protected List<Integer> getAllAcceptedStates(CharSequence text, int startIndex){
		List<Integer> states = new ArrayList<Integer>();
		int lastAcceptedState = -1;
		
		int p = runAutomaton.getInitialState();
		int l = text.length();
		for (int i = startIndex; i < l; i++) {
			p = runAutomaton.step(p, text.charAt(i));
			if (p == -1) {
				if (lastAcceptedState == -1){
					return null;
				}else{
					break;
				}
			}
			if (runAutomaton.isAccept(p)){
				states.add(p);
				lastAcceptedState = p;
			}
		}
		if (states.size() == 0){// this does happen, but why?
			return null;
		}
		return states;
	}

	
	/**
	 * Begin from the specified position, test if the text string can be matched 
	 * by any of the regular expression, 
	 * return the attachment of the longest matching one.<br>
	 * ��ָ��λ�ÿ�ʼ�ж��ı��ַ����Ƿ���Ա�����һ��������ʽ��ƥ�䣬
	 * ��������ƥ�䵽���Ǹ��������ʽ�ĸ�������
	 * <p>
	 * The test starts from the specified position.
	 * Caution: if the beginning part matches, the whole text is considered to match. 
	 * <p>
	 * ƥ���ָ����λ�ÿ�ʼ�� ע�⣺ֻҪ��ͷƥ���˾���ƥ�䡣
	 * 
	 * @param text			Text string to be tested for matching.<br>������ַ�������ƥ���жϡ�
	 * @param startIndex 	The starting position of the testing.<br>���ı������λ�ÿ�ʼƥ�䡣
	 * @return	Attachment object of the regular expression that matches the text string;
	 * 			null is returned if no matching can be found.<br>
	 * 			ƥ�䵽���Ǹ�������ʽ����Ӧ�ĸ���������null��ʾû��ƥ�䵽�κ�һ��������ʽ��
	 */
	public Object match(CharSequence text, int startIndex){
		int p = getLastAcceptedState(text, startIndex);
		return (p == -1) ? null : attachments[p];
	}
	
	/**
	 * Begin from the specified position, test if the text string can be matched 
	 * by any of the regular expression,
	 * return list of the attachments of all the matching regular expressions.<br>
	 * ��ָ��λ�ÿ�ʼ�ж��ı��ַ����Ƿ���Ա�����һ��������ʽ��ƥ�䣬
	 * ����������ƥ�䵽��������ʽ�ĸ���������б�
	 * <p>
	 * The test starts from the specified position.
	 * Caution: if the beginning part matches, the whole text is considered to match. 
	 * <p>
	 * ƥ���ָ����λ�ÿ�ʼ�� ע�⣺ֻҪ��ͷƥ���˾���ƥ�䡣
	 * 
	 * @param text			Text string to be tested for matching.<br>������ַ�������ƥ���жϡ�
	 * @param startIndex 	The starting position of the testing.<br>���ı������λ�ÿ�ʼƥ�䡣
	 * @return	List of attachment objects of the regular expressions that matche the text string;
	 * 			null is returned if no matching can be found.<br>
	 * 			ƥ�䵽����Щ������ʽ����Ӧ�ĸ������б�����null��ʾû��ƥ�䵽�κ�һ��������ʽ��
	 */
	public List<Object> matchAll(CharSequence text, int startIndex){
		List<Integer> states = getAllAcceptedStates(text, startIndex);
		if (states == null){
			return null;
		}else{
			List<Object> result = new ArrayList<Object>(states.size());
			for (int p: states){
				result.add(attachments[p]);
			}
			return result;
		}
	}

	/**
	 * Test if the text string can be matched by any of the regular expression. <br>
	 * �ж��ı��ַ����Ƿ���Ա�����һ��������ʽ��ƥ�䡣
	 * <p>
	 * The test starts from the beginning.
	 * Caution: if the beginning part matches, the whole text is considered to match. 
	 * <p>
	 * ƥ���ͷ��ʼ�� ע�⣺ֻҪ��ͷƥ���˾���ƥ�䡣
	 * 
	 * @param text	Text string to be tested for matching.<br>������ַ�������ƥ���жϡ�
	 * @return	Attachment object of the regular expression that matches the text string;
	 * 			null is returned if no matching can be found.<br>
	 * 			ƥ�䵽���Ǹ�������ʽ����Ӧ�ĸ���������null��ʾû��ƥ�䵽�κ�һ��������ʽ��
	 */
	public Object match(CharSequence text){
		return this.match(text, 0);
	}
	
	/**
	 * Test if the text string can be matched by any of the regular expression,
	 * return list of the attachments of all the matching regular expressions.<br>
	 * �ж��ı��ַ����Ƿ���Ա�����һ��������ʽ��ƥ�䣬
	 * ����������ƥ�䵽��������ʽ�ĸ���������б�
	 * <p>
	 * The test starts from the beginning.
	 * Caution: if the beginning part matches, the whole text is considered to match. 
	 * <p>
	 * ƥ���ͷ��ʼ�� ע�⣺ֻҪ��ͷƥ���˾���ƥ�䡣
	 * 
	 * @param text			Text string to be tested for matching.<br>������ַ�������ƥ���жϡ�
	 * @return	List of attachment objects of the regular expressions that matche the text string;
	 * 			null is returned if no matching can be found.<br>
	 * 			ƥ�䵽����Щ������ʽ����Ӧ�ĸ������б�����null��ʾû��ƥ�䵽�κ�һ��������ʽ��
	 */
	public List<Object> matchAll(CharSequence text){
		return this.matchAll(text, 0);
	}


	/**
	 * Escape special characters according to syntax of regular expression.<br>
	 * ��������ʽ�﷨�����ַ�������escape��
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
	 * Make a copy of the internal state machine (dk.brics.automaton.RunAutomaton) of this instance.<br>
	 * ���Լ��ڲ���״̬����dk.brics.automaton.RunAutomaton��������һ�ݡ�
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
