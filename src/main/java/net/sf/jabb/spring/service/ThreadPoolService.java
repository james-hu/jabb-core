package net.sf.jabb.spring.service;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.ScheduledExecutorService;

public interface ThreadPoolService {

	/**
	 * Get a thread pool by its name.
	 * If the thread pool has no configuration, default configurations will be used to create it.
	 * @param name  name of the thread pool. If the name contains "Scheduled" or "Schd" then a ScheduledExecutorService will be created and/or returned.
	 * @return the thread pool, never null
	 */
	public ExecutorService get(String name);

	/**
	 * get a ScheduledExecutorService thread pool by its name.
	 * If the thread pool has no configuration, default configurations will be used to create it.
	 * @param name name of the thread pool, must contain "Scheduled" or "Schd".
	 * @return	the thread pool, never null
	 */
	public ScheduledExecutorService getScheduled(String name);

	/**
	 * Do a two-phase/two-attempts shutdown of a thread pool.
	 * The thread pool will not be removed after shutdown. 
	 * As a kind of infrastructure service, there is no point for a thread pool to be shutdown and later recreated.
	 * @param name			name of the thread pool
	 * @param waitSeconds	number of seconds to wait in each of the shutdown attempts
	 * @return	true if shutdown completed, false if not
	 */
	public boolean shutdownAndAwaitTermination(String name, long waitSeconds);

	/**
	 * Do a two-phase/two-attempts shutdown of a thread pool.
	 * The thread pool will not be removed after shutdown.
	 * As a kind of infrastructure service, there is no point for a thread pool to be shutdown and later recreated.
	 * @param name			name of the thread pool
	 * @return	true if shutdown completed, false if not
	 */
	public boolean shutdownAndAwaitTermination(String name);

}