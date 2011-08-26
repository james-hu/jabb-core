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
 * A template for processing data in batch from a queue.<br>
 * 一个从队列中取得数据并批量处理的模板，数据一批批被取走并处理。
 * <p>
 * One working thread will be created for each instance of this class when necessary.
 * <p>
 * 本类的每个实例相应的会有一个工作线程在需要的时候被创建。
 * <p>
 * For each batch, data will be taken from the queue as much as possible.
 * Maximum size of a batch and maximum time for waiting for new data can be configured.
 * If the maximum size limit reached, or maximum wait time limit reached, all
 * data in current batch will be processed, and further data taken will be put
 * into later batches.
 * Duplicated data will be discarded in a batch, which means, if there are duplicated data
 * taken from the queue in a batch, only one instance of those duplicated will be 
 * processed.
 * <p>
 * 每批会尽可能地多取一些数据，可以设定每批最大的数据量，以及最长的等待时间。
 * 如果达到了这个量，或者是达到了这个时间，则当前批的数据就处理掉，然后开始下一批。
 * 每批数据中如果有重复的，会被剔除掉，也就是说，在一批当中不会重复处理。
 * 
 * @author Zhengmao HU (James)
 *
 * @param <E>	Type of the data in the queue.<br>队列中数据的类型
 */
abstract public class QueueBatchUniqueProcessor<E> extends QueueConsumer<E> {
	protected int maxBatchSize;
	protected long pollTimeout;
	protected TimeUnit pollTimeoutUnit;

	/**
	 * Constructor to create an instance.<br>
	 * 创建一个实例。
	 * 
	 * @param name				Name of this instance, which determines the naming of working thread.<br>
	 * 							本个实例的名称，会被用在工作线程名里。
	 * @param workQueue			The queue that data for processing will be fetched from.<br>
	 * 							本实例将从这个队列取得待处理数据。
	 * @param executorService	Thread pool that working thread will be get from.<br>
	 * 							指定让本实例从这里获得工作线程。
	 * @param batchSize			Maximum size allowed for a batch, remaining data will be put into later batches.<br>
	 * 							一批最大包含多少个数据，超过这个数量的就要留到下一批处理
	 * @param batchWaitTimeout	Maximum time period allowed for waiting for new data from the queue 
	 * 							before current batch is processed, 0 means no waiting.<br>
	 * 							超过这个时段如果没有更多数据则留到下一批处理，0表示不等待。
	 * @param timeoutUnit		Unit of the batchWaitTimeout parameter.<br>		
	 * 							batchWaitTimeout的单位
	 */
	public QueueBatchUniqueProcessor(BlockingQueue<E> workQueue, String name, ExecutorService executorService,
			int batchSize, long batchWaitTimeout, TimeUnit timeoutUnit){
		super(workQueue, name, executorService);
		maxBatchSize = batchSize;
		pollTimeout = batchWaitTimeout;
		pollTimeoutUnit = timeoutUnit;
	}
	
	
	/**
	 * Constructor to create an instance using default thread pool.<br>
	 * 创建一个使用缺省线程池的实例。
	 * 
	 * @param name				Name of this instance, which determines the naming of working thread.<br>
	 * 							本个实例的名称，会被用在工作线程名里。
	 * @param workQueue			The queue that data for processing will be fetched from.<br>
	 * 							本实例将从这个队列取得待处理数据。
	 * @param batchSize			Maximum size allowed for a batch, remaining data will be put into later batches.<br>
	 * 							一批最大包含多少个数据，超过这个数量的就要留到下一批处理
	 * @param batchWaitTimeout	Maximum time period allowed for waiting for new data from the queue 
	 * 							before current batch is processed, 0 means no waiting.<br>
	 * 							超过这个时段如果没有更多数据则留到下一批处理，0表示不等待。
	 * @param timeoutUnit		Unit of the batchWaitTimeout parameter.<br>		
	 * 							batchWaitTimeout的单位
	 */
	public QueueBatchUniqueProcessor(BlockingQueue<E> workQueue, String name,
			int batchSize, long batchWaitTimeout, TimeUnit timeoutUnit){
		this(workQueue, name, defaultThreadPool, batchSize, batchWaitTimeout, timeoutUnit);
	}
	
