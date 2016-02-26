/**
 * 
 */
package net.sf.jabb.spring.service;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import net.sf.jabb.util.state.StartStopStateMachine;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.concurrent.BasicThreadFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.SmartLifecycle;
import org.springframework.core.env.PropertyResolver;

/**
 * An abstract implementation of SmartLifecycle.
 * Sub-classes should set values of phase and isAutoStart fields
 * @author James Hu
 *
 */
public abstract class AbstractSmartLifecycleService implements SmartLifecycle {
	private static Logger logger = LoggerFactory.getLogger(AbstractSmartLifecycleService.class);
	public static final String DEFAULT_CONFIG_PREFIX = "lifecycle.";
	
	protected int phase;
	protected boolean isAutoStart;
	protected StartStopStateMachine state = new StartStopStateMachine();
	
	abstract protected void doStart() throws Exception;
	abstract protected void doStop() throws Exception;
	
	protected static ExecutorService serviceThreadPool = Executors.newCachedThreadPool(new BasicThreadFactory.Builder().namingPattern("SmartLifecycleService-%04d").build()); 
	
	/**
	 * Configure isAutoStart and phase from properties.
	 * For example, if the class name of the service is a.b.Xyz, then the properties will be some thing like:
	 * <ul>
	 * 	<li>lifecycle.a.b.Xyz.phase=3</li>
	 * 	<li>lifecycle.a.b.Xyz.autoStart=true</li>
	 * </ul>
	 * This method is typically called from subclass after an instance of Environment is injected.
	 * @param configResolver	the PropertyResolver (normally the Environment) containing the configurations
	 */
	protected void setLifecycleConfigurations(PropertyResolver configResolver){
		setLifecycleConfigurations(configResolver, DEFAULT_CONFIG_PREFIX, null);
	}
	
	/**
	 * Configure isAutoStart and phase from properties.
	 * For example, if the class name of the service is a.b.Xyz, then the properties will be some thing like:
	 * <ul>
	 * 	<li>lifecycle.a.b.Xyz.phase=3</li>
	 * 	<li>lifecycle.a.b.Xyz.autoStart=true</li>
	 * </ul>
	 * This method is typically called from subclass after an instance of Environment is injected.
	 * @param configResolver	the PropertyResolver (normally the Environment) containing the configurations
	 * @param interfaceClass	the interface implemented. If it is not null, properties named after the interface will be get first,
	 * 							and then the values can be overridden by the properties named after the implementation.
	 */
	protected void setLifecycleConfigurations(PropertyResolver configResolver, Class<?> interfaceClass){
		setLifecycleConfigurations(configResolver, DEFAULT_CONFIG_PREFIX, interfaceClass);
	}
	
	/**
	 * Configure isAutoStart and phase from properties.
	 * For example, if the class name of the service is a.b.Xyz, then the properties will be some thing like:
	 * <ul>
	 * 	<li>lifecycle.a.b.Xyz.phase=3</li>
	 * 	<li>lifecycle.a.b.Xyz.autoStart=true</li>
	 * </ul>
	 * This method is typically called from subclass after an instance of Environment is injected.
	 * @param configResolver	the PropertyResolver (normally the Environment) containing the configurations
	 * @param configurationsCommonPrefix	the common prefix of those configuration items, for example 'myapp.common.lifecycle.'.
	 * 										If it is null, then there will be no prefix prepended.
	 * @param interfaceClass	the interface implemented. If it is not null, properties with the following names will be tried:
	 * 							<ol>
	 * 								<li>configurationsCommonPrefix + this.getClass().getName() + ".phase"</li>
	 * 								<li>configurationsCommonPrefix + this.getClass().getSimpleName() + ".phase"</li>
	 * 								<li>configurationsCommonPrefix + StringUtils.uncapitalize(this.getClass().getSimpleName()) + ".phase"</li>
	 * 								<li>configurationsCommonPrefix + interfaceClass.getName() + ".phase"</li>
	 * 								<li>configurationsCommonPrefix + interfaceClass.getSimpleName() + ".phase"</li>
	 * 								<li>configurationsCommonPrefix + StringUtils.uncapitalize(interfaceClass.getSimpleName()) + ".phase"</li>
	 * 							</ol>
	 * 							for the phase, and
	 * 							<ol>
	 * 								<li>configurationsCommonPrefix + this.getClass().getName() + ".autoStart"</li>
	 * 								<li>configurationsCommonPrefix + this.getClass().getSimpleName() + ".autoStart"</li>
	 * 								<li>configurationsCommonPrefix + StringUtils.uncapitalize(this.getClass().getSimpleName()) + ".autoStart"</li>
	 * 								<li>configurationsCommonPrefix + interfaceClass.getName() + ".autoStart"</li>
	 * 								<li>configurationsCommonPrefix + interfaceClass.getSimpleName() + ".autoStart"</li>
	 * 								<li>configurationsCommonPrefix + StringUtils.uncapitalize(interfaceClass.getSimpleName()) + ".autoStart"</li>
	 * 							</ol>
	 * 							for the auto-start.
	 * 							<p>The first one with non-null value wins.</p>
	 */
	protected void setLifecycleConfigurations(PropertyResolver configResolver, String configurationsCommonPrefix, Class<?> interfaceClass){
		Integer phase = getConfigProperty(configResolver, configurationsCommonPrefix, ".phase", Integer.class, interfaceClass);
		if (phase == null){
			logger.warn("No configuration found for the phase of SmartLifecycle service '{}', 0 will be used.", this.getClass().getName());
			phase = 0;
		}
		
		Boolean isAutoStart = getConfigProperty(configResolver, configurationsCommonPrefix, ".autoStart", Boolean.class, interfaceClass);
		if (isAutoStart == null){
			logger.warn("No configuration found for the auto-start of SmartLifecycle service '{}', false will be used.", this.getClass().getName());
			isAutoStart = false;
		}

		this.phase = phase;
		this.isAutoStart = isAutoStart;
	}
	
