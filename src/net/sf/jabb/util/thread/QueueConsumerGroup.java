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
 * һ�����д���ͬһ�����е�QueueConsumer��
 * <p>
 * One working thread will be created for each QueueConsumer when necessary.
 * <p>
 * ÿ��QueueConsumer��Ӧ�Ļ���һ�������߳�����Ҫ��ʱ�򱻴�����
 * 
 * @author Zhengmao HU (James)
 * 
 * @param <E>	Type of the data in the queue.<br>���������ݵ�����
 * 
 */
public class QueueConsumerGroup<E> {
	protected BlockingQueue<E> queue;
	protected Map<String, QueueConsumer<E>> consumers;
	protected ExecutorService threadPool;
	
	/**
	 * Internal constructor, without specifying thread pool.<br>
	 * ���ڲ��ã�����ʵ������ָ��ͳһ���̳߳ء�
	 * 
	 * @param workQueue			The queue that data for processing will be fetched from.<br>
	 * 							��ʵ�������������ȡ�ô��������ݡ�
	 */
	protected QueueConsumerGroup(BlockingQueue<E> workQueue){
		queue = workQueue;
		consumers = new TreeMap<String, QueueConsumer<E>>();
	}
	
	/**
	 * Internal constructor, without specifying thread pool.<br>
	 * ���ڲ��ã�����ʵ������ָ��ͳһ���̳߳ء�
	 * 
	 * @param workQueueSize		Size of the ArrayBlockingQueue to be created from which data for processing will be fetched.<br>
	 * 							����������ArrayBlockingQueue���еĴ�С����ʵ�������������ȡ�ô��������ݡ�
	 */
	protected QueueConsumerGroup(int workQueueSize){
		this(new ArrayBlockingQueue<E>(workQueueSize));
	}

	/**
	 * Internal constructor, specifying one thread pool for all QueueConsumers to use.<br>
	 * ���ڲ��ã�����ʵ���������е�QueueConsumerͳһʹ��ָ�����̳߳ء�
	 * @param workQueue			The queue that data for processing will be fetched from.<br>
	 * 							��ʵ�������������ȡ�ô��������ݡ�
	 * @param executorService	Thread pool that working threads will be get from.<br>
	 * 							ָ���ñ�ʵ�������������й����̡߳�
	 */
	protected QueueConsumerGroup(BlockingQueue<E> workQueue, ExecutorService executorService){
		this(workQueue);
		threadPool = executorService;
	}
	
	/**
	 * Internal constructor, specifying one thread pool for all QueueConsumers to use.<br>
	 * ���ڲ��ã�����ʵ���������е�QueueConsumerͳһʹ��ָ�����̳߳ء�
	 * @param workQueueSize		Size of the ArrayBlockingQueue to be created from which data for processing will be fetched.<br>
	 * 							����������ArrayBlockingQueue���еĴ�С����ʵ�������������ȡ�ô��������ݡ�
	 * @param executorService	Thread pool that working threads will be get from.<br>
	 * 							ָ���ñ�ʵ�������������й����̡߳�
	 */
	protected QueueConsumerGroup(int workQueueSize, ExecutorService executorService){
		this(new ArrayBlockingQueue<E>(workQueueSize), executorService);
	}
	
	/**
	 * Constructor, specifying one thread pool for all QueueConsumers to use.<br>
	 * ����ʵ���������е�QueueConsumerͳһʹ��ָ�����̳߳ء�
	 * <p>
	 * Duplicated names of QueueConsumer(s) will be renamed automatically when adding to this QueueConsumerGroup.
	 * <p>
	 * �����������QueueConsumerGroup��ʱ��QueueConsumer����������ظ����ᱻ�Զ�������
	 * 
	 * @param workQueue			The queue that data for processing will be fetched from.<br>
	 * 							��ʵ�������������ȡ�ô��������ݡ�
	 * @param executorService	Thread pool that working threads will be get from.<br>
	 * 							ָ���ñ�ʵ�������������й����̡߳�
	 * @param queueConsumers	QueueConsumer(s) that will work together.<br>
	 * 							��һ������QueueConsumer��
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
	 * ����ʵ���������е�QueueConsumerͳһʹ��ָ�����̳߳ء�
	 * <p>
	 * Duplicated names of QueueConsumer(s) will be renamed automatically when adding to this QueueConsumerGroup.
	 * <p>
	 * �����������QueueConsumerGroup��ʱ��QueueConsumer����������ظ����ᱻ�Զ�������
	 * 
	 * @param workQueueSize		Size of the ArrayBlockingQueue to be created from which data for processing will be fetched.<br>
	 * 							����������ArrayBlockingQueue���еĴ�С����ʵ�������������ȡ�ô��������ݡ�
	 * @param executorService	Thread pool that working threads will be get from.<br>
	 * 							ָ���ñ�ʵ�������������й����̡߳�
	 * @param queueConsumers	QueueConsumer(s) that will work together.<br>
	 * 							��һ������QueueConsumer��
	 */
	public QueueConsumerGroup(int workQueueSize, ExecutorService executorService, QueueConsumer<E>... queueConsumers){
		this(new ArrayBlockingQueue<E>(workQueueSize), executorService, queueConsumers);
	}
	
