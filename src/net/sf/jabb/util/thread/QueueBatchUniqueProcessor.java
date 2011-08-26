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
 * һ���Ӷ�����ȡ�����ݲ����������ģ�壬����һ������ȡ�߲�����
 * <p>
 * One working thread will be created for each instance of this class when necessary.
 * <p>
 * �����ÿ��ʵ����Ӧ�Ļ���һ�������߳�����Ҫ��ʱ�򱻴�����
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
 * ÿ���ᾡ���ܵض�ȡһЩ���ݣ������趨ÿ���������������Լ���ĵȴ�ʱ�䡣
 * ����ﵽ��������������Ǵﵽ�����ʱ�䣬��ǰ�������ݾʹ������Ȼ��ʼ��һ����
 * ÿ��������������ظ��ģ��ᱻ�޳�����Ҳ����˵����һ�����в����ظ�����
 * 
 * @author Zhengmao HU (James)
 *
 * @param <E>	Type of the data in the queue.<br>���������ݵ�����
 */
abstract public class QueueBatchUniqueProcessor<E> extends QueueConsumer<E> {
	protected int maxBatchSize;
	protected long pollTimeout;
	protected TimeUnit pollTimeoutUnit;

	/**
	 * Constructor to create an instance.<br>
	 * ����һ��ʵ����
	 * 
	 * @param name				Name of this instance, which determines the naming of working thread.<br>
	 * 							����ʵ�������ƣ��ᱻ���ڹ����߳����
	 * @param workQueue			The queue that data for processing will be fetched from.<br>
	 * 							��ʵ�������������ȡ�ô��������ݡ�
	 * @param executorService	Thread pool that working thread will be get from.<br>
	 * 							ָ���ñ�ʵ���������ù����̡߳�
	 * @param batchSize			Maximum size allowed for a batch, remaining data will be put into later batches.<br>
	 * 							һ�����������ٸ����ݣ�������������ľ�Ҫ������һ������
	 * @param batchWaitTimeout	Maximum time period allowed for waiting for new data from the queue 
	 * 							before current batch is processed, 0 means no waiting.<br>
	 * 							�������ʱ�����û�и���������������һ������0��ʾ���ȴ���
	 * @param timeoutUnit		Unit of the batchWaitTimeout parameter.<br>		
	 * 							batchWaitTimeout�ĵ�λ
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
	 * ����һ��ʹ��ȱʡ�̳߳ص�ʵ����
	 * 
	 * @param name				Name of this instance, which determines the naming of working thread.<br>
	 * 							����ʵ�������ƣ��ᱻ���ڹ����߳����
	 * @param workQueue			The queue that data for processing will be fetched from.<br>
	 * 							��ʵ�������������ȡ�ô��������ݡ�
	 * @param batchSize			Maximum size allowed for a batch, remaining data will be put into later batches.<br>
	 * 							һ�����������ٸ����ݣ�������������ľ�Ҫ������һ������
	 * @param batchWaitTimeout	Maximum time period allowed for waiting for new data from the queue 
	 * 							before current batch is processed, 0 means no waiting.<br>
	 * 							�������ʱ�����û�и���������������һ������0��ʾ���ȴ���
	 * @param timeoutUnit		Unit of the batchWaitTimeout parameter.<br>		
	 * 							batchWaitTimeout�ĵ�λ
	 */
	public QueueBatchUniqueProcessor(BlockingQueue<E> workQueue, String name,
			int batchSize, long batchWaitTimeout, TimeUnit timeoutUnit){
		this(workQueue, name, defaultThreadPool, batchSize, batchWaitTimeout, timeoutUnit);
	}
	
