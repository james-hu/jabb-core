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

import javolution.util.FastSet;

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
	protected boolean includeLengthCategory;
	
	public AnalyzedTextWordLister(){
		this(false);
	}
	
	public AnalyzedTextWordLister(boolean includeLengthCategory){
		this.includeLengthCategory = includeLengthCategory;
	}
	
	/* (non-Javadoc)
	 * @see com.enigmastation.extractors.WordLister#addWords(java.lang.Object, java.util.Collection)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public void addWords(Object document, Collection<String> collection) {
		if (document instanceof Collection<?>){
			collection.addAll((Collection<String>)document);
		}else if (document instanceof AnalyzedText){
			collection.addAll(((AnalyzedText)document).getWords());
		}else {
			throw new IllegalArgumentException("Only instances of AnalyzedText can be handled.");
		}
	}

	/* (non-Javadoc)
	 * @see com.enigmastation.extractors.WordLister#getUniqueWords(java.lang.Object)
	 */
	@Override
	public Set<String> getUniqueWords(Object document) {
        Set<String> features = new FastSet<String>();
		if (document instanceof AnalyzedText){
	        features.addAll(((AnalyzedText)document).getUniqueWords());
		    if (includeLengthCategory){
		       	features.add(((AnalyzedText)document).getLengthCategory().toString());
		    }
		}else{
	        addWords(document, features);
		}
	    return features;
	}

}