	protected <T> T getConfigProperty(PropertyResolver configResolver, String configurationsCommonPrefix, String suffix, Class<T> propertyClass, Class<?> interfaceClass){
		String className = this.getClass().getName();
		String simpleClassName = this.getClass().getSimpleName();
		if (configurationsCommonPrefix == null){
			configurationsCommonPrefix = "";
		}

		T result = configResolver.getProperty(configurationsCommonPrefix + className + suffix, propertyClass);
		if (result == null){
			result = configResolver.getProperty(configurationsCommonPrefix + simpleClassName + suffix, propertyClass);
		}
		if (result == null){
			result = configResolver.getProperty(configurationsCommonPrefix + StringUtils.uncapitalize(simpleClassName) + suffix, propertyClass);
		}
		if (interfaceClass != null){
			className = interfaceClass.getName();
			simpleClassName = interfaceClass.getSimpleName();

			if (result == null){
				result = configResolver.getProperty(configurationsCommonPrefix + className + suffix, propertyClass);
			}
			if (result == null){
				result = configResolver.getProperty(configurationsCommonPrefix + simpleClassName + suffix, propertyClass);
			}
			if (result == null){
				result = configResolver.getProperty(configurationsCommonPrefix + StringUtils.uncapitalize(simpleClassName) + suffix, propertyClass);
			}
		}
		
		return result;
	}

	public String getServiceState(){
		return state.getStateAsString();
	}

	/* (non-Javadoc)
	 * @see org.springframework.context.Lifecycle#isRunning()
	 */
	@Override
	public boolean isRunning() {
		return state.isRunning();
	}

	/* (non-Javadoc)
	 * @see org.springframework.context.Lifecycle#start()
	 */
	@Override
	public void start() {
		if (state.start()){
			try{
				doStart();
				state.finishStarting();
			}catch(Exception e){
				state.failStarting();
				logger.warn("Failed to start {}", this.getClass().getName(), e);
			}
		}

	}


	/* (non-Javadoc)
	 * @see org.springframework.context.Lifecycle#stop()
	 */
	@Override
	public void stop() {
		if (state.stop()){
			try{
				doStop();
				state.finishStopping();
			}catch(Exception e){
				state.failStopping();
				logger.warn("Failed to stop {}", this.getClass().getName(), e);
			}
		}

	}

	/* (non-Javadoc)
	 * @see org.springframework.context.Phased#getPhase()
	 */
	@Override
	public int getPhase() {
		return phase;
	}

	/* (non-Javadoc)
	 * @see org.springframework.context.SmartLifecycle#isAutoStartup()
	 */
	@Override
	public boolean isAutoStartup() {
		return isAutoStart;
	}

	/* (non-Javadoc)
	 * @see org.springframework.context.SmartLifecycle#stop(java.lang.Runnable)
	 */
	@Override
	public void stop(final Runnable callback) {
		if (state.stop()){
			serviceThreadPool.execute(new Runnable(){
				@Override
				public void run() {
					try{
						doStop();
						state.finishStopping();
						callback.run();
					}catch(Exception e){
						state.failStopping();
						logger.warn("Failed to stop {}", this.getClass().getName(), e);
					}
				}
			});
		}
	}

}
