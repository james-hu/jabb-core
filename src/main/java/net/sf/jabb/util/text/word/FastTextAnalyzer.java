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

import java.util.Map;

import net.sf.jabb.util.text.KeywordMatcher;


/**
 * 基于KeywordMatcher分词的文本分析器。
 * <p>
 * Text Analyzer.
 * 
 * @author Zhengmao HU (James)
 *
 */
public class FastTextAnalyzer extends TextAnalyzer {
	protected ChineseWordIdentifier cwIdentifier;
	protected WordIdentifier wIdentifier;
	protected KeywordMatcher kwMatcher;
	
	public FastTextAnalyzer(String dictionaryPath, 
			Map<String, ? extends Object> keywordDefinitions, 
			Map<Integer, ? extends Object> lengthDefinitions){
		super(dictionaryPath, keywordDefinitions, lengthDefinitions);
		
		cwIdentifier = new ChineseWordIdentifier();
		wIdentifier = new WordIdentifier(cwIdentifier);

		kwMatcher = keywordDefinitions == null ? 
				null : new KeywordMatcher(keywordDefinitions);
	}

	/* (non-Javadoc)
	 * @see net.sf.jabb.util.text.word.TextAnalyzer#analyzeKeywords(net.sf.jabb.util.text.word.AnalyzedText)
	 */
	@Override
	void analyzeKeywords(AnalyzedText aText) {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see net.sf.jabb.util.text.word.TextAnalyzer#analyzeWords(net.sf.jabb.util.text.word.AnalyzedText)
	 */
	@Override
	void analyzeWords(AnalyzedText aText) {
		// TODO Auto-generated method stub

	}

}
