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

import java.util.Collection;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;

import net.sf.jabb.util.text.NameDeduplicator;

/**
 * A group of QueueConsumer(s) that work on on the same queue simultaneously.<br>
 * 一批并行处理同一个队列的QueueConsumer。
 * <p>
 * One working thread will be created for each QueueConsumer when necessary.
 * <p>
 * 每个QueueConsumer相应的会有一个工作线程在需要的时候被创建。
 * 
 * @author Zhengmao HU (James)
 * 
 * @param <E>	Type of the data in the queue.<br>队列中数据的类型
 * 
 */
public class QueueConsumerGroup<E> {
	protected BlockingQueue<E> queue;
	protected Map<String, QueueConsumer<E>> consumers;
	protected ExecutorService threadPool;
	
	/**
	 * Internal constructor, without specifying thread pool.<br>
	 * （内部用）创建实例，不指定统一的线程池。
	 * 
	 * @param workQueue			The queue that data for processing will be fetched from.<br>
	 * 							本实例将从这个队列取得待处理数据。
	 */
	protected QueueConsumerGroup(BlockingQueue<E> workQueue){
		queue = workQueue;
		consumers = new TreeMap<String, QueueConsumer<E>>();
	}
	
	/**
	 * Internal constructor, without specifying thread pool.<br>
	 * （内部用）创建实例，不指定统一的线程池。
	 * 
	 * @param workQueueSize		Size of the ArrayBlockingQueue to be created from which data for processing will be fetched.<br>
	 * 							将被创建的ArrayBlockingQueue队列的大小，本实例将从这个队列取得待处理数据。
	 */
	protected QueueConsumerGroup(int workQueueSize){
		this(new ArrayBlockingQueue<E>(workQueueSize));
	}

	/**
	 * Internal constructor, specifying one thread pool for all QueueConsumers to use.<br>
	 * （内部用）创建实例，让所有的QueueConsumer统一使用指定的线程池。
	 * @param workQueue			The queue that data for processing will be fetched from.<br>
	 * 							本实例将从这个队列取得待处理数据。
	 * @param executorService	Thread pool that working threads will be get from.<br>
	 * 							指定让本实例从这里获得所有工作线程。
	 */
	protected QueueConsumerGroup(BlockingQueue<E> workQueue, ExecutorService executorService){
		this(workQueue);
		threadPool = executorService;
	}
	
	/**
	 * Internal constructor, specifying one thread pool for all QueueConsumers to use.<br>
	 * （内部用）创建实例，让所有的QueueConsumer统一使用指定的线程池。
	 * @param workQueueSize		Size of the ArrayBlockingQueue to be created from which data for processing will be fetched.<br>
	 * 							将被创建的ArrayBlockingQueue队列的大小，本实例将从这个队列取得待处理数据。
	 * @param executorService	Thread pool that working threads will be get from.<br>
	 * 							指定让本实例从这里获得所有工作线程。
	 */
	protected QueueConsumerGroup(int workQueueSize, ExecutorService executorService){
		this(new ArrayBlockingQueue<E>(workQueueSize), executorService);
	}
	
	/**
	 * Constructor, specifying one thread pool for all QueueConsumers to use.<br>
	 * 创建实例，让所有的QueueConsumer统一使用指定的线程池。
	 * <p>
	 * Duplicated names of QueueConsumer(s) will be renamed automatically when adding to this QueueConsumerGroup.
	 * <p>
	 * 当被加入这个QueueConsumerGroup的时候，QueueConsumer如果有名称重复，会被自动改名。
	 * 
	 * @param workQueue			The queue that data for processing will be fetched from.<br>
	 * 							本实例将从这个队列取得待处理数据。
	 * @param executorService	Thread pool that working threads will be get from.<br>
	 * 							指定让本实例从这里获得所有工作线程。
	 * @param queueConsumers	QueueConsumer(s) that will work together.<br>
	 * 							会一起工作的QueueConsumer。
	 */
	public QueueConsumerGroup(BlockingQueue<E> workQueue, ExecutorService executorService, QueueConsumer<E>... queueConsumers){
		this(workQueue, executorService);
		NameDeduplicator ndd = new NameDeduplicator();
		for (QueueConsumer<E> c: queueConsumers){
			String newName = ndd.deduplicate(c.getName());
			c.setName(newName);
			if (threadPool != null){
				c.setExecutorService(threadPool);
			}
			c.setQueue(queue);
			consumers.put(newName, c);
		}
	}
	
