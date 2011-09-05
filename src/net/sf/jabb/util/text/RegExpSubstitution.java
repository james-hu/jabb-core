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
 * The utility to substitute part of the String that matches specified regular
 * expression(s) with another specified String.<br>
 * 一个工具类，它可以对一个或多个正则表达式所匹配到的字符串中的内容作替换。
 * <p>
 * An instance of this class can be used many times for substitution for different Strings.
 * The performance overhead of each time of substitution is very small. Therefore it is very suitble
 * to be used in occasions that the substitution criteria is fixed while the Strings to be substituted
 * are of huge volume.
 * Please be aware that regular expressions here do not support <code>"^"</code> and <code>"$"</code>
 * <p>
 * 这个类的一个实例可用来多次对不同的字符串进行替换，每次替换时的性能开销很小。因此它适合用在替换条件固定，
 * 但待替换字符串数量巨大的情形下。
 * 注意这里的正则表达式不支持“^”和“$”。
 * 
 * @author Zhengmao HU (James)
 *
 */
public class RegExpSubstitution implements Serializable{
	private static final long serialVersionUID = 7358024769261165557L;

	protected RunAutomaton runAutomation;
	protected String replacement;
	static protected final boolean moreSpaceForSpeed = true;

	/**
	 * Constructor with one regular expression.<br>
	 * 构造方法，用于只有一个正则表达式的情况。
	 * 
	 * @param replaceStr	The string to be replaced with<br>
	 * 						替换为这个字符串
	 * @param regExp		Part of the string that matches with this regular expression will be replaced.<br>
	 * 						匹配到这个正则表达式的部分会被替换
	 */
	public RegExpSubstitution(String replaceStr, String regExp){
		replacement = replaceStr;
		runAutomation = new RunAutomaton(new RegExp(regExp).toAutomaton(), moreSpaceForSpeed);
	}
	
	/**
	 * Constructor with several regular expressions.<br>
	 * 构造方法，用于有多个正则表达式的情况。
	 * 
	 * @param replaceStr	The string to be replaced with<br>
	 * 						替换为这个字符串
	 * @param regExps		Part of the string that matches with any of these regular expressions will be replaced.<br>
	 * 						匹配到这些正则表达式中的任意一个的部分都会被替换
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
	 * Constructor with several regular expressions.<br>
	 * 构造方法，用于有多个正则表达式的情况。
	 * 
	 * @param replaceStr	The string to be replaced with<br>
	 * 						替换为这个字符串
	 * @param regExps		Part of the string that matches with any of these regular expressions will be replaced.<br>
	 * 						匹配到这些正则表达式中的任意一个的部分都会被替换
	 */
	public RegExpSubstitution(String replaceStr, Collection<String> regExps){
		replacement = replaceStr;
		
		List<Automaton> list = new ArrayList<Automaton>(regExps.size());
		for (String re: regExps){
			list.add(new RegExp(re).toAutomaton());
		}
		runAutomation = new RunAutomaton(BasicOperations.union(list), moreSpaceForSpeed);
	}
	
	/**
	 * Do replacement.<br>
	 * 进行替换。
	 * 
	 * @param text	The original String.<br>替换前的原始字符串。
	 * @param firstOnly		If true, then only do replacement upon the first occurrence; 
	 * 						otherwise, replace in all the occurrences.<br>
	 * 						如果为true则只替换第一个出现的地方，否则全部替换。
	 * @return		The String after substitution; If no match found, the same String as the input one will be returned.<br>
	 * 				替换后的结果；如果没有发生替换，则结果与原来输入的字符串完全相同。
	 */
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
	 * Replace only the first occurrence.<br>
	 * 只替换第一个出现的地方
	 * @param text	The original String.<br>替换前的原始字符串。
	 * @return		The String after substitution; If no match found, the same String as the input one will be returned.<br>
	 * 				替换后的结果；如果没有发生替换，则结果与原来输入的字符串完全相同。
	 */
	public String replaceFirst(CharSequence text){
		return replace(text, true);
	}
	
	/**
	 * Replace all the occurrences.<br>
	 * 替换所有出现的地方
	 * @param text	The original String.<br>替换前的原始字符串。
	 * @return		The String after substitution; If no match found, the same String as the input one will be returned.<br>
	 * 				替换后的结果；如果没有发生替换，则结果与原来输入的字符串完全相同。
	 */
	public String replaceAll(CharSequence text){
		return replace(text, false);
	}
	
	/**
	 * Replace only the last occurrence.<br>
	 * 替换掉最后一个出现的地方
	 * @param text	The original String.<br>替换前的原始字符串。
	 * @return		The String after substitution; If no match found, the same String as the input one will be returned.<br>
	 * 				替换后的结果；如果没有发生替换，则结果与原来输入的字符串完全相同。
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
