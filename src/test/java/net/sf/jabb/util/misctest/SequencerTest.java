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
		range(100, 105, 100);
		range(100,105, 102);
		range(0, 100, 0);
		range(0, 100, 1);
		
		range(0, Long.MAX_VALUE - 1, 0);
		range(0, Long.MAX_VALUE - 1, Long.MAX_VALUE - 2);
		range(0, Long.MAX_VALUE - 10, Long.MAX_VALUE - 12);
		
		range(-500, -490, -499);
	}
	
	protected void range(long min, long max, long init){
		System.out.println("======= " + min + ", " + max + ", " + init);
		RangedSequencer s = new RangedSequencer(min, max, init);
		for (int i=0; i <20; i ++){
			System.out.println(s.next());
		}
		
	}
	
}
