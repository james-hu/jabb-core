/**
 * 
 */
package net.sf.jabb.util.state;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.concurrent.atomic.AtomicLong;

import net.sf.jabb.util.thread.Sequencer;

import com.google.common.base.Preconditions;
import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;

/**
 * State machine with states and actions defined.
 * Definition methods addState and addTransition must be called before other methods and they are not multi-thread safe.
 * Other methods are multi-thread safe.
 * Internally Long type is used for the state identifiers so the number of states allowed is quite huge.
 * @author James Hu
 *
 * @param <S>	Type of the sate object/name, for example String or Integer or Long.
 * @param <T>	Type of the transition object/name, for example String or Integer or Long.
 */
public class StateMachine<S, T> {
	private Sequencer sequencer = new Sequencer();
	private BiMap<S, Long> states = HashBiMap.<S, Long>create();
	private Map<T, Transition<S, T>> transitions = new HashMap<T, Transition<S, T>>();
	private AtomicLong currentState;
	
	static public class Transition<SN, TN>{
		long fromStateId;
		long toStateId;
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
	public void addState(S newState){
		Preconditions.checkArgument(newState != null, "State name cannot be null");
		Long id = Long.valueOf(sequencer.next());
		Long previousValue = states.put(newState, id);
		if (previousValue != null){
			throw new IllegalArgumentException("State '" + newState + "' has already been defined.");
		}
		if (currentState == null){
			currentState = new AtomicLong(id);
		}
	}
	
	/**
	 * Add a transition to the state machine. Transition from a state to itself is allowed.
	 * @param transition	the transition to be added, must not be null.
	 * @param fromState		from state of the transition, must have been defined already
	 * @param toState		to state of the transition, must have been defined already
	 */
	public void addTransition(T newTransition, S fromState, S toState){
		Preconditions.checkArgument(newTransition != null, "Transition name cannot be empty");
		Long fromStateId = getStateId(fromState);
		Long toStateId = getStateId(toState);
		
		Transition<S, T> transition = new Transition<S, T>();
		transition.fromStateId = fromStateId;
		transition.toStateId = toStateId;
		transition.fromState = fromState;
		transition.toState = toState;
		
		Transition<S, T> previousValue = transitions.put(newTransition, transition);
		if (previousValue != null){
			throw new IllegalArgumentException("Transition '" + transition + "' has already been defined.");
		}
	}
	
	/**
	 * Get current state.
	 * @return	current state
	 */
	public S getState(){
		Long id = currentState.get();
		return states.inverse().get(id);
	}
	
	/**
	 * Set current state of the state machine. This method is multi-thread safe. 
	 * @param state  the current state to be, must have already been defined.
	 */
	public void setState(S state){
		Long id = getStateId(state);
		currentState.set(id);
	}
	
	/**
	 * Transit from current state to another. The method is multi-thread safe.
	 * @param transition  the transition, must have already been defined.
	 * @return  true if successful. False return indicates that the specified transition does not apply to current state.
	 */
	public boolean transit(T transition){
		Transition<S, T> transitionDef = getTransition(transition);
		return currentState.compareAndSet(transitionDef.fromStateId, transitionDef.toStateId);
	}
	
	protected Long getStateId(S state){
		Long id = states.get(state);
		if (id == null){
			throw new IllegalArgumentException("State '" + state + "' has not been defined.");
		}
		return id;
	}
	
	protected Transition<S, T> getTransition(T transition){
		Transition<S, T> transitionDef = transitions.get(transition);
		if (transitionDef == null){
			throw new IllegalArgumentException("Transition '" + transition + "' has not been defined.");
		}
		return transitionDef;
	}
	/**
	 * Get all states defined.
	 * @return	all the states
	 */
	public Set<S> getStates() {
		return new TreeSet<S>(states.keySet());
	}
	
	/**
	 * Get all transitions defined
	 * @return  the map of (transition name - transition detail)
	 */
	public Map<T, Transition<S, T>> getTransitions() {
		return new TreeMap<T, Transition<S, T>>(transitions);
	}
	
	

}
