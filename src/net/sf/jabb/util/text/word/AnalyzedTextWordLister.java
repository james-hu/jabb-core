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

import java.util.Collection;
import java.util.Set;

import com.enigmastation.extractors.WordLister;

/**
 * 支持中英文的分词器。基于词典匹配。
 * <p>
 * A WordLister that can handle Chinese and English. It is based
 * on dictionary matching.
 * 
 * @author Zhengmao HU (James)
 *
 */
public class AnalyzedTextWordLister implements WordLister {
	
	protected AnalyzedText ensureAnalyzedText(Object document){
		if (! (document instanceof AnalyzedText)){
			throw new IllegalArgumentException("Only instances of AnalyzedText can be handled.");
		}
		return (AnalyzedText)document;
	}

	/* (non-Javadoc)
	 * @see com.enigmastation.extractors.WordLister#addWords(java.lang.Object, java.util.Collection)
	 */
	@Override
	public void addWords(Object document, Collection<String> collection) {
		AnalyzedText aText = ensureAnalyzedText(document);
		collection.addAll(aText.getUniqueWords());
	}

	/* (non-Javadoc)
	 * @see com.enigmastation.extractors.WordLister#getUniqueWords(java.lang.Object)
	 */
	@Override
	public Set<String> getUniqueWords(Object document) {
		AnalyzedText aText = ensureAnalyzedText(document);
        return aText.getUniqueWords();
	}

}
