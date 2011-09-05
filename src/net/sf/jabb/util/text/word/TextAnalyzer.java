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
 * �ı��������������Ľ�������{@link AnalyzedText}�С�
 * 
 * @author Zhengmao HU (James)
 *
 */
public abstract class TextAnalyzer {
	/**
	 * ʹ��com.chenlb.mmseg4j.SimpleSeg���зִ�
	 */
	static public final int TYPE_MMSEG_SIMPLE = 1;
	/**
	 * ʹ��com.chenlb.mmseg4j.MaxWordSeg���зִ�
	 */
	static public final int TYPE_MMSEG_MAXWORD = 2;
	/**
	 * ʹ��com.chenlb.mmseg4j.ComplexSeg���зִ�
	 */
	static public final int TYPE_MMSEG_COMPLEX = 3;
	/**
	 * ʹ��KeywordMatcher���Զ�����ֵ����зִʣ������У��в����ƣ�
	 */
	static public final int TYPE_FAST = 4;
	
	protected String dictionaryPath;
	protected Map<String, ? extends Object> keywordDefinitions;
	protected TreeMap<Integer, ? extends Object> lengthDefinitions;
	
	/**
	 * Create an instance of TextAnalyzer.<br>
	 * ����һ���ı�������ʵ����
	 * 
	 * @param type				{@link #TYPE_MMSEG_SIMPLE} | {@link #TYPE_MMSEG_COMPLEX} | {@link #TYPE_MMSEG_MAXWORD} | {@link #TYPE_FAST}
	 * @param dictionaryPath	�ֵ��ļ�·�������Ϊnull�����ʾʹ��ȱʡλ�õ��ֵ��ļ�
	 * @param keywordDefinitions	�ؼ����ֵĶ���
	 * @param lengthDefinitions		�ı����������
	 * @return	A new instance of TextAnalyzer.<br>TextAnalyzer��һ��ʵ����
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
	 * ����һ���ı�������ʵ����
	 * 
	 * @param type				{@link #TYPE_MMSEG_SIMPLE} | {@link #TYPE_MMSEG_COMPLEX} | {@link #TYPE_MMSEG_MAXWORD} | {@link #TYPE_FAST}
	 * @param keywordDefinitions	�ؼ����ֵĶ���
	 * @param lengthDefinitions		�ı����������
	 * @return	A new instance of TextAnalyzer.<br>TextAnalyzer��һ��ʵ����
	 */
	static public TextAnalyzer createInstance(int type, 
			Map<String, ? extends Object> keywordDefinitions, 
			Map<Integer, ? extends Object> lengthDefinitions){
		return createInstance(type, null, keywordDefinitions, lengthDefinitions);
	}

	
	/**
	 * Constructor that will be used internally.<br>
	 * �����ڲ�ʹ�õĹ��췽����
	 * @param dictionaryPath		�ֵ��ļ�·��
	 * @param keywordDefinitions	�ؼ����ֵĶ���
	 * @param lengthDefinitions		�ı����������
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
	 * ���ı����з�����
	 * @param text	���������ı�
	 * @param lazy	�Ƿ��ӳٷ�������ν�ӳ���ֱָ���õ����������ʱ��Ž���ʵ���Է�����
	 * @return		�������
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
	 * ���ı��������̷���������lazy��ʽ��
	 * @param text	���������ı�
	 * @return		�������
	 */
	public AnalyzedText analyze(String text){
		return analyze(text, false);
	}
	
	/**
	 * �����ı����ȷ������������ı��ĳ��������ĸ�������䡣
	 * @param aText		������ȡԭʼ�ı��Լ���Ž��
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
	 * ���зִʡ������ı��ָ�Ϊһ�����ʻ��֡�
	 * @param aText		������ȡԭʼ�ı��Լ���Ž��
	 */
	abstract void analyzeWords(AnalyzedText aText);
	
	/**
	 * ���йؼ�����ƥ�䡣
	 * @param aText	������ȡԭʼ�ı��Լ���Ž��
	 */
	abstract void analyzeKeywords(AnalyzedText aText);
	
	/**
	 * ���¼����������ݡ�
	 * @param keywordDefinitions	�ؼ��ʶ���
	 * @param lengthDefinitions		�ı����������
	 */
	void reloadDefinitions(
			Map<String, ? extends Object> keywordDefinitions, 
			Map<Integer, ? extends Object> lengthDefinitions){
			this.lengthDefinitions = lengthDefinitions == null ?
					null : new TreeMap<Integer, Object>(lengthDefinitions);
	}
		
}
