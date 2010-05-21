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

import javolution.util.FastMap;

import org.apache.commons.lang.mutable.MutableInt;

/**
 * 经过了分析的文本。
 * <p>
 * Text that had been analyzed.
 * 
 * @author Zhengmao HU (James)
 *
 */
public class AnalyzedText {
	protected String text;
	protected boolean analyzed;
	protected List<String> words;
	protected Set<String> uniqueWords;
	protected Object lengthCategory;
	protected Map<Object, MutableInt> matchedKeywords;
	
	/**
	 * @return 供分析的原始文本
	 * 		<br>Original text for analysis.
	 */
	public String getText() {
		return text;
	}
	/**
	 * @param text 供分析的原始文本
	 * 			<br>Original text for analysis.
	 */
	public void setText(String text) {
		this.text = text;
	}
	/**
	 * @return 是否已经经过分析了
	 * 		<br>Had the original text been analyzed?
	 */
	public boolean isAnalyzed() {
		return analyzed;
	}
	/**
	 * @param analyzed 是否已经经过分析了
	 * 		<br>Had the original text been analyzed?
	 */
	void setAnalyzed(boolean analyzed) {
		this.analyzed = analyzed;
	}
	/**
	 * @return 组成原文的全部词、字，按出现的次序排列。
	 * 		<br>All words that consist the original text, in the order of appearance.
	 */
	public List<String> getWords() {
		return words;
	}
	/**
	 * @param words 切分后的全部词、字，按出现的次序排列。
	 * 		<br>All words that consist the original text, in the order of appearance.
	 */
	void setWords(List<String> words) {
		this.words = words;
	}
	/**
	 * @return 组成原文的不重复的词、字。
	 * 		<br>Unique words that consist the original text.
	 */
	public Set<String> getUniqueWords() {
		return uniqueWords;
	}
	/**
	 * @param uniqueWords 组成原文的不重复的词、字。
	 * 		<br>Unique words that consist the original text.
	 */
	void setUniqueWords(Set<String> uniqueWords) {
		this.uniqueWords = uniqueWords;
	}
	/**
	 * @return 文本长度类别
	 * 		<br>Category according to the length of the original text.
	 */
	public Object getLengthCategory() {
		return lengthCategory;
	}
	/**
	 * @param lengthCategory 文本长度类别
	 * 		<br>Category according to the length of the original text.
	 */
	void setLengthCategory(Object lengthCategory) {
		this.lengthCategory = lengthCategory;
	}
	/**
	 * @return 匹配上的关键词所对应的attachment（在Map的Key中），以及它们出现的次数（在Map的Value中）
	 * 			<br>For each keywords that find in the text, return its attachment (as the Key
	 * 			in the Map) and occurrences count (as the Value in the Map).
	 */
	public Map<Object, MutableInt> getMatchedKeywords() {
		synchronized(this){
			if (matchedKeywords == null){
				matchedKeywords = new FastMap<Object, MutableInt>();
			}
		}
		return matchedKeywords;
	}
	/**
	 * @param matchedKeywords 匹配上的关键词所对应的attachment（在Map的Key中），以及它们出现的次数（在Map的Value中）
	 * 			<br>For each keywords that find in the text, return its attachment (as the Key
	 * 			in the Map) and occurrences count (as the Value in the Map).
	 */
	void setMatchedKeywords(Map<Object, MutableInt> matchedKeywords) {
		this.matchedKeywords = matchedKeywords;
	}

}