	/**
	 * Constructor to create an instance with default name: QueueConsumer.class.getSimpleName()<br>
	 * ����һ��ʵ����������ʹ��ȱʡ���ƣ�QueueConsumer.class.getSimpleName()��
	 * 
	 * @param workQueue			The queue that data for processing will be fetched from.<br>
	 * 							��ʵ�������������ȡ�ô��������ݡ�
	 * @param batchSize			Maximum size allowed for a batch, remaining data will be put into later batches.<br>
	 * 							һ�����������ٸ����ݣ�������������ľ�Ҫ������һ������
	 * @param batchWaitTimeout	Maximum time period allowed for waiting for new data from the queue 
	 * 							before current batch is processed, 0 means no waiting.<br>
	 * 							�������ʱ�����û�и���������������һ������0��ʾ���ȴ���
	 * @param timeoutUnit		Unit of the batchWaitTimeout parameter.<br>		
	 * 							batchWaitTimeout�ĵ�λ
	 */
	public QueueBatchUniqueProcessor(BlockingQueue<E> workQueue,
			int batchSize, long batchWaitTimeout, TimeUnit timeoutUnit){
		this(workQueue, QueueConsumer.class.getSimpleName(), batchSize, batchWaitTimeout, timeoutUnit);
	}
	
	/**
	 * Constructor to create an instance that do not wait for new data.<br>
	 * ����һ�����ȴ������ݵ�����ʵ����
	 * 
	 * @param name				Name of this instance, which determines the naming of working thread.<br>
	 * 							����ʵ�������ƣ��ᱻ���ڹ����߳����
	 * @param workQueue			The queue that data for processing will be fetched from.<br>
	 * 							��ʵ�������������ȡ�ô��������ݡ�
	 * @param executorService	Thread pool that working thread will be get from.<br>
	 * 							ָ���ñ�ʵ���������ù����̡߳�
	 * @param batchSize			Maximum size allowed for a batch, remaining data will be put into later batches.<br>
	 * 							һ�����������ٸ����ݣ�������������ľ�Ҫ������һ������
	 */
	public QueueBatchUniqueProcessor(BlockingQueue<E> workQueue, String name, ExecutorService executorService,
			int batchSize){
		this(workQueue, name, executorService, batchSize, 0, null);
	}
	
	
	/**
	 * Constructor to create an instance that uses default thread pool and do not wait for new data.<br>
	 * ����һ��ʹ��ȱʡ�̳߳��Ҳ��ȴ������ݵ�����ʵ����
	 * 
	 * @param name				Name of this instance, which determines the naming of working thread.<br>
	 * 							����ʵ�������ƣ��ᱻ���ڹ����߳����
	 * @param workQueue			The queue that data for processing will be fetched from.<br>
	 * 							��ʵ�������������ȡ�ô��������ݡ�
	 * @param batchSize			Maximum size allowed for a batch, remaining data will be put into later batches.<br>
	 * 							һ�����������ٸ����ݣ�������������ľ�Ҫ������һ������
	 */
	public QueueBatchUniqueProcessor(BlockingQueue<E> workQueue, String name,
			int batchSize){
		this(workQueue, name, defaultThreadPool, batchSize, 0, null);
	}
	
	/**
	 * Constructor to create an instance that uses default name - QueueConsumer.class.getSimpleName(), 
	 * default thread pool and do not wait for new data.<br>
	 * ����һ��ʹ��ȱʡ���ƣ�QueueConsumer.class.getSimpleName()����ȱʡ�̳߳��Ҳ��ȴ������ݵ�����ʵ����
	 * 
	 * @param workQueue			The queue that data for processing will be fetched from.<br>
	 * 							��ʵ�������������ȡ�ô��������ݡ�
	 * @param batchSize			Maximum size allowed for a batch, remaining data will be put into later batches.<br>
	 * 							һ�����������ٸ����ݣ�������������ľ�Ҫ������һ������
	 */
	public QueueBatchUniqueProcessor(BlockingQueue<E> workQueue,
			int batchSize){
		this(workQueue, QueueConsumer.class.getSimpleName(), batchSize, 0, null);
	}
	

	/**
	 * This method is overridden over parent class so that a batch of data is taken 
	 * from the queue and {@link #process(Set)} is invoked.<br>
	 * ��������������ˣ��Ӷ������е�һ�����ݻᱻȡ��������{@link #process(Set)}������
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
	 * ����һ�����ݡ����������Ӧ���������б����ء�
	 * <p>
	 * This method may be interrupted while running, so please note the following:<br>
	 * ������������й����п��ܻ������̵߳�interrupt������������������Ҫע����ȷ����
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
	 * 				�Ӷ�����ȡ���Ĵ��������ݡ�
	 */
	abstract public void process(Set<E> batch);


}
