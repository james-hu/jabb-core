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

package net.sf.jabb.util.text.word;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.mutable.MutableInt;

/**
 * Information about the text after analysis, including: original text, list of segmented words,
 * list of segmented words after de-duplication, text length category, and result of keywords matching.<br>
 * 对文本进行分析之后的信息，包括：原文、拆分开的词或字的清单、去重复之后的拆分开的词或字的清单、
 * 文本长度类别、关键词字匹配结果。
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
	
	/**
	 * Constructor.
	 * @param analyzer	The analyzer
	 * @param text		The text to be analyzed
	 */
	public AnalyzedText(TextAnalyzer analyzer, String text){
		this.analyzer = analyzer;
		this.text = text;
	}
	
	/**
	 * @return 供分析的原始文本
	 * 		<br>Original text for analysis.
	 */
	public String getText() {
		return text;
	}
	/**
	 * @return 组成原文的全部词、字，按出现的次序排列。
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
		synchronized(this){
			if (uniqueWords == null){
				analyzer.analyzeWords(this);
			}
		}
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
		synchronized(this){
			if (lengthCategory == null){
				analyzer.analyzeLength(this);
			}
		}
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
				analyzer.analyzeKeywords(this);
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
	
	public String toString(){
		return ToStringBuilder.reflectionToString(this);
	}

}
