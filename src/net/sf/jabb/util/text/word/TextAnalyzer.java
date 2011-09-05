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

import java.util.Map;
import java.util.TreeMap;

/**
 * Text Analyzer; Result of the analysis will be hold in {@link AnalyzedText}.<br>
 * 文本分析器；分析的结果会放在{@link AnalyzedText}中。
 * 
 * @author Zhengmao HU (James)
 *
 */
public abstract class TextAnalyzer {
	/**
	 * 使用com.chenlb.mmseg4j.SimpleSeg进行分词
	 */
	static public final int TYPE_MMSEG_SIMPLE = 1;
	/**
	 * 使用com.chenlb.mmseg4j.MaxWordSeg进行分词
	 */
	static public final int TYPE_MMSEG_MAXWORD = 2;
	/**
	 * 使用com.chenlb.mmseg4j.ComplexSeg进行分词
	 */
	static public final int TYPE_MMSEG_COMPLEX = 3;
	/**
	 * 使用KeywordMatcher与自定义的字典表进行分词（试验中，尚不完善）
	 */
	static public final int TYPE_FAST = 4;
	
	protected String dictionaryPath;
	protected Map<String, ? extends Object> keywordDefinitions;
	protected TreeMap<Integer, ? extends Object> lengthDefinitions;
	
	/**
	 * Create an instance of TextAnalyzer.<br>
	 * 创建一个文本分析器实例。
	 * 
	 * @param type				{@link #TYPE_MMSEG_SIMPLE} | {@link #TYPE_MMSEG_COMPLEX} | {@link #TYPE_MMSEG_MAXWORD} | {@link #TYPE_FAST}
	 * @param dictionaryPath	字典文件路径，如果为null，则表示使用缺省位置的字典文件
	 * @param keywordDefinitions	关键词字的定义
	 * @param lengthDefinitions		文本长度类别定义
	 * @return	A new instance of TextAnalyzer.<br>TextAnalyzer的一个实例。
	 */
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
	
	/**
	 * Create an instance of TextAnalyzer.<br>
	 * 创建一个文本分析器实例。
	 * 
	 * @param type				{@link #TYPE_MMSEG_SIMPLE} | {@link #TYPE_MMSEG_COMPLEX} | {@link #TYPE_MMSEG_MAXWORD} | {@link #TYPE_FAST}
	 * @param keywordDefinitions	关键词字的定义
	 * @param lengthDefinitions		文本长度类别定义
	 * @return	A new instance of TextAnalyzer.<br>TextAnalyzer的一个实例。
	 */
	static public TextAnalyzer createInstance(int type, 
			Map<String, ? extends Object> keywordDefinitions, 
			Map<Integer, ? extends Object> lengthDefinitions){
		return createInstance(type, null, keywordDefinitions, lengthDefinitions);
	}

	
	/**
	 * Constructor that will be used internally.<br>
	 * 仅供内部使用的构造方法。
	 * @param dictionaryPath		字典文件路径
	 * @param keywordDefinitions	关键词字的定义
	 * @param lengthDefinitions		文本长度类别定义
	 */
	protected TextAnalyzer(String dictionaryPath, 
			Map<String, ? extends Object> keywordDefinitions, 
			Map<Integer, ? extends Object> lengthDefinitions){
		this.dictionaryPath = dictionaryPath;
		this.keywordDefinitions = keywordDefinitions;
		this.lengthDefinitions = lengthDefinitions == null ? 
				null : new TreeMap<Integer, Object>(lengthDefinitions);
	}
	
	/**
	 * 对文本进行分析。
	 * @param text	待分析的文本
	 * @param lazy	是否延迟分析（所谓延迟是指直到用到分析结果的时候才进行实质性分析）
	 * @return		分析结果
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
	 * 对文本进行立刻分析，不用lazy方式。
	 * @param text	待分析的文本
	 * @return		分析结果
	 */
	public AnalyzedText analyze(String text){
		return analyze(text, false);
	}
	
	/**
	 * 进行文本长度分析——分析文本的长度落在哪个类别区间。
	 * @param aText		用来获取原始文本以及存放结果
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
	 * 进行分词——将文本分割为一个个词或字。
	 * @param aText		用来获取原始文本以及存放结果
	 */
	abstract void analyzeWords(AnalyzedText aText);
	
	/**
	 * 进行关键词字匹配。
	 * @param aText	用来获取原始文本以及存放结果
	 */
	abstract void analyzeKeywords(AnalyzedText aText);
	
	/**
	 * 重新加载配置数据。
	 * @param keywordDefinitions	关键词定义
	 * @param lengthDefinitions		文本长度类别定义
	 */
	void reloadDefinitions(
			Map<String, ? extends Object> keywordDefinitions, 
			Map<Integer, ? extends Object> lengthDefinitions){
			this.lengthDefinitions = lengthDefinitions == null ?
					null : new TreeMap<Integer, Object>(lengthDefinitions);
	}
		
}
