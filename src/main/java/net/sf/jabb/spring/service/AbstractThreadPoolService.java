/**
 * 
 */
package net.sf.jabb.spring.service;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import net.sf.jabb.spring.env.PropertiesPropertyResolver;
import net.sf.jabb.util.col.MapValueFactory;
import net.sf.jabb.util.col.PutIfAbsentMap;

import org.apache.commons.lang3.concurrent.BasicThreadFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.env.PropertyResolver;
import org.springframework.core.env.PropertySourcesPropertyResolver;

/**
 * The service to provide thread pools.
 * TODO: 
 * 1) setters to reconfigure the pools, including changing the reject policies; 
 * 2) configure queue type; 
 * 3) configure reject policy; 
 * 4) register custom created pool
 * 5) decorate BlockingQueue to limit the number of elements allowed in the queue, and use that in the pools
 * 6) do we need clear()?
 * 7) get default configurations from configurationsResolver when start so that subclasses don't need to have such code
 * @author James Hu
 *
 */
public abstract class AbstractThreadPoolService extends AbstractSmartLifecycleService implements ThreadPoolService {
	private static final Logger logger = LoggerFactory.getLogger(AbstractThreadPoolService.class);
	
	protected PropertyResolver configurationsResolver;
	protected String configurationsCommonPrefix;
	
	protected Map<String, ThreadPoolExecutor> threadPools;
	protected int defaultCoreSize;
	protected int defaultMaxSize;
	protected long defaultKeepAliveSeconds;
	protected int defaultQueueSize;
	protected boolean defaultAllowCoreThreadTimeout;
	protected long shutdownWaitSeconds;
	
	/**
	 * Constructor. The following parameters will be used to construct an instance:
	 * <ul>
	 *   <li>configurations = null</li>
	 *   <li>configurationsCommonPrefix = null</li>
	 *   <li>defaultCoreSize = 10</li>
	 *   <li>defaultMaxSize = 10</li>
	 *   <li>defaultKeepAliveSeconds = 2*60</li>
	 *   <li>defaultQueueSize = Integer.MAX_VALUE</li>
	 *   <li>defaultAllowCoreThreadTimeout = true</li>
	 *   <li>shutdownWaitSeconds = 5*60</li>
	 * </ul>
	 */
	public AbstractThreadPoolService(){
		this(Collections.emptyMap(), null, 10);
	}

	/**
	 * Constructor. The following parameters will be used to construct an instance:
	 * <ul>
	 *   <li>defaultCoreSize = defaultMaxSize</li>
	 *   <li>defaultKeepAliveSeconds = 2*60</li>
	 *   <li>defaultQueueSize = Integer.MAX_VALUE</li>
	 *   <li>defaultAllowCoreThreadTimeout = true</li>
	 *   <li>shutdownWaitSeconds = 5*60</li>
	 * </ul>
	 * @param configurations	configurations (probably loaded from a .properties file)
	 * @param configurationsCommonPrefix	the common prefix of those configuration items, for example 'myapp.common.threadPools.'
	 * @param defaultMaxSize  		the default max size which is also the default core size.
	 */
	public AbstractThreadPoolService(Map<? extends Object, ? extends Object> configurations, String configurationsCommonPrefix, int defaultMaxSize){
		this(configurations, configurationsCommonPrefix, defaultMaxSize, defaultMaxSize, 2*60, Integer.MAX_VALUE, true, 5*60);
	}
	
	/**
	 * Constructor. The following parameters will be used to construct an instance:
	 * <ul>
	 *   <li>defaultCoreSize = defaultMaxSize</li>
	 *   <li>defaultKeepAliveSeconds = 2*60</li>
	 *   <li>defaultQueueSize = Integer.MAX_VALUE</li>
	 *   <li>defaultAllowCoreThreadTimeout = true</li>
	 *   <li>shutdownWaitSeconds = 5*60</li>
	 * </ul>
	 * @param configurationsResolver	resolver of the configurations 
	 * @param configurationsCommonPrefix	the common prefix of those configuration items, for example 'myapp.common.threadPools.'
	 * @param defaultMaxSize  		the default max size which is also the default core size.
	 */
	public AbstractThreadPoolService(PropertyResolver configurationsResolver, String configurationsCommonPrefix, int defaultMaxSize){
		this(configurationsResolver, configurationsCommonPrefix, defaultMaxSize, defaultMaxSize, 2*60, Integer.MAX_VALUE, true, 5*60);
	}
	
