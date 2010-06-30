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

package net.sf.jabb.util.text.word;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.mutable.MutableInt;

/**
 * �����˷������ı���
 * <p>
 * Text that had been analyzed.
 * 
 * @author Zhengmao HU (James)
 *
 */
public class AnalyzedText {
	protected TextAnalyzer analyzer;
	protected String text;
	protected List<String> words;
	protected Set<String> uniqueWords;
	protected Object lengthCategory;
	protected Map<Object, MutableInt> matchedKeywords;
	
	public AnalyzedText(TextAnalyzer analyzer, String text){
		this.analyzer = analyzer;
		this.text = text;
	}
	
	/**
	 * @return ��������ԭʼ�ı�
	 * 		<br>Original text for analysis.
	 */
	public String getText() {
		return text;
	}
	/**
	 * @return ���ԭ�ĵ�ȫ���ʡ��֣������ֵĴ������С�
	 * 		<br>All words that consist the original text, in the order of appearance.
	 */
	public List<String> getWords() {
		synchronized(this){
			if (words == null){
				analyzer.analyzeWords(this);
			}
		}
		return words;
	}
	/**
	 * @param words �зֺ��ȫ���ʡ��֣������ֵĴ������С�
	 * 		<br>All words that consist the original text, in the order of appearance.
	 */
	void setWords(List<String> words) {
		this.words = words;
	}
	/**
	 * @return ���ԭ�ĵĲ��ظ��Ĵʡ��֡�
	 * 		<br>Unique words that consist the original text.
	 */
	public Set<String> getUniqueWords() {
		synchronized(this){
			if (uniqueWords == null){
				analyzer.analyzeWords(this);
			}
		}
		return uniqueWords;
	}
	/**
	 * @param uniqueWords ���ԭ�ĵĲ��ظ��Ĵʡ��֡�
	 * 		<br>Unique words that consist the original text.
	 */
	void setUniqueWords(Set<String> uniqueWords) {
		this.uniqueWords = uniqueWords;
	}
	/**
	 * @return �ı��������
	 * 		<br>Category according to the length of the original text.
	 */
	public Object getLengthCategory() {
		synchronized(this){
			if (lengthCategory == null){
				analyzer.analyzeLength(this);
			}
		}
		return lengthCategory;
	}
	/**
	 * @param lengthCategory �ı��������
	 * 		<br>Category according to the length of the original text.
	 */
	void setLengthCategory(Object lengthCategory) {
		this.lengthCategory = lengthCategory;
	}
	/**
	 * @return ƥ���ϵĹؼ�������Ӧ��attachment����Map��Key�У����Լ����ǳ��ֵĴ�������Map��Value�У�
	 * 			<br>For each keywords that find in the text, return its attachment (as the Key
	 * 			in the Map) and occurrences count (as the Value in the Map).
	 */
	public Map<Object, MutableInt> getMatchedKeywords() {
		synchronized(this){
			if (matchedKeywords == null){
				analyzer.analyzeKeywords(this);
			}
		}
		return matchedKeywords;
	}
	/**
	 * @param matchedKeywords ƥ���ϵĹؼ�������Ӧ��attachment����Map��Key�У����Լ����ǳ��ֵĴ�������Map��Value�У�
	 * 			<br>For each keywords that find in the text, return its attachment (as the Key
	 * 			in the Map) and occurrences count (as the Value in the Map).
	 */
	void setMatchedKeywords(Map<Object, MutableInt> matchedKeywords) {
		this.matchedKeywords = matchedKeywords;
	}

}