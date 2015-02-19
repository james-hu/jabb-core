package net.sf.jabb.util.state;

import static org.junit.Assert.*;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import org.junit.Test;

public class StartStopStateMachineTest {

	@Test
	public void testTransitions() {
		StartStopStateMachine s = new StartStopStateMachine();
		doTestTransitionsFromStopped(s);
		doTestTransitionsFromRunning(s);
	}
	
	@Test
	public void testSerialization() throws IOException, ClassNotFoundException{
		StartStopStateMachine s = new StartStopStateMachine();
		doTestTransitionsFromStopped(s);
		
		ByteArrayOutputStream byteArrayOut = new ByteArrayOutputStream();
		ObjectOutputStream out = new ObjectOutputStream(byteArrayOut);
		out.writeObject(s);
		out.close();
		byteArrayOut.close();
		
		byte[] bytes = byteArrayOut.toByteArray();
		
		ByteArrayInputStream byteArrayIn = new ByteArrayInputStream(bytes);
		ObjectInputStream in = new ObjectInputStream(byteArrayIn);
		StartStopStateMachine s2 = (StartStopStateMachine)(in.readObject());
		
		doTestTransitionsFromRunning(s2);
	}
	
	protected void doTestTransitionsFromStopped(StartStopStateMachine s){
		assertTrue(s.isStopped());
		
		assertTrue(s.start());
		assertTrue(s.isStarting());
		
		assertTrue(s.finishStarting());
		assertTrue(s.isRunning());
	}
	
	protected void doTestTransitionsFromRunning(StartStopStateMachine s){
		assertTrue(s.isRunning());

		assertTrue(s.stop());
		assertTrue(s.isStopping());
		
		assertTrue(s.finishStopping());
		assertTrue(s.isStopped());
		
		assertFalse(s.finishStarting());
		assertFalse(s.stop());
		assertFalse(s.finishStopping());
	}

}
