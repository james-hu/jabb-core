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
 * һ�������࣬�����Զ�һ������������ʽ��ƥ�䵽���ַ����е��������滻��
 * <p>
 * An instance of this class can be used many times for substitution for different Strings.
 * The performance overhead of each time of substitution is very small. Therefore it is very suitble
 * to be used in occasions that the substitution criteria is fixed while the Strings to be substituted
 * are of huge volume.
 * Please be aware that regular expressions here do not support <code>"^"</code> and <code>"$"</code>
 * <p>
 * ������һ��ʵ����������ζԲ�ͬ���ַ��������滻��ÿ���滻ʱ�����ܿ�����С��������ʺ������滻�����̶���
 * �����滻�ַ��������޴�������¡�
 * ע�������������ʽ��֧�֡�^���͡�$����
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
	 * ���췽��������ֻ��һ��������ʽ�������
	 * 
	 * @param replaceStr	The string to be replaced with<br>
	 * 						�滻Ϊ����ַ���
	 * @param regExp		Part of the string that matches with this regular expression will be replaced.<br>
	 * 						ƥ�䵽���������ʽ�Ĳ��ֻᱻ�滻
	 */
	public RegExpSubstitution(String replaceStr, String regExp){
		replacement = replaceStr;
		runAutomation = new RunAutomaton(new RegExp(regExp).toAutomaton(), moreSpaceForSpeed);
	}
	
	/**
	 * Constructor with several regular expressions.<br>
	 * ���췽���������ж��������ʽ�������
	 * 
	 * @param replaceStr	The string to be replaced with<br>
	 * 						�滻Ϊ����ַ���
	 * @param regExps		Part of the string that matches with any of these regular expressions will be replaced.<br>
	 * 						ƥ�䵽��Щ������ʽ�е�����һ���Ĳ��ֶ��ᱻ�滻
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
	 * ���췽���������ж��������ʽ�������
	 * 
	 * @param replaceStr	The string to be replaced with<br>
	 * 						�滻Ϊ����ַ���
	 * @param regExps		Part of the string that matches with any of these regular expressions will be replaced.<br>
	 * 						ƥ�䵽��Щ������ʽ�е�����һ���Ĳ��ֶ��ᱻ�滻
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
	 * �����滻��
	 * 
	 * @param text	The original String.<br>�滻ǰ��ԭʼ�ַ�����
	 * @param firstOnly		If true, then only do replacement upon the first occurrence; 
	 * 						otherwise, replace in all the occurrences.<br>
	 * 						���Ϊtrue��ֻ�滻��һ�����ֵĵط�������ȫ���滻��
	 * @return		The String after substitution; If no match found, the same String as the input one will be returned.<br>
	 * 				�滻��Ľ�������û�з����滻��������ԭ��������ַ�����ȫ��ͬ��
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
	 * ֻ�滻��һ�����ֵĵط�
	 * @param text	The original String.<br>�滻ǰ��ԭʼ�ַ�����
	 * @return		The String after substitution; If no match found, the same String as the input one will be returned.<br>
	 * 				�滻��Ľ�������û�з����滻��������ԭ��������ַ�����ȫ��ͬ��
	 */
	public String replaceFirst(CharSequence text){
		return replace(text, true);
	}
	
	/**
	 * Replace all the occurrences.<br>
	 * �滻���г��ֵĵط�
	 * @param text	The original String.<br>�滻ǰ��ԭʼ�ַ�����
	 * @return		The String after substitution; If no match found, the same String as the input one will be returned.<br>
	 * 				�滻��Ľ�������û�з����滻��������ԭ��������ַ�����ȫ��ͬ��
	 */
	public String replaceAll(CharSequence text){
		return replace(text, false);
	}
	
	/**
	 * Replace only the last occurrence.<br>
	 * �滻�����һ�����ֵĵط�
	 * @param text	The original String.<br>�滻ǰ��ԭʼ�ַ�����
	 * @return		The String after substitution; If no match found, the same String as the input one will be returned.<br>
	 * 				�滻��Ľ�������û�з����滻��������ԭ��������ַ�����ȫ��ͬ��
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
