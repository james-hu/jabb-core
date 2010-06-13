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

/**
 * 从队列中取数据，进行处理。它所启动的线程是从内部的线程池取的。
 * 处理方式：一个一个取，一个一个处理。 
 * 
 * @author Zhengmao HU (James)
 *
 * @param <E>	队列中元素的类型
 */
abstract public class QueueProcessor<E> extends QueueConsumer<E> {

	/**
	 * 创建一个实例。
	 * @param name			名称，会被用在线程名里
	 * @param workQueue			从这个队列取得待处理数据
	 * @param executorService	指定从这里获得线程
	 */
	public QueueProcessor(BlockingQueue<E> workQueue, String name, ExecutorService executorService){
		super(workQueue, name, executorService);
	}
	
	
	/**
	 * 创建一个实例，使用缺省的线程池。
	 * @param name	名称，会被用在线程名里
	 * @param workQueue	从这个队列取得待处理数据
	 * 
	 */
	public QueueProcessor(BlockingQueue<E> workQueue, String name){
		this(workQueue, name, defaultThreadPool);
	}
	
	/**
	 * 创建一个实例，使用缺省的线程名称和缺省的线程池。
	 * @param workQueue	从这个队列取得待处理数据
	 */
	public QueueProcessor(BlockingQueue<E> workQueue){
		this(workQueue, QueueConsumer.class.getSimpleName());
	}
	

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
	 * 具体的处理方法，一次处理一个对象。
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
	 * @param obj
	 */
	abstract public void process(E obj );


}
