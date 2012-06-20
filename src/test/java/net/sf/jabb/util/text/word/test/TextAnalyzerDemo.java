package net.sf.jabb.util.text.word.test;

import net.sf.jabb.util.text.word.AnalyzedText;
import net.sf.jabb.util.text.word.TextAnalyzer;

public class TextAnalyzerDemo {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		String text = "Yellow fox jump过大片河流，so caused chaos. 这可怎么办？";
		TextAnalyzer analyzer = TextAnalyzer.createInstance(TextAnalyzer.TYPE_MMSEG_SIMPLE);
		AnalyzedText at = analyzer.analyze(text);
		System.out.println(at);
	}

}
