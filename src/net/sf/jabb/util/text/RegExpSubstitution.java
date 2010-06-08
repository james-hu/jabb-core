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
import java.util.Collection;
import java.util.List;

import dk.brics.automaton.Automaton;
import dk.brics.automaton.AutomatonMatcher;
import dk.brics.automaton.BasicOperations;
import dk.brics.automaton.RegExp;
import dk.brics.automaton.RunAutomaton;

/**
 * 对正则表达式所匹配到的内容作替换。注意这里的正则表达式不支持“^”和“$”。
 * @author Zhengmao HU (James)
 *
 */
public class RegExpSubstitution implements Serializable{
	private static final long serialVersionUID = 7358024769261165557L;

	protected RunAutomaton runAutomation;
	protected String replacement;
	static protected final boolean moreSpaceForSpeed = true;

	/**
	 * 构造方法
	 * @param replaceStr	替换为这个字符串
	 * @param regExp		匹配到这个正则表达式的部分会被替换
	 */
	public RegExpSubstitution(String replaceStr, String regExp){
		replacement = replaceStr;
		runAutomation = new RunAutomaton(new RegExp(regExp).toAutomaton(), moreSpaceForSpeed);
	}
	
	/**
	 * 构造方法
	 * @param replaceStr	替换为这个字符串
	 * @param regExps		匹配到这些正则表达式当中任意一个的部分都会被替换
	 */
	public RegExpSubstitution(String replaceStr, String... regExps){
		replacement = replaceStr;
		
		List<Automaton> list = new ArrayList<Automaton>(regExps.length);
		for (String re: regExps){
			list.add(new RegExp(re).toAutomaton());
		}
		runAutomation = new RunAutomaton(BasicOperations.union(list), moreSpaceForSpeed);
	}
	
	/**
	 * 构造方法
	 * @param replaceStr	替换为这个字符串
	 * @param regExps		匹配到这些正则表达式当中任意一个的部分都会被替换
	 */
	public RegExpSubstitution(String replaceStr, Collection<String> regExps){
		replacement = replaceStr;
		
		List<Automaton> list = new ArrayList<Automaton>(regExps.size());
		for (String re: regExps){
			list.add(new RegExp(re).toAutomaton());
		}
		runAutomation = new RunAutomaton(BasicOperations.union(list), moreSpaceForSpeed);
	}
	
	protected String replace(CharSequence text, boolean firstOnly){
		StringBuilder sb = new StringBuilder();
		AutomatonMatcher am = runAutomation.newMatcher(text);
		int lastEnd = 0;
		while (am.find() && (firstOnly == false || lastEnd == 0)){
			sb.append(text, lastEnd, am.start());
			lastEnd = am.end();
			sb.append(replacement);
		}
		sb.append(text, lastEnd, text.length());
		return sb.toString();
	}
	
	/**
	 * 只替换第一个出现的地方
	 * @param text
	 * @return		替换后的结果。如果没有发生替换，则结果与原来输入的字符串完全相同。
	 */
	public String replaceFirst(CharSequence text){
		return replace(text, true);
	}
	
	/**
	 * 替换所有出现的地方
	 * @param text
	 * @return		替换后的结果。如果没有发生替换，则结果与原来输入的字符串完全相同。
	 */
	public String replaceAll(CharSequence text){
		return replace(text, false);
	}
	
	/**
	 * 替换掉最后一个出现的地方
	 * @param text
	 * @return		替换后的结果。如果没有发生替换，则结果与原来输入的字符串完全相同。
	 */
	public String replaceLast(CharSequence text){
		StringBuilder sb = new StringBuilder();
		AutomatonMatcher am = runAutomation.newMatcher(text);
		int start = 0;
		int end = 0;
		while (am.find()){
			start = am.start();
			end = am.end();
		}
		if (start != 0 || end != 0){
			sb.append(text, 0, start);
			sb.append(replacement);
			sb.append(text, end, text.length());
		}else{
			sb.append(text);
		}
		return sb.toString();
	}

}