	/**
	 * Constructor to create an instance with default name: QueueConsumer.class.getSimpleName()<br>
	 * 创建一个实例，其名称使用缺省名称：QueueConsumer.class.getSimpleName()。
	 * 
	 * @param workQueue			The queue that data for processing will be fetched from.<br>
	 * 							本实例将从这个队列取得待处理数据。
	 * @param batchSize			Maximum size allowed for a batch, remaining data will be put into later batches.<br>
	 * 							一批最大包含多少个数据，超过这个数量的就要留到下一批处理
	 * @param batchWaitTimeout	Maximum time period allowed for waiting for new data from the queue 
	 * 							before current batch is processed, 0 means no waiting.<br>
	 * 							超过这个时段如果没有更多数据则留到下一批处理，0表示不等待。
	 * @param timeoutUnit		Unit of the batchWaitTimeout parameter.<br>		
	 * 							batchWaitTimeout的单位
	 */
	public QueueBatchUniqueProcessor(BlockingQueue<E> workQueue,
			int batchSize, long batchWaitTimeout, TimeUnit timeoutUnit){
		this(workQueue, QueueConsumer.class.getSimpleName(), batchSize, batchWaitTimeout, timeoutUnit);
	}
	
	/**
	 * Constructor to create an instance that do not wait for new data.<br>
	 * 创建一个不等待新数据到来的实例。
	 * 
	 * @param name				Name of this instance, which determines the naming of working thread.<br>
	 * 							本个实例的名称，会被用在工作线程名里。
	 * @param workQueue			The queue that data for processing will be fetched from.<br>
	 * 							本实例将从这个队列取得待处理数据。
	 * @param executorService	Thread pool that working thread will be get from.<br>
	 * 							指定让本实例从这里获得工作线程。
	 * @param batchSize			Maximum size allowed for a batch, remaining data will be put into later batches.<br>
	 * 							一批最大包含多少个数据，超过这个数量的就要留到下一批处理
	 */
	public QueueBatchUniqueProcessor(BlockingQueue<E> workQueue, String name, ExecutorService executorService,
			int batchSize){
		this(workQueue, name, executorService, batchSize, 0, null);
	}
	
	
	/**
	 * Constructor to create an instance that uses default thread pool and do not wait for new data.<br>
	 * 创建一个使用缺省线程池且不等待新数据到来的实例。
	 * 
	 * @param name				Name of this instance, which determines the naming of working thread.<br>
	 * 							本个实例的名称，会被用在工作线程名里。
	 * @param workQueue			The queue that data for processing will be fetched from.<br>
	 * 							本实例将从这个队列取得待处理数据。
	 * @param batchSize			Maximum size allowed for a batch, remaining data will be put into later batches.<br>
	 * 							一批最大包含多少个数据，超过这个数量的就要留到下一批处理
	 */
	public QueueBatchUniqueProcessor(BlockingQueue<E> workQueue, String name,
			int batchSize){
		this(workQueue, name, defaultThreadPool, batchSize, 0, null);
	}
	
	/**
	 * Constructor to create an instance that uses default name - QueueConsumer.class.getSimpleName(), 
	 * default thread pool and do not wait for new data.<br>
	 * 创建一个使用缺省名称（QueueConsumer.class.getSimpleName()）、缺省线程池且不等待新数据到来的实例。
	 * 
	 * @param workQueue			The queue that data for processing will be fetched from.<br>
	 * 							本实例将从这个队列取得待处理数据。
	 * @param batchSize			Maximum size allowed for a batch, remaining data will be put into later batches.<br>
	 * 							一批最大包含多少个数据，超过这个数量的就要留到下一批处理
	 */
	public QueueBatchUniqueProcessor(BlockingQueue<E> workQueue,
			int batchSize){
		this(workQueue, QueueConsumer.class.getSimpleName(), batchSize, 0, null);
	}
	

	/**
	 * This method is overridden over parent class so that a batch of data is taken 
	 * from the queue and {@link #process(Set)} is invoked.<br>
	 * 这个方法被重载了，从而队列中的一批数据会被取出并调用{@link #process(Set)}方法。
	 */
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
	 * @param batch	The data taken from queue, which needs to be processed<br>
	 * 				从队列中取出的待处理数据。
	 */
	abstract public void process(Set<E> batch);


}
