/**
 * 
 */
package net.sf.jabb.util.state;

import java.io.Serializable;
import java.lang.reflect.Type;
import java.lang.reflect.ParameterizedType;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import net.sf.jabb.util.bean.DoubleValueBean;


/**
 * Wrapper of a state machine that can be applied to specific use case.
 * Definitions of the state machines defined by subclasses are cached in memory.
 * @author James Hu
 *
 */
public abstract class StateMachineWrapper<S, T> implements Serializable{
	private static final long serialVersionUID = -2873965028262709114L;

	private static Map<Object, StateMachineDefinition<?, ?>> definitions = new HashMap<Object, StateMachineDefinition<?, ?>>();
	
	protected  StateMachine<S, T> stateMachine;
	
	/**
	 * The method to define the state machine.
	 * Inside this method should call addState(...) and addTransition(...) method and should not call finishDefinition() method.
	 * The initial state of the state  machine must be added as the first state.
	 * 
	 * @param definition	The StateMachineDefinition instance for which the method startDefinition() had already been called.
	 */
	abstract protected void define(StateMachineDefinition<S, T> definition);
	
	@SuppressWarnings("unchecked")
	public StateMachineWrapper(){
		//Class<?> type = this.getClass();
		Type superClass = this.getClass().getGenericSuperclass();
		Type[] typeArguments  = ((ParameterizedType)superClass).getActualTypeArguments();
		
		Object key = new DoubleValueBean<Type, Type[]>(superClass, typeArguments);
		StateMachineDefinition<?, ?> definition = definitions.get(key);
		if (definition == null){
			synchronized(definitions){
				definition = definitions.get(key);
				if (definition == null){
					definition = new StateMachineDefinition<S, T>();
					definition.startDefinition();
					define((StateMachineDefinition<S, T>)definition);
					definition.finishDefinition();
					definitions.put(key, definition);
				}
			}
		}
		
		stateMachine = new StateMachine<S, T>((StateMachineDefinition<S, T>)definition);
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
	
	/**
	 * Get all transitions valid for specified state
	 * @param state the state
	 * @return  all transitions valid for the state
	 */
	public Set<T> getTransitions(S state) {
		return stateMachine.getTransitions(state);
	}

	
	@Override
	public String toString(){
		return stateMachine.getState().toString();
	}
}
