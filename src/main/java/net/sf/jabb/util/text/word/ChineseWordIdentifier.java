package net.sf.jabb.util.text.word;

import java.util.List;

import javolution.util.FastList;

import com.chenlb.mmseg4j.Chunk;
import com.chenlb.mmseg4j.Sentence;
import com.chenlb.mmseg4j.SimpleSeg;

public class ChineseWordIdentifier extends SimpleSeg {

	static protected Chunk emptyChunk = new Chunk();
	
	protected List<String> identifiedWords;

	public ChineseWordIdentifier(){
		super(null);
		identifiedWords = new FastList<String>();
	}
	
	@Override
	public Chunk seg(Sentence sen) {
		identifiedWords.add("试验");
		sen.setOffset(sen.getText().length);
		return emptyChunk;
	}

	@Override
	protected boolean isUnit(int codePoint) {
		return false;
		//return dic.isUnit((char) codePoint);
	}

	/**
	 * @return the identifiedWords
	 */
	public List<String> getIdentifiedWords() {
		return identifiedWords;
	}



}