	/**
	 * Constructor, specifying one thread pool for all QueueConsumers to use.<br>
	 * 创建实例，让所有的QueueConsumer统一使用指定的线程池。
	 * <p>
	 * Duplicated names of QueueConsumer(s) will be renamed automatically when adding to this QueueConsumerGroup.
	 * <p>
	 * 当被加入这个QueueConsumerGroup的时候，QueueConsumer如果有名称重复，会被自动改名。
	 * 
	 * @param workQueueSize		Size of the ArrayBlockingQueue to be created from which data for processing will be fetched.<br>
	 * 							将被创建的ArrayBlockingQueue队列的大小，本实例将从这个队列取得待处理数据。
	 * @param executorService	Thread pool that working threads will be get from.<br>
	 * 							指定让本实例从这里获得所有工作线程。
	 * @param queueConsumers	QueueConsumer(s) that will work together.<br>
	 * 							会一起工作的QueueConsumer。
	 */
	public QueueConsumerGroup(int workQueueSize, ExecutorService executorService, QueueConsumer<E>... queueConsumers){
		this(new ArrayBlockingQueue<E>(workQueueSize), executorService, queueConsumers);
	}
	
	/**
	 * Constructor, without specifying thread pool.<br>
	 * 创建实例，不指定统一的线程池。
	 * <p>
	 * Duplicated names of QueueConsumer(s) will be renamed automatically when adding to this QueueConsumerGroup.
	 * <p>
	 * 当被加入这个QueueConsumerGroup的时候，QueueConsumer如果有名称重复，会被自动改名。
	 * 
	 * @param workQueue			The queue that data for processing will be fetched from.<br>
	 * 							本实例将从这个队列取得待处理数据。
	 * @param queueConsumers	QueueConsumer(s) that will work together.<br>
	 * 							会一起工作的QueueConsumer。
	 */
	public QueueConsumerGroup(BlockingQueue<E> workQueue, QueueConsumer<E>... queueConsumers){
		this(workQueue, null, queueConsumers);
	}
	
	/**
	 * Constructor, without specifying thread pool.<br>
	 * 创建实例，不指定统一的线程池。
	 * <p>
	 * Duplicated names of QueueConsumer(s) will be renamed automatically when adding to this QueueConsumerGroup.
	 * <p>
	 * 当被加入这个QueueConsumerGroup的时候，QueueConsumer如果有名称重复，会被自动改名。
	 * 
	 * @param workQueueSize		Size of the ArrayBlockingQueue to be created from which data for processing will be fetched.<br>
	 * 							将被创建的ArrayBlockingQueue队列的大小，本实例将从这个队列取得待处理数据。
	 * @param queueConsumers	QueueConsumer(s) that will work together.<br>
	 * 							会一起工作的QueueConsumer。
	 */
	public QueueConsumerGroup(int workQueueSize, QueueConsumer<E>... queueConsumers){
		this(workQueueSize, null, queueConsumers);
	}
	

	/**
	 * Constructor, specifying one thread pool for all QueueConsumers to use.<br>
	 * 创建实例，让所有的QueueConsumer统一使用指定的线程池。
	 * <p>
	 * Duplicated names of QueueConsumer(s) will be renamed automatically when adding to this QueueConsumerGroup.
	 * <p>
	 * 当被加入这个QueueConsumerGroup的时候，QueueConsumer如果有名称重复，会被自动改名。
	 * 
	 * @param workQueue			The queue that data for processing will be fetched from.<br>
	 * 							本实例将从这个队列取得待处理数据。
	 * @param executorService	Thread pool that working threads will be get from.<br>
	 * 							指定让本实例从这里获得所有工作线程。
	 * @param queueConsumers	QueueConsumer(s) that will work together.<br>
	 * 							会一起工作的QueueConsumer。
	 */
	public QueueConsumerGroup(BlockingQueue<E> workQueue, ExecutorService executorService, Collection<? extends QueueConsumer<E>> queueConsumers){
		this(workQueue, executorService);
		NameDeduplicator ndd = new NameDeduplicator();
		for (QueueConsumer<E> c: queueConsumers){
			String newName = ndd.deduplicate(c.getName());
			c.setName(newName);
			if (threadPool != null){
				c.setExecutorService(threadPool);
			}
			c.setQueue(queue);
			consumers.put(newName, c);
		}
	}
	
