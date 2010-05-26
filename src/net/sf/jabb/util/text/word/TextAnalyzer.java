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
 * �ı���������
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
	 * ���ı����з���
	 * @param text	���������ı�
	 * @param lazy	�Ƿ��ӳٷ������õ����������ʱ��Ž���ʵ���Է�����
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
	 * �������������ĸ�����
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
	 * �ִ�
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
