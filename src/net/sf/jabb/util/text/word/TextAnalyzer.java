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
import java.util.TreeMap;

/**
 * 文本分析器。
 * <p>
 * Text Analyzer.
 * 
 * @author Zhengmao HU (James)
 *
 */
public abstract class TextAnalyzer {
	static public final int TYPE_MMSEG_SIMPLE = 1;
	static public final int TYPE_MMSEG_MAXWORD = 2;
	static public final int TYPE_MMSEG_COMPLEX = 3;
	static public final int TYPE_FAST = 4;
	
	protected String dictionaryPath;
	protected Map<String, ? extends Object> keywordDefinitions;
	protected TreeMap<Integer, ? extends Object> lengthDefinitions;
	
	static public TextAnalyzer createInstance(int type, 
			String dictionaryPath, 
			Map<String, ? extends Object> keywordDefinitions, 
			Map<Integer, ? extends Object> lengthDefinitions){
		switch(type){
		case TYPE_MMSEG_COMPLEX:
		case TYPE_MMSEG_MAXWORD:
		case TYPE_MMSEG_SIMPLE:
			return new MmsegTextAnalyzer(type, dictionaryPath, keywordDefinitions, lengthDefinitions);
		case TYPE_FAST:
			return new FastTextAnalyzer(dictionaryPath, keywordDefinitions, lengthDefinitions);
		default:
			throw new IllegalArgumentException("Not supported TextAnalyzer type: " + type);
		}
	}
	
	protected TextAnalyzer(String dictionaryPath, 
			Map<String, ? extends Object> keywordDefinitions, 
			Map<Integer, ? extends Object> lengthDefinitions){
		this.dictionaryPath = dictionaryPath;
		this.keywordDefinitions = keywordDefinitions;
		this.lengthDefinitions = lengthDefinitions == null ? 
				null : new TreeMap<Integer, Object>(lengthDefinitions);
	}
	
	/**
	 * 对文本进行分析
	 * @param text	待分析的文本
	 * @param lazy	是否延迟分析（用到分析结果的时候才进行实质性分析）
	 * @return
	 */
	public AnalyzedText analyze(String text, boolean lazy){
		AnalyzedText result = new AnalyzedText(this, text);
		if (!lazy){
			analyzeLength(result);
			analyzeWords(result);
			analyzeKeywords(result);
		}
		return result;
	}
	
	/**
	 * 分析长度落在哪个区间
	 * @param aText
	 */
	void analyzeLength(AnalyzedText aText){
		if (lengthDefinitions != null){
			for (int l: lengthDefinitions.keySet()){
				if (aText.getText().length() <= l){
					aText.setLengthCategory(lengthDefinitions.get(l));
					break;
				}
			}
		}
	}
	
	/**
	 * 分词
	 * @param aText
	 */
	abstract void analyzeWords(AnalyzedText aText);
	
	abstract void analyzeKeywords(AnalyzedText aText);
	
	void reloadDefinitions(
			Map<String, ? extends Object> keywordDefinitions, 
			Map<Integer, ? extends Object> lengthDefinitions){
			this.lengthDefinitions = lengthDefinitions == null ?
					null : new TreeMap<Integer, Object>(lengthDefinitions);
	}
		
}
