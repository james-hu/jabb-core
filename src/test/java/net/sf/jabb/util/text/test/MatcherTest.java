package net.sf.jabb.util.text.test;

import net.sf.jabb.util.text.RegExpSubstitution;

import org.junit.Test;

import dk.brics.automaton.Automaton;
import dk.brics.automaton.AutomatonMatcher;
import dk.brics.automaton.RegExp;
import dk.brics.automaton.RunAutomaton;


public class MatcherTest {
	static String re = "(aaa.bbb.ccc)|(aaa.bbb.ccc/)";
	static String text = "http://aaa.bbb.ccc/test/aaa.bbb.ccc/ds/sdfk/abc/aaa.bbb.ccc/abc.jpg";
	
	@Test
	public void simpleMatch(){
		Automaton a = new RegExp("中国").toAutomaton();
		RunAutomaton ra = new RunAutomaton(a);
		System.out.println("simpleMatch ==> " + ra.run("中国"));
		System.out.println("newMatcher ==> " + ra.newMatcher("中华人民中国大家").find());
		
	}
	
	@Test
	public void simple(){
		
		StringBuilder sb = new StringBuilder();
		
		Automaton a = new RegExp(re).toAutomaton();
		RunAutomaton ra = new RunAutomaton(a);
		AutomatonMatcher am = ra.newMatcher(text);
		int lastend = 0;
		while (am.find()){
			System.out.println(am.start());
			System.out.println(am.end());
			System.out.println(text.substring(am.start(), am.end()));
			
			sb.append(text.substring(lastend, am.start()));
			lastend = am.end();
		}
		sb.append(text.substring(lastend, text.length()));
		System.out.println("==> " + sb.toString());
	}
	
	@Test
	public void optimized(){
		RegExpSubstitution rre = new RegExpSubstitution("REPLACED", re);
		System.out.println(rre.replaceAll(text));
		System.out.println(rre.replaceFirst(text));
		System.out.println(rre.replaceLast(text));
	}
	
	@Test
	public void union(){
		RegExpSubstitution rre = new RegExpSubstitution("REPLACED", "abc", "bbb");
		System.out.println(rre.replaceAll(text));
		System.out.println(rre.replaceFirst(text));
		System.out.println(rre.replaceLast(text));
	}
	
	@Test
	public void url(){
		RegExpSubstitution rre = new RegExpSubstitution("http://", "http://aaa.bbb.ccc/");
		System.out.println(rre.replaceFirst(text));
		
	}

}
