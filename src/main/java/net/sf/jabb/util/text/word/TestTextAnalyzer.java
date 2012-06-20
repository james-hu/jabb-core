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

import org.apache.commons.lang.mutable.MutableInt;


import net.sf.jabb.util.text.StringStartWithMatcher;

/**
 * ÎÄ±¾·ÖÎöÆ÷¡£
 * <p>
 * Text Analyzer.
 * 
 * @author Zhengmao HU (James)
 *
 */
public class TestTextAnalyzer {
	
	protected StringStartWithMatcher matcher;
	
	public TestTextAnalyzer(Dictionary dictionary, boolean moreSpaceForSpeed){
		matcher = new StringStartWithMatcher(dictionary.getWords(), moreSpaceForSpeed);
	}
	
	protected void scan(AnalyzedText aText){
		String text = aText.text;
		if (text != null && text.length() > 0){
			int i = 0;
			while (i < text.length()){
				Word word = (Word)matcher.match(text, i);
				if (word == null){
					i ++;
				}else{
					int types = word.getTypes();
					if ((types & Word.TYPE_IGNORE) != 0){
						
					}else if ((types & Word.TYPE_KEYWORD) != 0){
						Map<Object, MutableInt> matched = aText.getMatchedKeywords();
						Object attachment = word.getKeywordAttachment();
						if (matched.containsKey(attachment)){
							matched.get(attachment).increment();
						}else{
							matched.put(attachment, new MutableInt(1));
						}

					}else if ((types & Word.TYPE_NORMAL) != 0){
						aText.getWords().add(word.getWord());
						aText.getUniqueWords().add(word.getWord());
						
					}else if ((types & Word.TYPE_SEPARATOR) != 0){
						// skip over
					}
					i += word.getWord().length();
				}
			}
		}

	}

}
