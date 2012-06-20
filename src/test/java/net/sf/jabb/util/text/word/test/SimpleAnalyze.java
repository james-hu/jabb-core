package net.sf.jabb.util.text.word.test;


import net.sf.jabb.util.text.word.ChineseWordIdentifier;
import net.sf.jabb.util.text.word.WordIdentifier;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class SimpleAnalyze {
	static protected WordIdentifier wi;
	static protected ChineseWordIdentifier cwi;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		cwi = new ChineseWordIdentifier();
		wi = new WordIdentifier(cwi);
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}
	
	@Test
	public void example(){
		String s = "Yellow fox jump过大片河流，so caused chaos. 这可怎么办？";
		System.out.println(s);
		System.out.println(wi.getWords(s));
	}

}
