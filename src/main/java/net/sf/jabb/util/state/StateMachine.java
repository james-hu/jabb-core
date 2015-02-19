/**
 * 
 */
package net.sf.jabb.util.state;

import java.io.Serializable;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

import javax.persistence.Transient;

import net.sf.jabb.util.col.MapValueFactory;
import net.sf.jabb.util.col.PutIfAbsentMap;
import net.sf.jabb.util.thread.Sequencer;

import com.google.common.base.Preconditions;
import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.common.collect.ImmutableBiMap;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Maps;

/**
 * State machine with states and actions defined.
 * Definition methods addState and addTransition must be called before other methods and they are not multi-thread safe.
 * Other methods are multi-thread safe.
 * Internally Integer type is used for the state identifiers so the number of states allowed is quite huge.
 * However, there are overheads, so if you need a really big and fast state machine, you'd better create your own lookup array based one.
 * @author James Hu
 *
 * @param <S>	Type of the sate object/name, for example String or Integer or Long.
 * @param <T>	Type of the transition object/name, for example String or Integer or Long.
 */
public class StateMachine<S, T> implements Serializable{
	private static final long serialVersionUID = -2875478874027002800L;

	@Transient		// we only need this when defining the states and transitions
	private Sequencer sequencer = new Sequencer();
	
	private BiMap<S, Integer> states = Maps.synchronizedBiMap(HashBiMap.<S, Integer>create());
	private Map<T, Transition<S, T>> transitions = new ConcurrentHashMap<T, Transition<S, T>>();
	private Map<S, Set<T>> validTransitions = new PutIfAbsentMap<S, Set<T>>(new HashMap<S, Set<T>>(), new MapValueFactory<S, Set<T>>(){
		@Override
		public Set<T> createValue(S key) {
			return Collections.newSetFromMap(new ConcurrentHashMap<T, Boolean>());
		}
	});
	private AtomicInteger currentStateId;
	
	static public class Transition<SN, TN> implements Serializable{
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
		Preconditions.checkArgument(newState != null, "State name cannot be null");
		Integer id = Integer.valueOf((int)sequencer.next());
		Integer previousValue = states.put(newState, id);
		if (previousValue != null){
			throw new IllegalArgumentException("State '" + newState + "' has already been defined.");
		}
		if (currentStateId == null){
			currentStateId = new AtomicInteger(id);
		}
		return this;
	}
	
	/**
	 * Add a transition to the state machine. Transition from a state to itself is allowed.
	 * @param transition	the transition to be added, must not be null.
	 * @param fromState		from state of the transition, must have been defined already
	 * @param toState		to state of the transition, must have been defined already
	 */
	public StateMachine<S, T> addTransition(T newTransition, S fromState, S toState){
		Preconditions.checkArgument(newTransition != null, "Transition name cannot be empty");
		Integer fromStateId = getStateId(fromState);
		Integer toStateId = getStateId(toState);
		
		Transition<S, T> transition = new Transition<S, T>();
		transition.fromStateId = fromStateId;
		transition.toStateId = toStateId;
		transition.fromState = fromState;
		transition.toState = toState;
		
		Transition<S, T> previousValue = transitions.put(newTransition, transition);
		if (previousValue != null){
			throw new IllegalArgumentException("Transition '" + transition + "' has already been defined.");
		}
		validTransitions.get(fromState).add(newTransition);
		return this;
	}
	
	/**
	 * Finalize the definition of the state machine, start it with a specified state.
	 * After calling this method, addSate(...) and addTransition(...) will cause exceptions to be thrown.
	 * @param initialState	the start state
	 */
	public void start(S initialState){
		setState(initialState);
		start();
	}
	
	/**
	 * Finalize the definition of the state machine, start it with the first state added.
	 * After calling this method, addSate(...) and addTransition(...) will cause exceptions to be thrown.
	 */
	public void start(){
		sequencer = null;
		states = ImmutableBiMap.copyOf(states);
		Map<S, Set<T>> validTransitionsCopied = new HashMap<S, Set<T>>();
		for (Map.Entry<S, Set<T>> entry: validTransitions.entrySet()){
			validTransitionsCopied.put(entry.getKey(), ImmutableSet.copyOf(entry.getValue()));
		}
		validTransitions = ImmutableMap.copyOf(validTransitions);
	}
	
	/**
	 * Get current state.
	 * @return	current state
	 */
	public S getState(){
		Integer id = currentStateId.get();
		return states.inverse().get(id);
	}
	
	/**
	 * Set current state of the state machine. This method is multi-thread safe. 
	 * @param state  the current state to be, must have already been defined.
	 */
	public void setState(S state){
		Integer id = getStateId(state);
		currentStateId.set(id);
	}
	
	/**
	 * Transit from current state to another. The method is multi-thread safe.
	 * @param transition  the transition, must have already been defined.
	 * @return  true if successful. False return indicates that the specified transition does not apply to current state.
	 */
	public boolean transit(T transition){
		Transition<S, T> transitionDef = getTransition(transition);
		return currentStateId.compareAndSet(transitionDef.fromStateId, transitionDef.toStateId);
	}
	
	protected Integer getStateId(S state){
		Integer id = states.get(state);
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
		return states.keySet();
	}
	
	/**
	 * Get all transitions defined
	 * @return  the map of (transition - transition detail)
	 */
	public Map<T, Transition<S, T>> getTransitions() {
		return transitions;
	}
	
	/**
	 * Get all transitions valid for specified state
	 * @param state the state
	 * @return  all transitions valid for the state
	 */
	public Set<T> getTransitions(S state) {
		getStateId(state);
		return validTransitions.get(state);
	}
	
	

}