	/**
	 * Constructor, without specifying thread pool.<br>
	 * ����ʵ������ָ��ͳһ���̳߳ء�
	 * <p>
	 * Duplicated names of QueueConsumer(s) will be renamed automatically when adding to this QueueConsumerGroup.
	 * <p>
	 * �����������QueueConsumerGroup��ʱ��QueueConsumer����������ظ����ᱻ�Զ�������
	 * 
	 * @param workQueue			The queue that data for processing will be fetched from.<br>
	 * 							��ʵ�������������ȡ�ô��������ݡ�
	 * @param queueConsumers	QueueConsumer(s) that will work together.<br>
	 * 							��һ������QueueConsumer��
	 */
	public QueueConsumerGroup(BlockingQueue<E> workQueue, QueueConsumer<E>... queueConsumers){
		this(workQueue, null, queueConsumers);
	}
	
	/**
	 * Constructor, without specifying thread pool.<br>
	 * ����ʵ������ָ��ͳһ���̳߳ء�
	 * <p>
	 * Duplicated names of QueueConsumer(s) will be renamed automatically when adding to this QueueConsumerGroup.
	 * <p>
	 * �����������QueueConsumerGroup��ʱ��QueueConsumer����������ظ����ᱻ�Զ�������
	 * 
	 * @param workQueueSize		Size of the ArrayBlockingQueue to be created from which data for processing will be fetched.<br>
	 * 							����������ArrayBlockingQueue���еĴ�С����ʵ�������������ȡ�ô��������ݡ�
	 * @param queueConsumers	QueueConsumer(s) that will work together.<br>
	 * 							��һ������QueueConsumer��
	 */
	public QueueConsumerGroup(int workQueueSize, QueueConsumer<E>... queueConsumers){
		this(workQueueSize, null, queueConsumers);
	}
	

