/**
 * 
 */
package net.sf.jabb.util.state;

import java.io.Serializable;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

import net.sf.jabb.util.bean.DoubleValueBean;

/**
 * State machine with states and actions defined.
 * Definition methods addState(...) and addTransition(...) must be called before other methods and they are not thread safe.
 * Other methods are thread safe.
 * Internally Integer type is used for the state identifiers so the number of states allowed is quite huge.
 * However, there are overheads, so if you need a really big and fast state machine, you'd better create your own lookup array based one.
 * @author James Hu
 *
 * @param <S>	Type of the sate object/name, for example String or Integer or Long.
 * @param <T>	Type of the transition object/name, for example String or Integer or Long.
 */
public class StateMachine<S, T> implements Serializable{
	private static final long serialVersionUID = -2875478874027002800L;

	private StateMachineDefinition<S, T> definition;
	private AtomicInteger currentStateId;
	
	/**
	 * Constructor with an undefined state machine definition
	 */
	public StateMachine(){
		definition = new StateMachineDefinition<S, T>();
		definition.startDefinition();
	}
	
	/**
	 * Constructor with an already defined state machine definition and an initial state
	 * @param definition		the state machine definition
	 * @param initialState		the initial state of this state machine
	 */
	public StateMachine(StateMachineDefinition<S, T> definition, S initialState){
		this.definition = definition;
		this.currentStateId = new AtomicInteger(definition.getStateId(initialState));
	}

	/**
	 * Constructor with an already defined state machine definition and the first defined state as the initial state
	 * @param definition		the state machine definition
	 */
	public StateMachine(StateMachineDefinition<S, T> definition){
		this.definition = definition;
		this.currentStateId = new AtomicInteger(definition.getFirstStateId());
	}

	
	static public class Transition<SN> implements Serializable{
		private static final long serialVersionUID = 6386811695284573346L;
		int fromStateId;
		int toStateId;
		SN fromState;
		SN toState;
		
		public SN getFromState() {
			return fromState;
		}
		public SN getToState() {
			return toState;
		}
		
		@Override
		public String toString(){
			return fromState.toString() + " -> " + toState.toString();
		}
	}
	
	/**
	 * Add a state to the state machine. If this is the first state added, it will be the start/initial state of the state machine
	 * until overridden by setState(...) method.
	 * @param newState the state to be added, must not be null, and must have not been defined before.
	 */
	public StateMachine<S, T> addState(S newState){
		definition.addState(newState);
		return this;
	}
	
	/**
	 * Add a transition to the state machine. Transition from a state to itself is allowed.
	 * @param newTransition	the transition to be added, must not be null.
	 * @param fromState		from state of the transition, must have been defined already
	 * @param toState		to state of the transition, must have been defined already
	 */
	public StateMachine<S, T> addTransition(T newTransition, S fromState, S toState){
		definition.addTransition(newTransition, fromState, toState);
		return this;
	}
	
	/**
	 * Finalize the definition of the state machine, start it with a specified state.
	 * After calling this method, addSate(...) and addTransition(...) will cause exceptions to be thrown.
	 * @param initialState	the start state
	 */
	public void start(S initialState){
		start();
		setState(initialState);
	}
	
	/**
	 * Finalize the definition of the state machine, start it with the first state added.
	 * After calling this method, addSate(...) and addTransition(...) will cause exceptions to be thrown.
	 */
	public void start(){
		definition.finishDefinition();
		currentStateId = new AtomicInteger(definition.getFirstStateId());
	}
	
	/**
	 * Get current state.
	 * @return	current state
	 */
	public S getState(){
		Integer id = currentStateId.get();
		return definition.getState(id);
	}
	
	/**
	 * Set current state of the state machine. This method is multi-thread safe. 
	 * @param state  the current state to be, must have already been defined.
	 */
	public void setState(S state){
		Integer id = definition.getStateId(state);
		currentStateId.set(id);
	}
	
	/**
	 * Transit from current state to another. The method is multi-thread safe.
	 * @param transition  the transition, must have already been defined.
	 * @return  true if successful. False return indicates that the specified transition does not apply to current state.
	 */
	public boolean transit(T transition){
		S currentState = definition.getState(currentStateId.get());
		if (currentState != null){
			try{
				Transition<S> transitionDef = definition.getTransition(currentState, transition);
				return currentStateId.compareAndSet(transitionDef.fromStateId, transitionDef.toStateId);
			}catch(IllegalArgumentException iae){
				return false;
			}catch(NullPointerException npe){
				return false;
			}
		}
		return false;
	}
	
	/**
	 * Get all states defined.
	 * @return	all the states
	 */
	public Set<S> getStates() {
		return definition.getStates().keySet();
	}
	
	/**
	 * Get all transitions defined
	 * @return  the map of (transition - transition detail)
	 */
	public Map<DoubleValueBean<S, T>, Transition<S>> getTransitions() {
		return definition.getTransitions();
	}
	
	/**
	 * Get all transitions valid for specified state
	 * @param state the state
	 * @return  all transitions valid for the state
	 */
	public Set<T> getTransitions(S state) {
		return definition.getTransitions(state);
	}

	public StateMachineDefinition<S, T> getDefinition() {
		return definition;
	}
	
	

}
