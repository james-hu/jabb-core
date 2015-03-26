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
	
	protected int largestSize;
	protected int maxSize;
	protected long keepAliveSeconds;
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public int getSize() {
		return size;
	}
	public void setSize(int size) {
		this.size = size;
	}
	public int getActive() {
		return active;
	}
	public void setActive(int active) {
		this.active = active;
	}
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
	
	
}