	/**
	 * Constructor
	 * @param configurations	configurations (probably loaded from a .properties file)
	 * @param configurationsCommonPrefix	the common prefix of those configuration items, for example 'myapp.common.threadPools.'
	 * @param defaultCoreSize		the default core size
	 * @param defaultMaxSize		the default max size
	 * @param defaultKeepAliveSeconds	the default keep alive seconds
	 * @param defaultQueueSize			the default queue size
	 * @param defaultAllowCoreThreadTimeout	the default option about whether allow core threads to time out
	 * @param shutdownWaitSeconds		seconds to wait during the thread pool shutdown process
	 */
	public AbstractThreadPoolService(Map<? extends Object, ? extends Object> configurations, String configurationsCommonPrefix, 
			int defaultCoreSize, int defaultMaxSize, long defaultKeepAliveSeconds, int defaultQueueSize, 
			boolean defaultAllowCoreThreadTimeout, long shutdownWaitSeconds){
		this.setConfigurations(configurations);
		this.setConfigurationsCommonPrefix(configurationsCommonPrefix);
		this.setDefaultCoreSize(defaultCoreSize);
		this.setDefaultMaxSize(defaultMaxSize);
		this.setDefaultKeepAliveSeconds(defaultKeepAliveSeconds);
		this.setDefaultQueueSize(defaultQueueSize);
		this.setDefaultAllowCoreThreadTimeout(defaultAllowCoreThreadTimeout);
		this.setShutdownWaitSeconds(shutdownWaitSeconds);
	}
	
	/**
	 * Constructor
	 * @param configurationsResolver	resolver of the configurations 
	 * @param configurationsCommonPrefix	the common prefix of those configuration items, for example 'myapp.common.threadPools.'
	 * @param defaultCoreSize		the default core size
	 * @param defaultMaxSize		the default max size
	 * @param defaultKeepAliveSeconds	the default keep alive seconds
	 * @param defaultQueueSize			the default queue size
	 * @param defaultAllowCoreThreadTimeout	the default option about whether allow core threads to time out
	 * @param shutdownWaitSeconds		seconds to wait during the thread pool shutdown process
	 */
	public AbstractThreadPoolService(PropertyResolver configurationsResolver, String configurationsCommonPrefix, 
			int defaultCoreSize, int defaultMaxSize, long defaultKeepAliveSeconds, int defaultQueueSize, 
			boolean defaultAllowCoreThreadTimeout, long shutdownWaitSeconds){
		this.setConfigurations(configurationsResolver);
		this.setConfigurationsCommonPrefix(configurationsCommonPrefix);
		this.setDefaultCoreSize(defaultCoreSize);
		this.setDefaultMaxSize(defaultMaxSize);
		this.setDefaultKeepAliveSeconds(defaultKeepAliveSeconds);
		this.setDefaultQueueSize(defaultQueueSize);
		this.setDefaultAllowCoreThreadTimeout(defaultAllowCoreThreadTimeout);
		this.setShutdownWaitSeconds(shutdownWaitSeconds);
	}
	
	/**
	 * Set configurations. This methods sets/overrides both the default configurations and the per-pool configurations.
	 * @param commonPrefix	the common prefix in the keys, for example, "threadPools."
	 * @param resolver		the configuration properties resolver
	 */
	protected void setConfigurations(String commonPrefix, PropertySourcesPropertyResolver resolver){
		setConfigurationsCommonPrefix(commonPrefix);
		setConfigurations(resolver);
		
		setDefaultCoreSize(resolver.getProperty(commonPrefix + "defaultCoreSize", Integer.class, defaultCoreSize));
		setDefaultMaxSize(resolver.getProperty(commonPrefix + "defaultMaxSize", Integer.class, defaultMaxSize));
		setDefaultKeepAliveSeconds(resolver.getProperty(commonPrefix + "defaultKeepAliveSeconds", Long.class, defaultKeepAliveSeconds));
		setDefaultQueueSize(resolver.getProperty(commonPrefix + "defaultQueueSize", Integer.class, defaultQueueSize));
		setDefaultAllowCoreThreadTimeout(resolver.getProperty(commonPrefix + "defaultAllowCoreThreadTimeout", Boolean.class, defaultAllowCoreThreadTimeout));
		setShutdownWaitSeconds(resolver.getProperty(commonPrefix + "shutdownWaitSeconds", Long.class, shutdownWaitSeconds));
	}


	
	/* (non-Javadoc)
	 * @see net.sf.jabb.spring.service.ThreadPoolService#get(java.lang.String)
	 */
	@Override
	public ExecutorService get(String name){
		if (!state.isRunning()){
			throw new IllegalStateException("Thread pool service is not in running state");
		}
		return threadPools.get(name);
	}
	
