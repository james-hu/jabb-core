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
 * �Ӷ�����ȡ���ݣ����д��������������߳��Ǵ��ڲ����̳߳�ȡ�ġ�
 * ����ʽ�������ܶ�ȡ��һ�δ���һ����һ���������ظ�����ֻ����һ����
 * ��׼���������һ�����ݵ�ʱ�򣬻�ȴ�һ��ָ����ʱ��Σ�������ʱ������и������������򲢵���ǰ
 * ��һ������ȥ��Ȼ���ٵȣ����û�У���ѵ�ǰ�����������
 * 
 * @author Zhengmao HU (James)
 *
 * @param <E>	������Ԫ�ص�����
 */
abstract public class QueueBatchUniqueProcessor<E> extends QueueConsumer<E> {
	protected int maxBatchSize;
	protected long pollTimeout;
	protected TimeUnit pollTimeoutUnit;

	/**
	 * ����һ��ʵ�������ڡ������ݡ���ʱ��ȴ�ָ����ʱ����
	 * @param name			���ƣ��ᱻ�����߳�����
	 * @param workQueue			���������ȡ�ô���������
	 * @param executorService	ָ�����������߳�
	 * @param batchSize			һ�����������ٸ����ݣ�������������ľ�Ҫ������һ������
	 * @param batchWaitTimeout	�������ʱ�����û�и���������������һ������0��ʾ���ȴ�
	 * @param timeoutUnit		batchWaitTimeout�ĵ�λ
	 */
	public QueueBatchUniqueProcessor(BlockingQueue<E> workQueue, String name, ExecutorService executorService,
			int batchSize, long batchWaitTimeout, TimeUnit timeoutUnit){
		super(workQueue, name, executorService);
		maxBatchSize = batchSize;
		pollTimeout = batchWaitTimeout;
		pollTimeoutUnit = timeoutUnit;
	}
	
	
	/**
	 * ����һ��ʵ����ʹ��ȱʡ���̳߳أ����ڡ������ݡ���ʱ��ȴ�ָ����ʱ����
	 * @param name	���ƣ��ᱻ�����߳�����
	 * @param workQueue	���������ȡ�ô���������
	 * @param batchSize			һ�����������ٸ����ݣ�������������ľ�Ҫ������һ������
	 * @param batchWaitTimeout	�������ʱ�����û�и���������������һ������0��ʾ���ȴ�
	 * @param timeoutUnit		batchWaitTimeout�ĵ�λ
	 */
	public QueueBatchUniqueProcessor(BlockingQueue<E> workQueue, String name,
			int batchSize, long batchWaitTimeout, TimeUnit timeoutUnit){
		this(workQueue, name, defaultThreadPool, batchSize, batchWaitTimeout, timeoutUnit);
	}
	
	/**
	 * ����һ��ʵ����ʹ��ȱʡ���߳����ƺ�ȱʡ���̳߳أ����ڡ������ݡ���ʱ��ȴ�ָ����ʱ����
	 * @param workQueue	���������ȡ�ô���������
	 * @param batchSize			һ�����������ٸ����ݣ�������������ľ�Ҫ������һ������
	 * @param batchWaitTimeout	�������ʱ�����û�и���������������һ������0��ʾ���ȴ�
	 * @param timeoutUnit		batchWaitTimeout�ĵ�λ
	 */
	public QueueBatchUniqueProcessor(BlockingQueue<E> workQueue,
			int batchSize, long batchWaitTimeout, TimeUnit timeoutUnit){
		this(workQueue, QueueConsumer.class.getSimpleName(), batchSize, batchWaitTimeout, timeoutUnit);
	}
	
	/**
	 * ����һ��ʵ�������ڡ������ݡ���ʱ�����ȴ������ڡ������ݡ���ʱ��ȴ�ָ����ʱ����
	 * @param name			���ƣ��ᱻ�����߳�����
	 * @param workQueue			���������ȡ�ô���������
	 * @param executorService	ָ�����������߳�
	 * @param batchSize			һ�����������ٸ����ݣ�������������ľ�Ҫ������һ������
	 */
	public QueueBatchUniqueProcessor(BlockingQueue<E> workQueue, String name, ExecutorService executorService,
			int batchSize){
		this(workQueue, name, executorService, batchSize, 0, null);
	}
	
	
	/**
	 * ����һ��ʵ����ʹ��ȱʡ���̳߳أ����ڡ������ݡ���ʱ�����ȴ���
	 * @param name	���ƣ��ᱻ�����߳�����
	 * @param workQueue	���������ȡ�ô���������
	 * @param batchSize			һ�����������ٸ����ݣ�������������ľ�Ҫ������һ������
	 */
	public QueueBatchUniqueProcessor(BlockingQueue<E> workQueue, String name,
			int batchSize){
		this(workQueue, name, defaultThreadPool, batchSize, 0, null);
	}
	
	/**
	 * ����һ��ʵ����ʹ��ȱʡ���߳����ƺ�ȱʡ���̳߳أ����ڡ������ݡ���ʱ�����ȴ���
	 * @param workQueue	���������ȡ�ô���������
	 * @param batchSize			һ�����������ٸ����ݣ�������������ľ�Ҫ������һ������
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
	 * ����Ĵ�������һ�δ���һ��������һ������Ĵ��������ǽ�����еĴ�����ͬ��
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
	 * @param batch	һ��������������ǵĴ��������ǽ�����еĴ�����ͬ
	 */
	abstract public void process(Set<E> batch);


}
