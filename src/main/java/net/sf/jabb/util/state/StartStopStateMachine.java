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
	private static final long serialVersionUID = 4646150130797002922L;

	static public final Integer STOPPED = 0;
	static public final Integer STARTING = 1;
	static public final Integer RUNNING = 2;
	static public final Integer STOPPING = 3;
	
	static public final Integer START = 4;
	static public final Integer STOP = 5;
	static public final Integer FINISH_STARTING = 6;
	static public final Integer FINISH_STOPPING = 7;
	static public final Integer FAIL_STARTING = 8;
	static public final Integer FAIL_STOPPING = 9;
	
	@Override
	protected void define(StateMachineDefinition<Integer, Integer> definition) {
		definition
			.addState(STOPPED)
			.addState(STARTING)
			.addState(RUNNING)
			.addState(STOPPING)
		
			.addTransition(START, STOPPED, STARTING)
			.addTransition(FINISH_STARTING, STARTING, RUNNING)
			.addTransition(STOP, RUNNING, STOPPING)
			.addTransition(FINISH_STOPPING, STOPPING, STOPPED)
		
			.addTransition(FAIL_STARTING, STARTING, STOPPED)
			.addTransition(FAIL_STOPPING, STOPPING, RUNNING);
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
	
	public boolean failStarting(){
		return transit(FAIL_STARTING);
	}
	
	public boolean failStopping(){
		return transit(FAIL_STOPPING);
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
