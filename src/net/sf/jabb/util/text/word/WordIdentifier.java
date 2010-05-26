package net.sf.jabb.util.text.word;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.List;

import javolution.util.FastList;

import com.chenlb.mmseg4j.MMSeg;
import com.chenlb.mmseg4j.Seg;
import com.chenlb.mmseg4j.Word;

public class WordIdentifier extends MMSeg {
	protected ChineseWordIdentifier chineseWordIdentifier;

	public WordIdentifier(ChineseWordIdentifier cwIdentifier) {
		super(null, cwIdentifier);
		chineseWordIdentifier = cwIdentifier;
	}
	
	@Override
	public void reset(Reader input){
		if (input != null){
			super.reset(input);
		}
	}
	
	public List<String> getWords(String document){
		List<String> words = new FastList<String>();
		
		Reader sr = new StringReader(document.toString());
		this.reset(sr);
		
		Word word = null;
		List<String> identifiedWords = chineseWordIdentifier.getIdentifiedWords();
		try {
			while(true) {
				word = this.next();
				if (word != null){	// MMSeg分出来的非中文词条
					words.add(word.getString());
				} else if (identifiedWords.size() > 0){	//自己分出来的中文词条
					words.addAll(identifiedWords);
					identifiedWords.clear();
				} else {	//没词了
					break;
				}
				
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		
		return words;
	}


}
