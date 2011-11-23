/*
Copyright 2010-2011 Zhengmao HU (James)

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
*/

package net.sf.jabb.util.thread;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * A template for consuming data from a queue.<br>
 * 一个从队列中取数据进行处理的模板。
 * <p>
 * One working thread will be created for each instance of this class when necessary.
 * <p>
 * 本类的每个实例相应的会有一个工作线程在需要的时候被创建。
 * 
 * @author Zhengmao HU (James)
 * 
 * @param <E>	Type of the data in the queue.<br>队列中数据的类型
 *
 */
public abstract class QueueConsumer<E> implements Runnable{
	protected BlockingQueue<E> queue;
	/**
	 * The working thread.<br>
	 * 工作线程。
	 */
	protected Thread thread;
	protected AtomicInteger mode;
	/**
	 * Name of this instance, which determines the naming of working thread.<br>
	 * 本个实例的名字，它决定了工作线程的名字。
	 */
	protected String name;

	static protected final int MODE_INIT = 0;
	static protected final int MODE_START = 1;
	static protected final int MODE_RUNNING = 2;
	static protected final int MODE_STOP_ASAP = 3;
	static protected final int MODE_STOP_WHEN_EMPTY = 4;
	static protected final int MODE_STOPPED = 5;
	
	/**
	 * If no ExecutorService is specified in constructor,
	 * then the working thread will come from this thread pool.<br>
	 * 如果在构造方法中没有指定ExecutorService，则它的工作线程
	 * 将取自这个线程池。
	 * <p>
	 * defaultThreadPool = Executors.newCachedThreadPool()
	 */
	static protected ExecutorService defaultThreadPool;
	
	/**
	 * Effective thread pool of this instance.<br>
	 * 本个实例实际使用的线程池。
	 */
	protected ExecutorService threadPool;
	
	static{
		defaultThreadPool = Executors.newCachedThreadPool();
	}
	
	public void setName(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}
	
	public void setExecutorService(ExecutorService threadPool) {
		this.threadPool = threadPool;
	}

	/**
	 * Use this method if you don't want the queue to be passed in from constructor.<br>
	 * 如果你不想通过构造方法来传递队列对象，就用这个方法。
	 * 
	 * @param workQueue 	The queue that data for processing will be fetched from.<br>
	 * 						本实例将从这个队列取得待处理数据。
	 */
	public void setQueue(BlockingQueue<E> workQueue) {
		this.queue = workQueue;
	}
	
	/**
	 * Get the work queue.<br>
	 * 取得工作队列。
	 * @return The work queue.<br>工作队列。
	 */
	public BlockingQueue<E> getQueue() {
		return queue;
	}

	/**
	 * Constructor to create an instance.<br>
	 * 创建一个实例。
	 * 
	 * @param name			Name of this instance, which determines the naming of working thread.<br>
	 * 						本个实例的名称，会被用在工作线程名里。
	 * @param workQueue			The queue that data for processing will be fetched from.<br>
	 * 							本实例将从这个队列取得待处理数据。
	 * @param executorService	Thread pool that working thread will be get from.<br>
	 * 							指定让本实例从这里获得工作线程。
	 */
	public QueueConsumer(BlockingQueue<E> workQueue, String name, ExecutorService executorService){
		threadPool = executorService;
		this.name = name;
		queue = workQueue;
		mode = new AtomicInteger(MODE_INIT);
	}
	
	/**
	 * Constructor to create an instance.<br>
	 * 创建一个实例。
	 * 
	 * @param name			Name of this instance, which determines the naming of working thread.<br>
	 * 						本个实例的名称，会被用在工作线程名里。
	 * @param executorService	Thread pool that working thread will be get from.<br>
	 * 							指定让本实例从这里获得工作线程。
	 */
	public QueueConsumer(String name, ExecutorService executorService){
		this(null, name, executorService);
	}
	
	
	/**
	 * Constructor to create an instance using default thread pool.<br>
	 * 创建一个使用缺省线程池的实例。
	 * 
	 * @param name			Name of this instance, which determines the naming of working thread.<br>
	 * 						本个实例的名称，会被用在工作线程名里。
	 * @param workQueue			The queue that data for processing will be fetched from.<br>
	 * 							本实例将从这个队列取得待处理数据。
	 */
	public QueueConsumer(BlockingQueue<E> workQueue, String name){
		this(workQueue, name, defaultThreadPool);
	}
	
	/**
	 * Constructor to create an instance using default thread pool.<br>
	 * 创建一个使用缺省线程池的实例。
	 * 
	 * @param name			Name of this instance, which determines the naming of working thread.<br>
	 * 						本个实例的名称，会被用在工作线程名里。
	 */
	public QueueConsumer(String name){
		this(null, name, defaultThreadPool);
	}

	/**
	 * Constructor to create an instance with default name and using default thread pool.<br>
	 * 创建一个实例，使用缺省的名称和缺省的线程池。
	 * 
	 * @param workQueue			The queue that data for processing will be fetched from.<br>
	 * 							本实例将从这个队列取得待处理数据。
	 */
	public QueueConsumer(BlockingQueue<E> workQueue){
		this(workQueue, QueueConsumer.class.getSimpleName());
	}
	
	/**
	 * Constructor to create an instance with default name and using default thread pool.<br>
	 * 创建一个实例，使用缺省的名称和缺省的线程池。
	 */
	public QueueConsumer(){
		this(null, QueueConsumer.class.getSimpleName());
	}
	
