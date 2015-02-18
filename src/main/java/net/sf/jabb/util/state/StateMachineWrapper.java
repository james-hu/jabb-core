/**
 * 
 */
package net.sf.jabb.util.state;


/**
 * Wrapper of a state machine that can be applied to specific use case.
 * @author James Hu
 *
 */
public abstract class StateMachineWrapper<S, T> {
	protected  StateMachine<S, T> stateMachine;
	
	abstract protected void setup(StateMachine<S, T> stateMachine);
	
	public StateMachineWrapper(){
		stateMachine = new StateMachine<S, T>();
		setup(stateMachine);
	}
	
	/**
	 * Get current state.
	 * @return	current state
	 */
	public S getState(){
		return stateMachine.getState();
	}
	
	/**
	 * Set current state of the state machine. This method is multi-thread safe. 
	 * @param state  the current state to be, must have already been defined.
	 */
	public void setState(S state){
		stateMachine.setState(state);
	}
	
	/**
	 * Transit from current state to another. The method is multi-thread safe.
	 * @param transition  the transition, must have already been defined.
	 * @return  true if successful. False return indicates that the specified transition does not apply to current state.
	 */
	public boolean transit(T transition){
		return stateMachine.transit(transition);
	}
	
	@Override
	public String toString(){
		return stateMachine.getState().toString();
	}
}
