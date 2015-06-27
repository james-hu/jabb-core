/**
 * 
 */
package net.sf.jabb.util.state;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import javax.persistence.Transient;

import net.sf.jabb.util.col.MapValueFactory;
import net.sf.jabb.util.col.PutIfAbsentMap;
import net.sf.jabb.util.state.StateMachine.Transition;
import net.sf.jabb.util.thread.Sequencer;

import com.google.common.base.Preconditions;
import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.common.collect.ImmutableBiMap;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Maps;

/**
 * Definition of the state machine. Instance of StateMachineDefinition can be shared among multiple instances of StateMachine.
 * Definition methods addState(...) and addTransition(...) must be called before finishDefinition(...) methods and they are not thread safe.
 * @author James Hu
 *
 */
public class StateMachineDefinition<S, T> implements Serializable {
	private static final long serialVersionUID = -5541843518717440091L;

	@Transient		// we only need this when defining the states and transitions
	private Sequencer sequencer = new Sequencer();

	private BiMap<S, Integer> states;
	private Map<T, Transition<S, T>> transitions;
	private Map<S, Set<T>> validTransitions;
	
	/**
	 * Constructor for an empty definition.
	 * You must call startDefinition() first, then add states and transitions, then at last call finishDefinition();
	 */
	public StateMachineDefinition(){
	}
	
	/**
	 * The copy constructor
	 * @param other		another instance from which the definitions will be copied
	 */
	public StateMachineDefinition(StateMachineDefinition<S, T> other){
		this.states = other.states;
		this.transitions = other.transitions;
		this.validTransitions = other.validTransitions;
	}
	
	/**
	 * Initialize internal data structure for adding state and transitions later.
	 */
	public void startDefinition(){
		sequencer = new Sequencer();
		states = Maps.synchronizedBiMap(HashBiMap.<S, Integer>create());
		transitions = new ConcurrentHashMap<T, Transition<S, T>>();
		validTransitions = new PutIfAbsentMap<S, Set<T>>(new HashMap<S, Set<T>>(), new MapValueFactory<S, Set<T>>(){
			@Override
			public Set<T> createValue(S key) {
				return Collections.newSetFromMap(new ConcurrentHashMap<T, Boolean>());
			}
		});
	}

	/**
	 * Finalize internal data structure after all the states and transitions have been added.
	 */
	public void finishDefinition(){
		sequencer = null;
		states = ImmutableBiMap.copyOf(states);
		Map<S, Set<T>> validTransitionsCopied = new HashMap<S, Set<T>>();
		for (Map.Entry<S, Set<T>> entry: validTransitions.entrySet()){
			validTransitionsCopied.put(entry.getKey(), ImmutableSet.copyOf(entry.getValue()));
		}
		validTransitions = ImmutableMap.copyOf(validTransitions);
	}
	
	/**
	 * Get the internal integer ID of a state.
	 * @param state	the state
	 * @return	the integer ID of that state
	 */
	public Integer getStateId(S state){
		Integer id = states.get(state);
		if (id == null){
			throw new IllegalArgumentException("State '" + state + "' has not been defined.");
		}
		return id;
	}
	
	/**
	 * Get the state
	 * @param id	the internal integer ID of the state
	 * @return		the state with that integer ID
	 */
	public S getState(Integer id){
		return states.inverse().get(id);
	}
	
	/**
	 * Get the transition definitions
	 * @param transition	the transition
	 * @return	the definitions
	 */
	public Transition<S, T> getTransition(T transition){
		Transition<S, T> transitionDef = transitions.get(transition);
		if (transitionDef == null){
			throw new IllegalArgumentException("Transition '" + transition + "' has not been defined.");
		}
		return transitionDef;
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


	
	/**
	 * Add a state to the state machine. If this is the first state added, it will be the start/initial state of the state machine
	 * until overridden by setState(...) method.
	 * @param newState the state to be added, must not be null, and must have not been defined before.
	 */
	public StateMachineDefinition<S, T> addState(S newState){
		Preconditions.checkArgument(newState != null, "State name cannot be null");
		Integer id = Integer.valueOf((int)sequencer.next());
		Integer previousValue = states.put(newState, id);
		if (previousValue != null){
			throw new IllegalArgumentException("State '" + newState + "' has already been defined.");
		}
		return this;
	}

	/**
	 * Add a transition to the state machine. Transition from a state to itself is allowed.
	 * @param newTransition	the transition to be added, must not be null.
	 * @param fromState		from state of the transition, must have been defined already
	 * @param toState		to state of the transition, must have been defined already
	 */
	public StateMachineDefinition<S, T> addTransition(T newTransition, S fromState, S toState){
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
	 * Get the ID of the state that has been defined first.
	 * @return	ID of the first state
	 */
	public Integer getFirstStateId(){
		Set<Integer> ids = states.inverse().keySet();
		if (ids.size() < 1 ){
			throw new IllegalStateException("There is no state defined.");
		}
		Integer[] idsArray = new Integer[ids.size()];
		Arrays.sort(ids.toArray(idsArray));
		return idsArray[0];
	}
	
	public BiMap<S, Integer> getStates() {
		return states;
	}
	public Map<T, Transition<S, T>> getTransitions() {
		return transitions;
	}
	public Map<S, Set<T>> getValidTransitions() {
		return validTransitions;
	}
}
