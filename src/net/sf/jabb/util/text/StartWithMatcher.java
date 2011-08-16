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
 * 用来检查某一个字符串能同一组正则表达式中的哪一个符合“以此开头”形式的匹配。
 * <p>
 * If one of the regular expression can be matched from 
 * the first character of the string, they are considered to be matched.
 * <br>For example:
 * <p>
 * 匹配从字符串的第一个字符开始，只要开头一段完整匹配某个正则表达式，就算是匹配成功。
 * <br>比如：
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
 * 一般来说不需要直接用这个类，应该使用它的子类，比如{@link StringStartWithMatcher}或
 * {@link UrlStartWithMatcher}。
 * 
 * <p>
 * It's underlying implementation is based on dk.brics.automaton.RunAutomaton
 * <p>
 * 底层实现基于dk.brics.automaton.RunAutomaton。
 * 
 * @author Zhengmao HU (James)
 *
 */
public class StartWithMatcher implements Serializable{
	private static final long serialVersionUID = -6180680492778552560L;
	/**
	 * 实现匹配状态机的引擎
	 */
	protected RunAutomaton runAutomaton;
	/**
	 * runAutomation的每一个state对应一个attachment对象。
	 */
	protected Object[] attachments;
	
	/**
	 * Create a copy, the copy will have exactly the same matching 
	 * definitions as the original copy.<br>
	 * 创建一个副本，这个副本与原先的对象具有完全相同匹配方式。
	 * 
	 * @param toBeCopied	The original copy.<br>原本。
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
	 * 根据匹配方式定义创建一个新的对象实例；
	 * 在创建内部数据结构的时候，选择占用更多内存，而换取速度上的提升。
	 * 
	 * @param definitionList  Matching definitions<br>一组匹配方式定义。
	 */
	public StartWithMatcher(Collection<MatchingDefinition> definitionList){
		this.initialize(definitionList, true);
	}

