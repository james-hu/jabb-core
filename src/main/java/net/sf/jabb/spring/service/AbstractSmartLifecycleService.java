/**
 * 
 */
package net.sf.jabb.spring.service;

import net.sf.jabb.util.state.StartStopStateMachine;

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
		setLifecycleConfigurations(configResolver, null);
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
	 * 										If it is null, then the default one 'lifecycle.' will be used.
	 */
	protected void setLifecycleConfigurations(PropertyResolver configResolver, String configurationsCommonPrefix){
		String className = this.getClass().getName();
		if (configurationsCommonPrefix == null){
			configurationsCommonPrefix = DEFAULT_CONFIG_PREFIX;
		}
		this.phase = configResolver.getProperty(configurationsCommonPrefix + className + ".phase", Integer.class, 0);
		this.isAutoStart = configResolver.getProperty(configurationsCommonPrefix + className + ".autoStart", Boolean.class, false);
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
	public void stop(Runnable callback) {
		stop();
		callback.run();
	}

}