	/**
	 * Constructor, specifying one thread pool for all QueueConsumers to use.<br>
	 * ����ʵ���������е�QueueConsumerͳһʹ��ָ�����̳߳ء�
	 * <p>
	 * Duplicated names of QueueConsumer(s) will be renamed automatically when adding to this QueueConsumerGroup.
	 * <p>
	 * �����������QueueConsumerGroup��ʱ��QueueConsumer����������ظ����ᱻ�Զ�������
	 * 
	 * @param workQueue			The queue that data for processing will be fetched from.<br>
	 * 							��ʵ�������������ȡ�ô��������ݡ�
	 * @param executorService	Thread pool that working threads will be get from.<br>
	 * 							ָ���ñ�ʵ�������������й����̡߳�
	 * @param queueConsumers	QueueConsumer(s) that will work together.<br>
	 * 							��һ������QueueConsumer��
	 */
	public QueueConsumerGroup(BlockingQueue<E> workQueue, ExecutorService executorService, Collection<QueueConsumer<E>> queueConsumers){
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
	 * ����ʵ���������е�QueueConsumerͳһʹ��ָ�����̳߳ء�
	 * <p>
	 * Duplicated names of QueueConsumer(s) will be renamed automatically when adding to this QueueConsumerGroup.
	 * <p>
	 * �����������QueueConsumerGroup��ʱ��QueueConsumer����������ظ����ᱻ�Զ�������
	 * 
	 * @param workQueueSize		Size of the ArrayBlockingQueue to be created from which data for processing will be fetched.<br>
	 * 							����������ArrayBlockingQueue���еĴ�С����ʵ�������������ȡ�ô��������ݡ�
	 * @param executorService	Thread pool that working threads will be get from.<br>
	 * 							ָ���ñ�ʵ�������������й����̡߳�
	 * @param queueConsumers	QueueConsumer(s) that will work together.<br>
	 * 							��һ������QueueConsumer��
	 */
	public QueueConsumerGroup(int workQueueSize, ExecutorService executorService, Collection<QueueConsumer<E>> queueConsumers){
		this(new ArrayBlockingQueue<E>(workQueueSize), executorService, queueConsumers);
	}
	/**
	 * Constructor, without specifying thread pool.<br>
	 * ����ʵ������ָ��ͳһ���̳߳ء�
	 * <p>
	 * Duplicated names of QueueConsumer(s) will be renamed automatically when adding to this QueueConsumerGroup.
	 * <p>
	 * �����������QueueConsumerGroup��ʱ��QueueConsumer����������ظ����ᱻ�Զ�������
	 * 
	 * @param workQueue			The queue that data for processing will be fetched from.<br>
	 * 							��ʵ�������������ȡ�ô��������ݡ�
	 * @param queueConsumers	QueueConsumer(s) that will work together.<br>
	 * 							��һ������QueueConsumer��
	 */
	public QueueConsumerGroup(BlockingQueue<E> workQueue, Collection<QueueConsumer<E>> queueConsumers){
		this(workQueue, null, queueConsumers);
	}	

	/**
	 * Constructor, without specifying thread pool.<br>
	 * ����ʵ������ָ��ͳһ���̳߳ء�
	 * <p>
	 * Duplicated names of QueueConsumer(s) will be renamed automatically when adding to this QueueConsumerGroup.
	 * <p>
	 * �����������QueueConsumerGroup��ʱ��QueueConsumer����������ظ����ᱻ�Զ�������
	 * 
	 * @param workQueueSize		Size of the ArrayBlockingQueue to be created from which data for processing will be fetched.<br>
	 * 							����������ArrayBlockingQueue���еĴ�С����ʵ�������������ȡ�ô��������ݡ�
	 * @param queueConsumers	QueueConsumer(s) that will work together.<br>
	 * 							��һ������QueueConsumer��
	 */
	public QueueConsumerGroup(int workQueueSize, Collection<QueueConsumer<E>> queueConsumers){
		this(workQueueSize, null, queueConsumers);
	}	

	/**
	 * Get QueueConsumer instance by its name.<br>
	 * ������Ѱ�ҵõ�QueueConsumer��
	 * 
	 * @param name 	Name of the QueueConsumer
	 * @return		The instance with the name specified
	 */
	public QueueConsumer<E> getConsumer(String name){
		return consumers.get(name);
	}
	
	/**
	 * Get the Map of all QueueConsumer.<br>
	 * ��ú���ȫ��QueueConsumer��Map��
	 * 
	 * @return	A Map, its key is the name of QueueConsumer, its value is QueueConsumer itself.<br>
	 * 			һ��Map����key��QueueConsumer�����ƣ�ֵ��QueueConsumer����
	 */
	public Map<String, QueueConsumer<E>> getConsumers() {
		return consumers;
	}

	/**
	 * Get the work queue.<br>
	 * ȡ�ù������С�
	 * @return The work queue.<br>�������С�
	 */
	public BlockingQueue<E> getQueue() {
		return queue;
	}

	/**
	 * Start all QueueConsumer(s) one by one.<br>
	 * �����������Consumer��
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
	 * �Ѵ��������ݷ�����У���������л��п�λ��������������������ض����ǵȴ�ʵ�ʴ�����ɡ�
	 * <p>
	 * If the queue has no space left, this method will wait for the space then put data into the queue for processing,
	 * after that, this method will return immediately without waiting for the data to be actually processed.
	 * <p>
	 * ���������û�п�λ���ˣ����ȴ����пճ�λ����֮���ٰ����ݷŽ�ȥ������֮������������������ض����ǵȴ�ʵ�ʴ�����ɡ�
	 * 
	 * @param obj	Data need to be processed<br>
	 * 				����������ݡ�
	 * @throws InterruptedException if interrupted while waiting for space to become available.<br>
	 * 								��������������ڵȴ��ճ�λ�õ�ʱ�������жϡ�
	 */
	public void queue(E obj) throws InterruptedException{
		queue.put(obj);
	}
	
	/**
	 * Stop all the working threads one by one; 
	 * This method will not return until all threads are stopped.<br>
	 * ���ֹͣ�������̣߳����������ȵ����й����߳̽����ŷ��ء�
	 * 
	 * @param afterQueueEmpty	true if working thread should keep processing until the queue is empty;<br>
	 * 							false if working thread should stop after finished current work;<br>
	 * 							���Ϊtrue�������߳�Ҫ�ȵ����д�����˲Ž�����<br>
	 * 							���Ϊfalse�������̴߳����굱ǰ���ݾͽ�����
	 */
	public void stop(boolean afterQueueEmpty){
		for (QueueConsumer<E> c: consumers.values()){
			c.stop(afterQueueEmpty);
		}
	}
	
	/**
	 * Stop working threads after the queue is empty; 
	 * This method will not return until working thread finishes.<br>
	 * �����д����߳��ڶ��д������֮��ֹͣ�����������ȵ����й��������߳̽����ŷ��ء�
	 */
	public void stop(){
		stop(true);
	}

}