	/**
	 * Constructor, specifying one thread pool for all QueueConsumers to use.<br>
	 * 创建实例，让所有的QueueConsumer统一使用指定的线程池。
	 * <p>
	 * Duplicated names of QueueConsumer(s) will be renamed automatically when adding to this QueueConsumerGroup.
	 * <p>
	 * 当被加入这个QueueConsumerGroup的时候，QueueConsumer如果有名称重复，会被自动改名。
	 * 
	 * @param workQueueSize		Size of the ArrayBlockingQueue to be created from which data for processing will be fetched.<br>
	 * 							将被创建的ArrayBlockingQueue队列的大小，本实例将从这个队列取得待处理数据。
	 * @param executorService	Thread pool that working threads will be get from.<br>
	 * 							指定让本实例从这里获得所有工作线程。
	 * @param queueConsumers	QueueConsumer(s) that will work together.<br>
	 * 							会一起工作的QueueConsumer。
	 */
	public QueueConsumerGroup(int workQueueSize, ExecutorService executorService, Collection<? extends QueueConsumer<E>> queueConsumers){
		this(new ArrayBlockingQueue<E>(workQueueSize), executorService, queueConsumers);
	}
	/**
	 * Constructor, without specifying thread pool.<br>
	 * 创建实例，不指定统一的线程池。
	 * <p>
	 * Duplicated names of QueueConsumer(s) will be renamed automatically when adding to this QueueConsumerGroup.
	 * <p>
	 * 当被加入这个QueueConsumerGroup的时候，QueueConsumer如果有名称重复，会被自动改名。
	 * 
	 * @param workQueue			The queue that data for processing will be fetched from.<br>
	 * 							本实例将从这个队列取得待处理数据。
	 * @param queueConsumers	QueueConsumer(s) that will work together.<br>
	 * 							会一起工作的QueueConsumer。
	 */
	public QueueConsumerGroup(BlockingQueue<E> workQueue, Collection<? extends QueueConsumer<E>> queueConsumers){
		this(workQueue, null, queueConsumers);
	}	

	/**
	 * Constructor, without specifying thread pool.<br>
	 * 创建实例，不指定统一的线程池。
	 * <p>
	 * Duplicated names of QueueConsumer(s) will be renamed automatically when adding to this QueueConsumerGroup.
	 * <p>
	 * 当被加入这个QueueConsumerGroup的时候，QueueConsumer如果有名称重复，会被自动改名。
	 * 
	 * @param workQueueSize		Size of the ArrayBlockingQueue to be created from which data for processing will be fetched.<br>
	 * 							将被创建的ArrayBlockingQueue队列的大小，本实例将从这个队列取得待处理数据。
	 * @param queueConsumers	QueueConsumer(s) that will work together.<br>
	 * 							会一起工作的QueueConsumer。
	 */
	public QueueConsumerGroup(int workQueueSize, Collection<? extends QueueConsumer<E>> queueConsumers){
		this(workQueueSize, null, queueConsumers);
	}	

	/**
	 * Get QueueConsumer instance by its name.<br>
	 * 按名称寻找得到QueueConsumer。
	 * 
	 * @param name 	Name of the QueueConsumer
	 * @return		The instance with the name specified
	 */
	public QueueConsumer<E> getConsumer(String name){
		return consumers.get(name);
	}
	
	/**
	 * Get the Map of all QueueConsumer.<br>
	 * 获得含有全部QueueConsumer的Map。
	 * 
	 * @return	A Map, its key is the name of QueueConsumer, its value is QueueConsumer itself.<br>
	 * 			一个Map，其key是QueueConsumer的名称，值是QueueConsumer本身。
	 */
	public Map<String, QueueConsumer<E>> getConsumers() {
		return consumers;
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
	 * Start all QueueConsumer(s) one by one.<br>
	 * 逐个启动所有Consumer。
	 */
	public void start(){
		for (QueueConsumer<E> c: consumers.values()){
			c.start();
		}
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
	 * Stop all the working threads one by one; 
	 * This method will not return until all threads are stopped.<br>
	 * 逐个停止所工作线程，这个方法会等到所有工作线程结束才返回。
	 * 
	 * @param afterQueueEmpty	true if working thread should keep processing until the queue is empty;<br>
	 * 							false if working thread should stop after finished current work;<br>
	 * 							如果为true，则工作线程要等到队列处理空了才结束；<br>
	 * 							如果为false，则工作线程处理完当前数据就结束。
	 */
	public void stop(boolean afterQueueEmpty){
		for (QueueConsumer<E> c: consumers.values()){
			c.preStop(afterQueueEmpty);
		}
		for (QueueConsumer<E> c: consumers.values()){
			c.stop(afterQueueEmpty);
		}
	}
	
	/**
	 * Stop working threads after the queue is empty; 
	 * This method will not return until working thread finishes.<br>
	 * 让所有处理线程在队列处理空了之后停止，这个方法会等到所有工作处理线程结束才返回。
	 */
	public void stop(){
		stop(true);
	}

}
