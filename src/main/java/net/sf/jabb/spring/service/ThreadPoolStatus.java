package net.sf.jabb.spring.service;

/**
 * Status of thread pool
 * @author James Hu
 *
 */
public class ThreadPoolStatus {
	protected String name;

	protected int size;
	protected int active;
	protected int queueLength;
	protected int queueSize;
	protected int queueLengthLimit;
	
	protected int largestSize;
	protected int maxSize;
	protected long keepAliveSeconds;
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	/**
	 * Get the current size of the thread pool
	 * @return	number of threads in the pool
	 */
	public int getSize() {
		return size;
	}
	public void setSize(int size) {
		this.size = size;
	}
	/**
	 * Get the number of active threads in the pool
	 * @return	number of threads that are executing tasks
	 */
	public int getActive() {
		return active;
	}
	public void setActive(int active) {
		this.active = active;
	}
	/**
	 * Get the current length of the queue
	 * @return	number of tasks currently in the queue
	 */
	public int getQueueLength() {
		return queueLength;
	}
	public void setQueueLength(int queueLength) {
		this.queueLength = queueLength;
	}
	public int getLargestSize() {
		return largestSize;
	}
	public void setLargestSize(int largestSize) {
		this.largestSize = largestSize;
	}
	public int getMaxSize() {
		return maxSize;
	}
	public void setMaxSize(int maxSize) {
		this.maxSize = maxSize;
	}
	public long getKeepAliveSeconds() {
		return keepAliveSeconds;
	}
	public void setKeepAliveSeconds(long keepAliveSeconds) {
		this.keepAliveSeconds = keepAliveSeconds;
	}
	/**
	 * Get the size of the task queue
	 * @return	actual size of the task queue reported by the underlying queue implementation
	 */
	public int getQueueSize() {
		return queueSize;
	}
	public void setQueueSize(int queueSize) {
		this.queueSize = queueSize;
	}
	/**
	 * Get the soft limit of the queue length, this is always smaller than the size of the task queue
	 * @return	the soft limit of the queue length
	 */
	public int getQueueLengthLimit() {
		return queueLengthLimit;
	}
	public void setQueueLengthLimit(int queueLengthLimit) {
		this.queueLengthLimit = queueLengthLimit;
	}
	
	
}
