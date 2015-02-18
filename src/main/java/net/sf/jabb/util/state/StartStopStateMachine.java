/**
 * 
 */
package net.sf.jabb.util.state;

/**
 * A simple state machine with two transitions - start and stop.
 * @author James Hu
 *
 */
public class StartStopStateMachine extends StateMachineWrapper<Integer, Integer>{
	static public final Integer STOPPED = 0;
	static public final Integer STARTING = 1;
	static public final Integer RUNNING = 2;
	static public final Integer STOPPING = 3;
	
	static public final Integer START = 4;
	static public final Integer STOP = 5;
	static public final Integer FINISH_STARTING = 6;
	static public final Integer FINISH_STOPPING = 7;
	
	@Override
	protected void setup(StateMachine<Integer, Integer> stateMachine) {
		stateMachine.addState(STOPPED);
		stateMachine.addState(STARTING);
		stateMachine.addState(RUNNING);
		stateMachine.addState(STOPPING);
		
		stateMachine.addTransition(START, STOPPED, STARTING);
		stateMachine.addTransition(FINISH_STARTING, STARTING, RUNNING);
		stateMachine.addTransition(STOP, RUNNING, STOPPING);
		stateMachine.addTransition(FINISH_STOPPING, STOPPING, STOPPED);
		
		stateMachine.setState(STOPPED);
	}
	
	public boolean start(){
		return transit(START);
	}
	
	public boolean stop(){
		return transit(STOP);
	}
	
	public boolean finishStarting(){
		return transit(FINISH_STARTING);
	}
	
	public boolean finishStopping(){
		return transit(FINISH_STOPPING);
	}
	
	
	public boolean isStopped(){
		return getState().equals(STOPPED);
	}
	
	public boolean isStarting(){
		return getState().equals(STARTING);
	}
	
	public boolean isRunning(){
		return getState().equals(RUNNING);
	}
	
	public boolean isStopping(){
		return getState().equals(STOPPING);
	}
	
}
