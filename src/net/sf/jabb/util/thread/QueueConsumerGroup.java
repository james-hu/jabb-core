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

import java.util.Collection;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;

import net.sf.jabb.util.text.NameDeduplicator;

/**
 * һ�����д����QueueConsumer��
 * 
 * @author Zhengmao HU (James)
 * 
 * @param <E>	������Ԫ�ص�����
 * 
 */
public class QueueConsumerGroup<E> {
	protected BlockingQueue<E> queue;
	protected Map<String, QueueConsumer<E>> consumers;
	protected ExecutorService threadPool;
	
	/**
	 * ����ʵ����ʹ�ø���QueueConsumer�Լ����̳߳�
	 * @param workQueue		��������
	 */
	protected QueueConsumerGroup(BlockingQueue<E> workQueue){
		queue = workQueue;
		consumers = new TreeMap<String, QueueConsumer<E>>();
	}

	/**
	 * ����ʵ����ͳһʹ��ָ�����̳߳�
	 * @param workQueue
	 * @param executorService
	 */
	protected QueueConsumerGroup(BlockingQueue<E> workQueue, ExecutorService executorService){
		this(workQueue);
		threadPool = executorService;
	}
	
	/**
	 * ����ʵ����ͳһʹ��ָ�����̳߳�
	 * @param workQueue
	 * @param executorService
	 * @param queueConsumers
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
			consumers.put(newName, c);
		}
	}
	
	/**
	 * ����ʵ����ʹ�ø���QueueConsumer�Լ����̳߳�
	 * @param workQueue		��������
	 * @param queueConsumers	�ֳɵ�Consumer
	 */
	public QueueConsumerGroup(BlockingQueue<E> workQueue, QueueConsumer<E>... queueConsumers){
		this(workQueue, null, queueConsumers);
	}
	
	/**
	 * ����ʵ����ͳһʹ��ָ�����̳߳�
	 * @param workQueue		��������
	 * @param queueConsumers	�ֳɵ�Consumer
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
			consumers.put(newName, c);
		}
	}
	
	/**
	 * ����ʵ����ʹ�ø���QueueConsumer�Լ����̳߳�
	 * @param workQueue
	 * @param queueConsumers
	 */
	public QueueConsumerGroup(BlockingQueue<E> workQueue, Collection<QueueConsumer<E>> queueConsumers){
		this(workQueue, null, queueConsumers);
	}	
	
	/**
	 * ������Ѱ�ҵõ�QueueConsumer
	 * @param name
	 * @return
	 */
	public QueueConsumer<E> getConsumer(String name){
		return consumers.get(name);
	}
	
	/**
	 * �����������Consumer
	 */
	public void start(){
		for (QueueConsumer<E> c: consumers.values()){
			c.start();
		}
	}
	
	/**
	 * �Ѵ��������ݷ�����У�����������������ض����ǵȴ�������ɡ�
	 * @param obj
	 */
	public void queue(E obj){
		queue.add(obj);
	}
	
	/**
	 * ���ֹͣ���д����̣߳����������ȵ������߳̽����ŷ��ء�
	 * @param afterQueueEmpty  ���Ϊtrue����ȶ��д�����˲ŷ��أ�����;��緵�ء�
	 */
	public void stop(boolean afterQueueEmpty){
		for (QueueConsumer<E> c: consumers.values()){
			c.stop(afterQueueEmpty);
		}
	}
	
	/**
	 * ����ȴ����д������֮��ֹͣ���д����̣߳����������ȵ������߳̽����ŷ��ء�
	 */
	public void stop(){
		stop(true);
	}

}
