package net.sf.jabb.util.misctest;

import static org.junit.Assert.*;
import net.sf.jabb.util.thread.Sequencer;
import net.sf.jabb.util.thread.RangedSequencer;

import org.junit.Test;


public class SequencerTest {
	@Test
	public void nature(){
		Sequencer s = new Sequencer();
		for (long l = 0; l < 666666; l ++){
			assertEquals(l, s.next());
		}

		s = new Sequencer(500);
		for (long l=0; l <666666; l ++){
			assertEquals(l+500, s.next());
		}
		
		s = new Sequencer(Long.MAX_VALUE-2);
		for (int i=0; i <=2; i ++){
			assertEquals(Long.MAX_VALUE - 2 + i, s.next());
		}
		for (long l = 0; l < 666666; l ++){
			assertEquals(l, s.next());
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
