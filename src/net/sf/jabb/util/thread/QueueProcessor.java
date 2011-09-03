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

/**
 * A template for processing data one by one from a queue.<br>
 * 一个从队列中逐个取得数据进行处理的模板，数据一个一个被取走并处理。
 * <p>
 * One working thread will be created for each instance of this class when necessary.
 * <p>
 * 本类的每个实例相应的会有一个工作线程在需要的时候被创建。
 * 
 * @author Zhengmao HU (James)
 *
 * @param <E>	Type of the data in the queue.<br>队列中数据的类型
 */
abstract public class QueueProcessor<E> extends QueueConsumer<E> {

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
	public QueueProcessor(BlockingQueue<E> workQueue, String name, ExecutorService executorService){
		super(workQueue, name, executorService);
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
	public QueueProcessor(String name, ExecutorService executorService){
		super(null, name, executorService);
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
	public QueueProcessor(BlockingQueue<E> workQueue, String name){
		this(workQueue, name, defaultThreadPool);
	}
	
	/**
	 * Constructor to create an instance using default thread pool.<br>
	 * 创建一个使用缺省线程池的实例。
	 * 
	 * @param name			Name of this instance, which determines the naming of working thread.<br>
	 * 						本个实例的名称，会被用在工作线程名里。
	 */
	public QueueProcessor(String name){
		this(null, name, defaultThreadPool);
	}

	/**
	 * Constructor to create an instance with default name and using default thread pool.<br>
	 * 创建一个实例，使用缺省的名称和缺省的线程池。
	 * 
	 * @param workQueue			The queue that data for processing will be fetched from.<br>
	 * 							本实例将从这个队列取得待处理数据。
	 */
	public QueueProcessor(BlockingQueue<E> workQueue){
		this(workQueue, QueueConsumer.class.getSimpleName());
	}
	
	/**
	 * Constructor to create an instance with default name and using default thread pool.<br>
	 * 创建一个实例，使用缺省的名称和缺省的线程池。
	 */
	public QueueProcessor(){
		this(null, QueueConsumer.class.getSimpleName());
	}

	/**
	 * This method is overridden over parent class so that one piece of data is taken 
	 * from the queue and {@link #process(Object)} is invoked.<br>
	 * 这个方法被重载了，从而队列中的一份数据会被取出并调用{@link #process(Object)}方法。
	 */
	@Override
	protected void consume() {
		E obj = null;
		try {
			obj = queue.take();
		} catch (InterruptedException e) {
			//thread.isInterrupted();
			return;
		}
		process(obj);
	}

	/**
	 * Process one piece of data - this method should be overridden in subclass.<br>
	 * 处理一份数据——这个方法应该在子类中被重载。
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
	 *  
	 * @param obj	The data taken from queue, which needs to be processed<br>
	 * 				从队列中取出的待处理数据。
	 */
	abstract public void process(E obj );


}
