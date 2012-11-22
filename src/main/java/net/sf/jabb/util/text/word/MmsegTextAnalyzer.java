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

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.Map;

import net.sf.jabb.util.text.KeywordMatcher;

import javolution.util.FastList;
import javolution.util.FastSet;

import com.chenlb.mmseg4j.ComplexSeg;
import com.chenlb.mmseg4j.MMSeg;
import com.chenlb.mmseg4j.MaxWordSeg;
import com.chenlb.mmseg4j.Seg;
import com.chenlb.mmseg4j.SimpleSeg;

/**
 * 基于mmseg4j分词的文本分析器。
 * <p>
 * Text Analyzer.
 * 
 * @author Zhengmao HU (James)
 *
 */
public class MmsegTextAnalyzer extends TextAnalyzer {
	protected com.chenlb.mmseg4j.Dictionary dict; 
	protected Seg seg;
	protected MMSeg mmSeg;
	protected KeywordMatcher kwMatcher;
	
	public MmsegTextAnalyzer(int type, String dictionaryPath, 
			Map<String, ? extends Object> keywordDefinitions, 
			Map<Integer, ? extends Object> lengthDefinitions){
		super(dictionaryPath, keywordDefinitions, lengthDefinitions);
		
		if (dictionaryPath == null){
			dict = com.chenlb.mmseg4j.Dictionary.getInstance();
		}else{
			dict = com.chenlb.mmseg4j.Dictionary.getInstance(dictionaryPath);
		}
		switch (type){
		case TYPE_MMSEG_SIMPLE:
			seg = new SimpleSeg(dict);
			break;
		case TYPE_MMSEG_COMPLEX:
			seg = new ComplexSeg(dict);
			break;
		case TYPE_MMSEG_MAXWORD:
			seg = new MaxWordSeg(dict);
			break;
		default:
			throw new IllegalArgumentException("Supported types are: TYPE_MMSEG_SIMPLE, TYPE_MMSEG_COMPLEX, TYPE_MMSEG_MAXWORD");
		}
		mmSeg = new MMSeg(new StringReader(""), seg);
		
		kwMatcher = keywordDefinitions == null ? 
				null : new KeywordMatcher(keywordDefinitions);
	}


	/* (non-Javadoc)
	 * @see net.sf.jabb.util.text.word.TextAnalyzer#analyzeKeywords(net.sf.jabb.util.text.word.AnalyzedText)
	 */
	@Override
	void analyzeKeywords(AnalyzedText aText) {
		if (kwMatcher != null){
			aText.setMatchedKeywords(kwMatcher.match(aText.getText()));
		}
	}

	/* (non-Javadoc)
	 * @see net.sf.jabb.util.text.word.TextAnalyzer#analyzeWords(net.sf.jabb.util.text.word.AnalyzedText)
	 */
	@Override
	void analyzeWords(AnalyzedText aText) {
		FastList<String> list = new FastList<String>();
		FastSet<String> set = new FastSet<String>();

		com.chenlb.mmseg4j.Word word = null;
		Reader sr = new StringReader(aText.getText());
		synchronized(mmSeg){
			mmSeg.reset(sr);
			try{
				while((word=mmSeg.next())!=null) {
					String w = word.getString();
					list.add(w);
					set.add(w);
				}
			}catch(IOException e){
				throw new RuntimeException("IOException occurred", e);
			}
		}
		aText.setWords(list);
		aText.setUniqueWords(set);
	}


	@Override
	void reloadDefinitions(
			Map<String, ? extends Object> keywordDefinitions,
			Map<Integer, ? extends Object> lengthDefinitions) {
		super.reloadDefinitions(keywordDefinitions, lengthDefinitions);
		
		if (dict.wordsFileIsChange()){
			dict.reload();
		}
		kwMatcher = keywordDefinitions == null ? 
			null : new KeywordMatcher(keywordDefinitions);
	}

}