	/**
	 * Create an instance according to matching definitions.<br>
	 * 根据匹配方式定义创建一个新的对象实例。
	 * 
	 * @param definitionList 	Matching definitions<br>一组匹配方式定义。
	 * @param moreSpaceForSpeed  Whether or not to consume more memory for better matching speed.<br>
	 * 							是否占用更多内存，而换取速度上的提升。
	 */
	public StartWithMatcher(Collection<MatchingDefinition> definitionList, boolean moreSpaceForSpeed){
		this.initialize(definitionList, moreSpaceForSpeed);
	}

	
	/**
	 * Initialize the state machine (dk.brics.automaton.RunAutomaton).<br>
	 * 初始化状态机（dk.brics.automaton.RunAutomaton）。
	 * 
	 * @param definitionList 	Matching definitions<br>一组匹配方式定义。
	 * @param moreSpaceForSpeed  Whether or not to consume more memory for better matching speed.<br>
	 * 							是否占用更多内存，而换取速度上的提升。
	 */
	protected void initialize(Collection<MatchingDefinition> definitionList, boolean moreSpaceForSpeed){
		// 把所有正则表达式union起来
		List<Automaton> list = new ArrayList<Automaton>(definitionList.size());
		for (MatchingDefinition c: definitionList) {
			Automaton a = new RegExp(c.getRegularExpression()).toAutomaton();
			list.add(a);
		}
		Automaton a = BasicOperations.union(list);
		runAutomaton = new RunAutomaton(a, moreSpaceForSpeed);
		
		// 利用精确匹配示范字符串，找到对应的state，并将附件对象与之关联
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
		// 检查是否每个状态都有覆盖
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
	 * 根据示范字符串，设置各状态所对应的附件对象。
	 * 
	 * @param example	Example string.<br>示范字符串
	 * @param att		Attachment object.<br>附件对象
	 * @param exp		Regular expression which will only be used within the exception message.<br>
	 * 					正则表达式字符串（仅被用在抛出的异常消息中）
	 */
	protected void setAttachmentByExample(String example, Object att, String exp){
		int p = getLastAcceptedState(example, 0);
		if (p != -1){
			this.attachments[p] = att;
		}else{
			// 说明精确匹配示范字符串有错误
			throw new IllegalArgumentException("\"" + example + "\" can not match \"" + exp + "\"");
		}
	}

	/**
	 * By utilizing the state machine (dk.brics.automaton.RunAutomaton), get the last accepted matching state.<br>
	 * 利用状态机（dk.brics.automaton.RunAutomaton），取得最后一个匹配到的状态。
	 * 
	 * @param text	The text to be tested.<br>待进行匹配检查的文本。
	 * @return	The state number that matched, return -1 if no expression can match the text.<br>
	 * 			匹配到的状态编号，返回-1表示没有与任何一个正则表达式相匹配。
	 */
	protected int getLastAcceptedState(CharSequence text){
		return getLastAcceptedState(text, 0);
	}

	/**
	 * By utilizing the state machine (dk.brics.automaton.RunAutomaton), get the last accepted matching state.
	 * The matching test starts at specified position.<br>
	 * 利用状态机（dk.brics.automaton.RunAutomaton），取得最后一个匹配到的状态。从指定位置开始进行匹配检查。
	 * 
	 * @param text				The text to be tested.<br>待进行匹配检查的文本。
	 * @param startIndex		The position to start matching test.<br>从文本的这个位置开始匹配。
	 * @return	The state number that matched, return -1 if no expression can match the text.<br>
	 * 			匹配到的状态编号，返回-1表示没有与任何一个正则表达式相匹配。
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
	 * 利用状态机（dk.brics.automaton.RunAutomaton），取得所有匹配到的状态。从指定位置开始进行匹配检查。
	 * 
	 * @param text				The text to be tested.<br>待进行匹配检查的文本。
	 * @param startIndex		The position to start matching test.<br>从文本的这个位置开始匹配。
	 * @return	List of the state numbers that are matched, return null if no expression can match the text.<br>
	 * 			匹配到的所有状态编号的列表，返回null表示没有与任何一个正则表达式相匹配。
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
	 * 从指定位置开始判断文本字符串是否可以被任意一个正则表达式所匹配，
	 * 返回所能匹配到的那个最长正则表达式的附件对象。
	 * <p>
	 * The test starts from the specified position.
	 * Caution: if the beginning part matches, the whole text is considered to match. 
	 * <p>
	 * 匹配从指定的位置开始。 注意：只要开头匹配了就算匹配。
	 * 
	 * @param text			Text string to be tested for matching.<br>对这个字符串进行匹配判断。
	 * @param startIndex 	The starting position of the testing.<br>从文本的这个位置开始匹配。
	 * @return	Attachment object of the regular expression that matches the text string;
	 * 			null is returned if no matching can be found.<br>
	 * 			匹配到的那个正则表达式所对应的附件；返回null表示没有匹配到任何一个正则表达式。
	 */
	public Object match(CharSequence text, int startIndex){
		int p = getLastAcceptedState(text, startIndex);
		return (p == -1) ? null : attachments[p];
	}
	
	/**
	 * Begin from the specified position, test if the text string can be matched 
	 * by any of the regular expression,
	 * return list of the attachments of all the matching regular expressions.<br>
	 * 从指定位置开始判断文本字符串是否可以被任意一个正则表达式所匹配，
	 * 返回所有能匹配到的正则表达式的附件对象的列表。
	 * <p>
	 * The test starts from the specified position.
	 * Caution: if the beginning part matches, the whole text is considered to match. 
	 * <p>
	 * 匹配从指定的位置开始。 注意：只要开头匹配了就算匹配。
	 * 
	 * @param text			Text string to be tested for matching.<br>对这个字符串进行匹配判断。
	 * @param startIndex 	The starting position of the testing.<br>从文本的这个位置开始匹配。
	 * @return	List of attachment objects of the regular expressions that matche the text string;
	 * 			null is returned if no matching can be found.<br>
	 * 			匹配到的那些正则表达式所对应的附件的列表；返回null表示没有匹配到任何一个正则表达式。
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
	 * 判断文本字符串是否可以被任意一个正则表达式所匹配。
	 * <p>
	 * The test starts from the beginning.
	 * Caution: if the beginning part matches, the whole text is considered to match. 
	 * <p>
	 * 匹配从头开始。 注意：只要开头匹配了就算匹配。
	 * 
	 * @param text	Text string to be tested for matching.<br>对这个字符串进行匹配判断。
	 * @return	Attachment object of the regular expression that matches the text string;
	 * 			null is returned if no matching can be found.<br>
	 * 			匹配到的那个正则表达式所对应的附件。返回null表示没有匹配到任何一个正则表达式。
	 */
	public Object match(CharSequence text){
		return this.match(text, 0);
	}
	
	/**
	 * Test if the text string can be matched by any of the regular expression,
	 * return list of the attachments of all the matching regular expressions.<br>
	 * 判断文本字符串是否可以被任意一个正则表达式所匹配，
	 * 返回所有能匹配到的正则表达式的附件对象的列表。
	 * <p>
	 * The test starts from the beginning.
	 * Caution: if the beginning part matches, the whole text is considered to match. 
	 * <p>
	 * 匹配从头开始。 注意：只要开头匹配了就算匹配。
	 * 
	 * @param text			Text string to be tested for matching.<br>对这个字符串进行匹配判断。
	 * @return	List of attachment objects of the regular expressions that matche the text string;
	 * 			null is returned if no matching can be found.<br>
	 * 			匹配到的那些正则表达式所对应的附件的列表；返回null表示没有匹配到任何一个正则表达式。
	 */
	public List<Object> matchAll(CharSequence text){
		return this.matchAll(text, 0);
	}


	/**
	 * Escape special characters according to syntax of regular expression.<br>
	 * 按正则表达式语法，对字符串进行escape。
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
	 * 把自己内部的状态机（dk.brics.automaton.RunAutomaton）对象复制一份。
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