	/**
	 * Clear all the thread pools so that if any of them are needed later, they will be recreated.
	 * However, no thread pool will be terminated inside this method.
	 */
	public void clear(){
		if (threadPools != null){
			threadPools.clear();
		}
	}
	
	/**
	 * Get current status of all the thread pools
	 * @return	status by names sorted by names
	 */
	public Map<String, ThreadPoolStatus> getStatusOfThreadPools(){
		Map<String, ThreadPoolStatus> result = new TreeMap<String, ThreadPoolStatus>();
		for (Map.Entry<String, ThreadPoolExecutor> entry: threadPools.entrySet()){
			ThreadPoolStatus status = new ThreadPoolStatus();
			status.setName(entry.getKey());
			ThreadPoolExecutor pool = entry.getValue();
			status.setActive(pool.getActiveCount());
			status.setKeepAliveSeconds(pool.getKeepAliveTime(TimeUnit.SECONDS));
			status.setLargestSize(pool.getLargestPoolSize());
			status.setMaxSize(pool.getMaximumPoolSize());
			status.setSize(pool.getPoolSize());
			status.setQueueLength(pool.getQueue().size());
			result.put(entry.getKey(), status);
		}
		return result;
	}
	
	/**
	 * Do a two-phase/two-attempts shutdown
	 * @param pool	the thread pool
	 * @param waitSeconds	number of seconds to wait in each of the shutdown attempts
	 * @return	true if shutdown completed, false if not
	 */
	public static boolean shutdownAndAwaitTermination(ExecutorService pool, long waitSeconds) {
		pool.shutdown(); // Disable new tasks from being submitted
		try {
			// Wait a while for existing tasks to terminate
			if(!pool.awaitTermination(waitSeconds, TimeUnit.SECONDS)) {
				//logger.warn("Thread pool is still running: {}", pool);
				pool.shutdownNow(); // Cancel currently executing tasks
				// Wait a while for tasks to respond to being cancelled
				if(!pool.awaitTermination(waitSeconds, TimeUnit.SECONDS)){
					//logger.warn("Thread pool is not terminating: {}", pool);
					return false;
				}else{
					return true;
				}
			}else{
				return true;
			}
		} catch(InterruptedException ie) {
			// (Re-)Cancel if current thread also interrupted
			pool.shutdownNow();
			// Preserve interrupt status
			Thread.currentThread().interrupt();
			return false;
		}
	}

	@Override
	public boolean shutdownAndAwaitTermination(String name, long waitSeconds){
		ExecutorService pool = get(name);
		boolean succeeded = shutdownAndAwaitTermination(pool, waitSeconds);
		return succeeded;
	}

	@Override
	public boolean shutdownAndAwaitTermination(String name){
		return shutdownAndAwaitTermination(name, shutdownWaitSeconds);
	}

