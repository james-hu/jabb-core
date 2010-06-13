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

import java.util.LinkedHashSet;
import java.util.Set;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * 从队列中取数据，进行处理。它所启动的线程是从内部的线程池取的。
 * 处理方式：尽可能多取，一次处理一批，一批中如有重复的则只处理一个。
 * 在准备待处理的一批数据的时候，会等待一个指定的时间段，如果这个时间段内有更多数据来，则并到当前
 * 这一批里面去，然后再等；如果没有，则把当前这批处理掉。
 * 
 * @author Zhengmao HU (James)
 *
 * @param <E>	队列中元素的类型
 */
abstract public class QueueBatchUniqueProcessor<E> extends QueueConsumer<E> {
	protected int maxBatchSize;
	protected long pollTimeout;
	protected TimeUnit pollTimeoutUnit;

	/**
	 * 创建一个实例，它在“攒数据”的时候等待指定的时长。
	 * @param name			名称，会被用在线程名里
	 * @param workQueue			从这个队列取得待处理数据
	 * @param executorService	指定从这里获得线程
	 * @param batchSize			一批最大包含多少个数据，超过这个数量的就要留到下一批处理
	 * @param batchWaitTimeout	超过这个时段如果没有更多数据则留到下一批处理，0表示不等待
	 * @param timeoutUnit		batchWaitTimeout的单位
	 */
	public QueueBatchUniqueProcessor(BlockingQueue<E> workQueue, String name, ExecutorService executorService,
			int batchSize, long batchWaitTimeout, TimeUnit timeoutUnit){
		super(workQueue, name, executorService);
		maxBatchSize = batchSize;
		pollTimeout = batchWaitTimeout;
		pollTimeoutUnit = timeoutUnit;
	}
	
	
	/**
	 * 创建一个实例，使用缺省的线程池，它在“攒数据”的时候等待指定的时长。
	 * @param name	名称，会被用在线程名里
	 * @param workQueue	从这个队列取得待处理数据
	 * @param batchSize			一批最大包含多少个数据，超过这个数量的就要留到下一批处理
	 * @param batchWaitTimeout	超过这个时段如果没有更多数据则留到下一批处理，0表示不等待
	 * @param timeoutUnit		batchWaitTimeout的单位
	 */
	public QueueBatchUniqueProcessor(BlockingQueue<E> workQueue, String name,
			int batchSize, long batchWaitTimeout, TimeUnit timeoutUnit){
		this(workQueue, name, defaultThreadPool, batchSize, batchWaitTimeout, timeoutUnit);
	}
	
	/**
	 * 创建一个实例，使用缺省的线程名称和缺省的线程池，它在“攒数据”的时候等待指定的时长。
	 * @param workQueue	从这个队列取得待处理数据
	 * @param batchSize			一批最大包含多少个数据，超过这个数量的就要留到下一批处理
	 * @param batchWaitTimeout	超过这个时段如果没有更多数据则留到下一批处理，0表示不等待
	 * @param timeoutUnit		batchWaitTimeout的单位
	 */
	public QueueBatchUniqueProcessor(BlockingQueue<E> workQueue,
			int batchSize, long batchWaitTimeout, TimeUnit timeoutUnit){
		this(workQueue, QueueConsumer.class.getSimpleName(), batchSize, batchWaitTimeout, timeoutUnit);
	}
	
	/**
	 * 创建一个实例，它在“攒数据”的时候不作等待，它在“攒数据”的时候等待指定的时长。
	 * @param name			名称，会被用在线程名里
	 * @param workQueue			从这个队列取得待处理数据
	 * @param executorService	指定从这里获得线程
	 * @param batchSize			一批最大包含多少个数据，超过这个数量的就要留到下一批处理
	 */
	public QueueBatchUniqueProcessor(BlockingQueue<E> workQueue, String name, ExecutorService executorService,
			int batchSize){
		this(workQueue, name, executorService, batchSize, 0, null);
	}
	
	
	/**
	 * 创建一个实例，使用缺省的线程池，它在“攒数据”的时候不作等待。
	 * @param name	名称，会被用在线程名里
	 * @param workQueue	从这个队列取得待处理数据
	 * @param batchSize			一批最大包含多少个数据，超过这个数量的就要留到下一批处理
	 */
	public QueueBatchUniqueProcessor(BlockingQueue<E> workQueue, String name,
			int batchSize){
		this(workQueue, name, defaultThreadPool, batchSize, 0, null);
	}
	
	/**
	 * 创建一个实例，使用缺省的线程名称和缺省的线程池，它在“攒数据”的时候不作等待。
	 * @param workQueue	从这个队列取得待处理数据
	 * @param batchSize			一批最大包含多少个数据，超过这个数量的就要留到下一批处理
	 */
	public QueueBatchUniqueProcessor(BlockingQueue<E> workQueue,
			int batchSize){
		this(workQueue, QueueConsumer.class.getSimpleName(), batchSize, 0, null);
	}
	

	@Override
	protected void consume() {
		int size = 0;
		LinkedHashSet<E> batch = new LinkedHashSet<E>();
		E obj = null;
		try {
			obj = queue.take();
		} catch (InterruptedException e) {
			return;
		}
		batch.add(obj);
		size++;
		if (pollTimeout == 0){
			// no waiting
			while(size < maxBatchSize && (obj = queue.poll()) != null){
				batch.add(obj);
				size++;
			}
		}else{
			// need to wait for a while
			try {
				while(size < maxBatchSize && (obj = queue.poll(pollTimeout, pollTimeoutUnit)) != null){
					batch.add(obj);
					size++;
				}
			} catch (InterruptedException e) {
				// do nothing because we need to have the batch processed;
			}
		}
		process(batch);
	}

	/**
	 * 具体的处理方法，一次处理一批对象，这一批对象的次序与它们进入队列的次序相同。
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
	 * @param batch	一批待处理对象，它们的次序与它们进入队列的次序相同
	 */
	abstract public void process(Set<E> batch);


}