	/**
	 * Put data into the queue for processing, if the queue still has space
	 * this method will return immediately 
	 * without waiting for the data to be actually processed.<br>
	 * 把待处理数据放入队列，如果队列中还有空位置则这个方法会立即返回而不是等待实际处理完成。
	 * <p>
	 * If the queue has no space left, this method will wait for the space then put data into the queue for processing,
	 * after that, this method will return immediately without waiting for the data to be actually processed.
	 * <p>
	 * 如果队列中没有空位置了，则会等待队列空出位置来之后再把数据放进去，放完之后这个方法会立即返回而不是等待实际处理完成。
	 * 
	 * @param obj	Data need to be processed<br>
	 * 				待处理的数据。
	 * @throws InterruptedException if interrupted while waiting for space to become available.<br>
	 * 								如果队列已满而在等待空出位置的时候发生了中断。
	 */
	public void queue(E obj) throws InterruptedException{
		queue.put(obj);
	}
	
	/**
	 * Start working thread; This method will return immediately 
	 * without waiting for anything to be actually processed.<br>
	 * 启动工作处理线程，这个方法立即返回，而不等待任何实际处理发生。
	 */
	public void start(){
		if (mode.compareAndSet(MODE_INIT, MODE_START)){
			threadPool.execute(this);
		}
	}
	
	/**
	 * Stop working thread after the queue is empty; 
	 * This method will not return until working thread finishes.<br>
	 * 等待队列处理空了之后停止处理线程，这个方法会等到工作处理线程结束才返回。
	 */
	public void stop(){
		stop(true);
	}

	/**
	 * Stop working thread; 
	 * This method will not return until working thread finishes.<br>
	 * 停止处理线程，这个方法会等到工作处理线程结束才返回。
	 * 
	 * @param afterQueueEmpty	true if working thread should keep processing until the queue is empty;<br>
	 * 							false if working thread should stop after finished current work;<br>
	 * 							如果为true，则工作线程要等到队列处理空了才结束；<br>
	 * 							如果为false，则工作线程处理完当前数据就结束。
	 */
	public void stop(boolean afterQueueEmpty){
		preStop(afterQueueEmpty);
		while(!(mode.compareAndSet(MODE_STOPPED, MODE_INIT) 
				|| mode.get() == MODE_INIT)){
			try {
				thread.join(100);
			} catch (InterruptedException e) {
				// do nothing
			}
			preStop(afterQueueEmpty);
			// 也存在这种可能性：在while作判断的时候，queue.size()>0，
			// 而take()的时候已经没东西取了，所以join之后也不能死等，要反复interrupt。
			thread.interrupt();
		}
	}
	
	/**
	 * Ask the working thread to stop;
	 * This method will return immediately.<br>
	 * 要求工作线程停止；这个方法立即返回。
	 * 
	 * @param afterQueueEmpty	true if working thread should keep processing until the queue is empty;<br>
	 * 							false if working thread should stop after finished current work;<br>
	 * 							如果为true，则工作线程要等到队列处理空了才结束；<br>
	 * 							如果为false，则工作线程处理完当前数据就结束。
	 */
	public void preStop(boolean afterQueueEmpty){
		mode.compareAndSet(MODE_RUNNING, afterQueueEmpty ? MODE_STOP_WHEN_EMPTY : MODE_STOP_ASAP);
	}

	@Override
	public void run() {
		thread = Thread.currentThread();	// run from thread in executor
		thread.setName(name);
		if (!mode.compareAndSet(MODE_START, MODE_RUNNING)){
			throw new IllegalStateException("Should be in state MODE_START, but actully not.");
		}
		
		int m;	//保证值一致
		while(((m = mode.get()) == MODE_RUNNING) || (m == MODE_STOP_WHEN_EMPTY && queue.size() > 0)){
			consume();
		}
		if (!mode.compareAndSet(MODE_STOP_WHEN_EMPTY, MODE_STOPPED) && !mode.compareAndSet(MODE_STOP_ASAP, MODE_STOPPED)){
			throw new IllegalStateException("Should be in state MODE_STOP_WHEN_EMPTY or MODE_STOP_ASAP, but actully not.");
		}
		thread.interrupt();
	}
	
	/**
	 * Consume the data in queue - this method should be overridden in subclass.<br>
	 * 处理队列中的数据——这个方法应该在子类中被重载。
	 * <p>
	 * This method may be interrupted while running, so please note the following:<br>
	 * 这个方法在运行过程中可能会遇到线程的interrupt，所以如果有以下情况要注意正确处理：
	 * <p>
	 *  If this thread is blocked in an invocation of the wait(), wait(long), or wait(long, int) 
	 *  methods of the Object  class, or of the join(), join(long), join(long, int), sleep(long), 
	 *  or sleep(long, int), methods of this class, then its interrupt status will be cleared and 
	 *  it will receive an InterruptedException.
	 *  <p>
	 *  If this thread is blocked in an I/O operation upon an interruptible channel then the channel 
	 *  will be closed, the thread's interrupt status will be set, and the thread will receive a 
	 *  ClosedByInterruptException.
	 *  <p>
	 *  If this thread is blocked in a Selector then the thread's interrupt status will be set and 
	 *  it will return immediately from the selection operation, possibly with a non-zero value, 
	 *  just as if the selector's wakeup method were invoked. 
	 */
	abstract protected void consume();  
	
}
