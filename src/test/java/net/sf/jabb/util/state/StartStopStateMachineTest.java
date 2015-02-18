package net.sf.jabb.util.state;

import static org.junit.Assert.*;

import org.junit.Test;

public class StartStopStateMachineTest {

	@Test
	public void test() {
		StartStopStateMachine s = new StartStopStateMachine();
		assertEquals(s.STOPPED, s.getState());
		
		assertTrue(s.start());
		assertEquals(s.STARTING, s.getState());
		
		assertTrue(s.finishStarting());
		assertEquals(s.RUNNING, s.getState());

		assertTrue(s.stop());
		assertEquals(s.STOPPING, s.getState());
		
		assertTrue(s.finishStopping());
		assertEquals(s.STOPPED, s.getState());
		
		assertFalse(s.finishStarting());
		assertFalse(s.stop());
		assertFalse(s.finishStopping());
	}

}