	/* (non-Javadoc)
	 * @see net.sf.jabb.spring.service.AbstractSmartLifecycleService#doStart()
	 */
	@Override
	protected void doStart() throws Exception {
		logger.debug("Start with configuration: defaultCoreSize={}, defaultMaxSize={}, defaultKeepAliveSeconds={}, defaultQueueSize={}, defaultAllowCoreThreadTimeout={}, shutdownWaitSeconds={}", 
				new Object[]{defaultCoreSize, defaultMaxSize, defaultKeepAliveSeconds, defaultQueueSize, defaultAllowCoreThreadTimeout, shutdownWaitSeconds});
		this.threadPools = new PutIfAbsentMap<String, ThreadPoolExecutor>(new HashMap<String, ThreadPoolExecutor>(),
				new MapValueFactory<String, ThreadPoolExecutor>(){
					@Override
					public ThreadPoolExecutor createValue(String key) {
						int coreSize = configurationsResolver.getProperty(configurationsCommonPrefix + key + ".coreSize", Integer.class, defaultCoreSize);
						int maxSize = configurationsResolver.getProperty(configurationsCommonPrefix + key + ".maxSize", Integer.class, defaultMaxSize);
						long keepAliveSeconds = configurationsResolver.getProperty(configurationsCommonPrefix + key + ".keepAliveSeconds", Long.class, defaultKeepAliveSeconds);
						int queueSize = configurationsResolver.getProperty(configurationsCommonPrefix + key + ".queueSize", Integer.class, defaultQueueSize);
						boolean allowCoreThreadTimeout = configurationsResolver.getProperty(configurationsCommonPrefix + key + ".allowCoreThreadTimeout", Boolean.class, defaultAllowCoreThreadTimeout);

						ThreadPoolExecutor pool = new ThreadPoolExecutor(coreSize, maxSize, 
								keepAliveSeconds, TimeUnit.SECONDS, queueSize < 10000000 ? new ArrayBlockingQueue<Runnable>(queueSize) : new LinkedBlockingQueue<Runnable>(queueSize),
								new BasicThreadFactory.Builder().namingPattern(key + "-%04d").build());
						pool.allowCoreThreadTimeOut(allowCoreThreadTimeout);
						logger.debug("Created thread pool '{}': coreSize={}, maxSize={}, keepAliveSeconds={}, queueSize={}, allowCoreThreadTimeout={}", 
								new Object[]{key, coreSize, maxSize, keepAliveSeconds, queueSize, allowCoreThreadTimeout});
						return pool;
					}
		});
	}

	/* (non-Javadoc)
	 * @see net.sf.jabb.spring.service.AbstractSmartLifecycleService#doStop()
	 */
	@Override
	protected void doStop() throws Exception {
		for (ThreadPoolExecutor pool: threadPools.values()){
			pool.shutdown();
		}

		for (ThreadPoolExecutor pool: threadPools.values()){
			shutdownAndAwaitTermination(pool, shutdownWaitSeconds);
		}

	}
	
	public void setConfigurations(Map<? extends Object, ? extends Object> configurations){
		this.configurationsResolver = configurations == null ? null : new PropertiesPropertyResolver(configurations);
	}

	public void setConfigurations(PropertyResolver configurationsResolver) {
		this.configurationsResolver = configurationsResolver;
	}

	public int getDefaultCoreSize() {
		return defaultCoreSize;
	}

	public int getDefaultMaxSize() {
		return defaultMaxSize;
	}

	public long getDefaultKeepAliveSeconds() {
		return defaultKeepAliveSeconds;
	}

	public int getDefaultQueueSize() {
		return defaultQueueSize;
	}

	public boolean isDefaultAllowCoreThreadTimeout() {
		return defaultAllowCoreThreadTimeout;
	}

	public long getShutdownWaitSeconds() {
		return shutdownWaitSeconds;
	}

	public String getConfigurationsCommonPrefix() {
		return configurationsCommonPrefix;
	}

	public void setConfigurationsCommonPrefix(String configurationsCommonPrefix) {
		this.configurationsCommonPrefix = configurationsCommonPrefix;
	}

	public void setDefaultCoreSize(int defaultCoreSize) {
		this.defaultCoreSize = defaultCoreSize;
	}

	public void setDefaultMaxSize(int defaultMaxSize) {
		this.defaultMaxSize = defaultMaxSize;
	}

	public void setDefaultKeepAliveSeconds(long defaultKeepAliveSeconds) {
		this.defaultKeepAliveSeconds = defaultKeepAliveSeconds;
	}

	public void setDefaultQueueSize(int defaultQueueSize) {
		this.defaultQueueSize = defaultQueueSize;
	}

	public void setDefaultAllowCoreThreadTimeout(
			boolean defaultAllowCoreThreadTimeout) {
		this.defaultAllowCoreThreadTimeout = defaultAllowCoreThreadTimeout;
	}

	public void setShutdownWaitSeconds(long shutdownWaitSeconds) {
		this.shutdownWaitSeconds = shutdownWaitSeconds;
	}

}
