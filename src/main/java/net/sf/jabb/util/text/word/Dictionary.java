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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Map;
import javolution.util.FastMap;

/**
 * �ʵ䡣
 * <p>
 * Dictionary of words.
 * 
 * @author Zhengmao HU (James)
 *
 */
public class Dictionary {
	protected FastMap<String, Word> words;
	
	public Dictionary(){
		words = new FastMap<String, Word>();
	}

	/**
	 * ����һ��ʵ�����������ǿ�������һ��ʵ����
	 * <p>
	 * Create an instance which is a copy of another instance.
	 * 
	 * @param original
	 */
	public Dictionary(Dictionary original){
		this();
		for (Word word: original.getWords().values()){
			addWord(word, true);
		}
	}
	
	/**
	 * ����һ���ֵ�����ݺϲ�������
	 * <p>
	 * Merge the words from another dictionary into this one.
	 * 
	 * @param dict	��Ҫ���ϲ���������һ���ֵ�
	 * 			<br>The other dictionary that will be merged into this one.
	 */
	public void merge(Dictionary dict){
		for (Word word: dict.getWords().values()){
			addWord(word, true);
		}
	}

	/**
	 * ����һ���´����������������ڴʵ����Ѿ����ڣ���ϲ���
	 * <p>
	 * Add a new word, if this word already exists in the dictionary then the new definition will 
	 * be merged into existing one.
	 * 
	 * @param newWord
	 * @param makeCopy	�Ƿ��ƴ������󣬶�������
	 * 				<br>Whether or not to copy the Word object, rather than to refer it.
	 */
	public void addWord(Word newWord, boolean makeCopy){
		synchronized(words){
			Word existingWord = words.get(newWord.getWord());
			if (existingWord != null){
				existingWord.setType(newWord.getTypes());
				existingWord.setKeywordAttachment(newWord.getKeywordAttachment());
			}else{
				if (makeCopy){
					newWord  = new Word(newWord);
				}
				words.put(newWord.getWord(), newWord);
			}
		}
	}
	
	/**
	 * ����һ���´����������������ڴʵ����Ѿ����ڣ���ϲ���
	 * �������󲻻ᱻ���ƣ����ǻᱻ���á�
	 * <p>
	 * Add a new word, if this word already exists in the dictionary then the new definition will 
	 * be merged into existing one.
	 * The Word object will not be copied, it will be referenced.
	 * 
	 * @param newWord
	 */
	public void addWord(Word newWord){
		addWord(newWord, false);
	}
	
	/**
	 * ����������һ��ָ�����͵Ĵ��������ĳ�������ڴʵ����Ѿ����ڣ���ϲ���
	 * <p>
	 * Load a batch of word of specified type from stream. If any word already exists in the dictionary,
	 * then the new definition will be merged into existing one.
	 * 
	 * @param is	������<br>The stream to read from.
	 * @param wordType	���ͣ�������Word����<br>Type of the word, which is defined in the class Word.
	 * @throws IOException
	 */
	public void loadWords(InputStream is, int wordType) throws IOException{
		BufferedReader br = new BufferedReader(new InputStreamReader(is));
		String w = null;
		while ((w=br.readLine()) != null){
			// Don't trim it, in case the word itself is blank.   w = w.trim();
			Word word = new Word();
			word.setWord(w);
			word.setType(wordType);
			if ((wordType & Word.TYPE_KEYWORD) != 0){
				word.setKeywordAttachment(w);
			}
			addWord(word);
		}
	}

	/**
	 * @return the words
	 */
	public Map<String, Word> getWords() {
		return words;
	}
	
}
