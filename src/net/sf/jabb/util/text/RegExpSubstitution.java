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
 * ��������ʽ��ƥ�䵽���������滻��ע�������������ʽ��֧�֡�^���͡�$����
 * @author Zhengmao HU (James)
 *
 */
public class RegExpSubstitution implements Serializable{
	private static final long serialVersionUID = 7358024769261165557L;

	protected RunAutomaton runAutomation;
	protected String replacement;
	static protected final boolean moreSpaceForSpeed = true;

	/**
	 * ���췽��
	 * @param replaceStr	�滻Ϊ����ַ���
	 * @param regExp		ƥ�䵽���������ʽ�Ĳ��ֻᱻ�滻
	 */
	public RegExpSubstitution(String replaceStr, String regExp){
		replacement = replaceStr;
		runAutomation = new RunAutomaton(new RegExp(regExp).toAutomaton(), moreSpaceForSpeed);
	}
	
	/**
	 * ���췽��
	 * @param replaceStr	�滻Ϊ����ַ���
	 * @param regExps		ƥ�䵽��Щ������ʽ��������һ���Ĳ��ֶ��ᱻ�滻
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
	 * ���췽��
	 * @param replaceStr	�滻Ϊ����ַ���
	 * @param regExps		ƥ�䵽��Щ������ʽ��������һ���Ĳ��ֶ��ᱻ�滻
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
	 * ֻ�滻��һ�����ֵĵط�
	 * @param text
	 * @return		�滻��Ľ�������û�з����滻��������ԭ��������ַ�����ȫ��ͬ��
	 */
	public String replaceFirst(CharSequence text){
		return replace(text, true);
	}
	
	/**
	 * �滻���г��ֵĵط�
	 * @param text
	 * @return		�滻��Ľ�������û�з����滻��������ԭ��������ַ�����ȫ��ͬ��
	 */
	public String replaceAll(CharSequence text){
		return replace(text, false);
	}
	
	/**
	 * �滻�����һ�����ֵĵط�
	 * @param text
	 * @return		�滻��Ľ�������û�з����滻��������ԭ��������ַ�����ȫ��ͬ��
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
