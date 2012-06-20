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
 * һ���Ӷ��������ȡ�����ݽ��д����ģ�壬����һ��һ����ȡ�߲�����
 * <p>
 * One working thread will be created for each instance of this class when necessary.
 * <p>
 * �����ÿ��ʵ����Ӧ�Ļ���һ�������߳�����Ҫ��ʱ�򱻴�����
 * 
 * @author Zhengmao HU (James)
 *
 * @param <E>	Type of the data in the queue.<br>���������ݵ�����
 */
abstract public class QueueProcessor<E> extends QueueConsumer<E> {

	/**
	 * Constructor to create an instance.<br>
	 * ����һ��ʵ����
	 * 
	 * @param name			Name of this instance, which determines the naming of working thread.<br>
	 * 						����ʵ�������ƣ��ᱻ���ڹ����߳����
	 * @param workQueue			The queue that data for processing will be fetched from.<br>
	 * 							��ʵ�������������ȡ�ô��������ݡ�
	 * @param executorService	Thread pool that working thread will be get from.<br>
	 * 							ָ���ñ�ʵ���������ù����̡߳�
	 */
	public QueueProcessor(BlockingQueue<E> workQueue, String name, ExecutorService executorService){
		super(workQueue, name, executorService);
	}
	
	/**
	 * Constructor to create an instance.<br>
	 * ����һ��ʵ����
	 * 
	 * @param name			Name of this instance, which determines the naming of working thread.<br>
	 * 						����ʵ�������ƣ��ᱻ���ڹ����߳����
	 * @param executorService	Thread pool that working thread will be get from.<br>
	 * 							ָ���ñ�ʵ���������ù����̡߳�
	 */
	public QueueProcessor(String name, ExecutorService executorService){
		super(null, name, executorService);
	}
	
	/**
	 * Constructor to create an instance using default thread pool.<br>
	 * ����һ��ʹ��ȱʡ�̳߳ص�ʵ����
	 * 
	 * @param name			Name of this instance, which determines the naming of working thread.<br>
	 * 						����ʵ�������ƣ��ᱻ���ڹ����߳����
	 * @param workQueue			The queue that data for processing will be fetched from.<br>
	 * 							��ʵ�������������ȡ�ô��������ݡ�
	 */
	public QueueProcessor(BlockingQueue<E> workQueue, String name){
		this(workQueue, name, defaultThreadPool);
	}
	
	/**
	 * Constructor to create an instance using default thread pool.<br>
	 * ����һ��ʹ��ȱʡ�̳߳ص�ʵ����
	 * 
	 * @param name			Name of this instance, which determines the naming of working thread.<br>
	 * 						����ʵ�������ƣ��ᱻ���ڹ����߳����
	 */
	public QueueProcessor(String name){
		this(null, name, defaultThreadPool);
	}

	/**
	 * Constructor to create an instance with default name and using default thread pool.<br>
	 * ����һ��ʵ����ʹ��ȱʡ�����ƺ�ȱʡ���̳߳ء�
	 * 
	 * @param workQueue			The queue that data for processing will be fetched from.<br>
	 * 							��ʵ�������������ȡ�ô��������ݡ�
	 */
	public QueueProcessor(BlockingQueue<E> workQueue){
		this(workQueue, QueueConsumer.class.getSimpleName());
	}
	
	/**
	 * Constructor to create an instance with default name and using default thread pool.<br>
	 * ����һ��ʵ����ʹ��ȱʡ�����ƺ�ȱʡ���̳߳ء�
	 */
	public QueueProcessor(){
		this(null, QueueConsumer.class.getSimpleName());
	}

	/**
	 * This method is overridden over parent class so that one piece of data is taken 
	 * from the queue and {@link #process(Object)} is invoked.<br>
	 * ��������������ˣ��Ӷ������е�һ�����ݻᱻȡ��������{@link #process(Object)}������
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
	 * @param obj	The data taken from queue, which needs to be processed<br>
	 * 				�Ӷ�����ȡ���Ĵ��������ݡ�
	 */
	abstract public void process(E obj );


}
