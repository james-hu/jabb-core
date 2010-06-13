/*
Copyright 2010 Zhengmao HU (James)

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
 * 从队列中取数据，进行处理。它所启动的线程是从内部的线程池取的。
 * 
 * @author Zhengmao HU (James)
 * 
 * @param <E>	队列中元素的类型
 *
 */
public abstract class QueueConsumer<E> implements Runnable{
	protected BlockingQueue<E> queue;
	protected Thread thread;
	protected Object startStopLock;
	protected AtomicInteger mode;
	protected String name;

	static protected final int MODE_INIT = 0;
	static protected final int MODE_START = 1;
	static protected final int MODE_RUNNING = 2;
	static protected final int MODE_STOP_ASAP = 3;
	static protected final int MODE_STOP_WHEN_EMPTY = 4;
	static protected final int MODE_STOPPED = 5;
	
	static protected ExecutorService defaultThreadPool;
	
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
	 * 创建一个实例。
	 * @param name			名称，会被用在线程名里
	 * @param workQueue			从这个队列取得待处理数据
	 * @param executorService	指定从这里获得线程
	 */
	public QueueConsumer(BlockingQueue<E> workQueue, String name, ExecutorService executorService){
		threadPool = executorService;
		startStopLock = new Object();
		this.name = name;
		queue = workQueue;
		mode = new AtomicInteger(MODE_INIT);
	}
	
	
	/**
	 * 创建一个实例，使用缺省的线程池。
	 * @param name	名称，会被用在线程名里
	 * @param workQueue	从这个队列取得待处理数据
	 * 
	 */
	public QueueConsumer(BlockingQueue<E> workQueue, String name){
		this(workQueue, name, defaultThreadPool);
	}
	
	/**
	 * 创建一个实例，使用缺省的线程名称和缺省的线程池。
	 * @param workQueue	从这个队列取得待处理数据
	 */
	public QueueConsumer(BlockingQueue<E> workQueue){
		this(workQueue, QueueConsumer.class.getSimpleName());
	}
	
	/**
	 * 把待处理数据放入队列，这个方法会立即返回而不是等待处理完成。
	 * @param obj
	 */
	public void queue(E obj){
		queue.add(obj);
	}
	
	/**
	 * 启动处理线程，这个方法立即返回。
	 */
	public void start(){
		if (mode.compareAndSet(MODE_INIT, MODE_START)){
			threadPool.execute(this);
		}
	}
	
	/**
	 * 等待队列处理空了之后停止处理线程，这个方法会等到处理线程结束才返回。
	 */
	public void stop(){
		stop(true);
	}

	/**
	 * 停止处理线程，这个方法会等到处理线程结束才返回。
	 * @param afterQueueEmpty	如果为true，则等队列处理空了才返回，否则就尽早返回。
	 */
	public void stop(boolean afterQueueEmpty){
		if (mode.compareAndSet(MODE_RUNNING, afterQueueEmpty ? MODE_STOP_WHEN_EMPTY : MODE_STOP_ASAP)){
			while(! mode.compareAndSet(MODE_STOPPED, MODE_INIT)){
				thread.interrupt();
				try {
					// 也存在这种可能性：在while作判断的时候，queue.size()>0，
					// 而take()的时候已经没东西取了，所以join之后也不能死等，要反复interrupt。
					thread.join(1000);
				} catch (InterruptedException e) {
					// do nothing
				}
			}
		}
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
	}
	
	/**
	 * 使用队列中的数据
	 */
	abstract protected void consume();  
	
}
