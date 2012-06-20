package net.sf.jabb.util.misctest;

import net.sf.jabb.util.thread.Sequencer;
import net.sf.jabb.util.thread.RangedSequencer;

import org.junit.Test;


public class SequencerTest {
	//@Ignore
	@Test
	public void nature(){
		Sequencer s = new Sequencer();
		for (int i=0; i <10; i ++){
			System.out.println(s.next());
		}

		s = new Sequencer(500);
		for (int i=0; i <10; i ++){
			System.out.println(s.next());
		}
		
		s = new Sequencer(Long.MAX_VALUE-2);
		for (int i=0; i <30; i ++){
			System.out.println(s.next());
		}
	}

	@Test
	public void range(){
		long min=100;
		long max=105;
		long init=100;
		System.out.println("======= " + min + ", " + max + ", " + init);
		RangedSequencer s = new RangedSequencer(min, max, init);
		for (int i=0; i <20; i ++){
			System.out.println(s.next());
		}

		System.out.println("======= ");
		s = new RangedSequencer();
		for (int i=0; i <20; i ++){
			System.out.println(s.next());
		}
		System.out.println("======= ");
		s = new RangedSequencer(Long.MAX_VALUE-2);
		for (int i=0; i <20; i ++){
			System.out.println(s.next());
		}
	}
	
}
