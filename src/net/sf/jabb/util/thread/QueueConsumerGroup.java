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

import net.sf.jabb.util.text.NameDeduplicator;

/**
 * 一批并行处理的QueueConsumer。
 * 
 * @author Zhengmao HU (James)
 * 
 * @param <E>	队列中元素的类型
 * 
 */
public class QueueConsumerGroup<E> {
	protected BlockingQueue<E> queue;
	protected Map<String, QueueConsumer<E>> consumers;
	
	/**
	 * 创建实例
	 * @param workQueue		工作队列
	 */
	public QueueConsumerGroup(BlockingQueue<E> workQueue){
		queue = workQueue;
		consumers = new TreeMap<String, QueueConsumer<E>>();
	}
	
	/**
	 * 创建实例
	 * @param workQueue		工作队列
	 * @param queueConsumers	现成的Consumer
	 */
	public QueueConsumerGroup(BlockingQueue<E> workQueue, QueueConsumer<E>... queueConsumers){
		this(workQueue);
		NameDeduplicator ndd = new NameDeduplicator();
		for (QueueConsumer<E> c: queueConsumers){
			String newName = ndd.deduplicate(c.getName());
			c.setName(newName);
			consumers.put(newName, c);
		}
	}
	
	/**
	 * 创建实例
	 * @param workQueue		工作队列
	 * @param queueConsumers	现成的Consumer
	 */
	public QueueConsumerGroup(BlockingQueue<E> workQueue, Collection<QueueConsumer<E>> queueConsumers){
		this(workQueue);
		NameDeduplicator ndd = new NameDeduplicator();
		for (QueueConsumer<E> c: queueConsumers){
			String newName = ndd.deduplicate(c.getName());
			c.setName(newName);
			consumers.put(newName, c);
		}
	}
	
	/**
	 * 按名称寻找得到QueueConsumer
	 * @param name
	 * @return
	 */
	public QueueConsumer<E> getConsumer(String name){
		return consumers.get(name);
	}
	
	/**
	 * 逐个启动所有Consumer
	 */
	public void start(){
		for (QueueConsumer<E> c: consumers.values()){
			c.start();
		}
	}
	
	/**
	 * 把待处理数据放入队列，这个方法会立即返回而不是等待处理完成。
	 * @param obj
	 */
	public void queue(E obj){
		queue.add(obj);
	}
	
	/**
	 * 逐个停止所有处理线程，这个方法会等到处理线程结束才返回。
	 * @param afterQueueEmpty  如果为true，则等队列处理空了才返回，否则就尽早返回。
	 */
	public void stop(boolean afterQueueEmpty){
		for (QueueConsumer<E> c: consumers.values()){
			c.stop(afterQueueEmpty);
		}
	}
	
	/**
	 * 逐个等待队列处理空了之后停止所有处理线程，这个方法会等到处理线程结束才返回。
	 */
	public void stop(){
		stop(true);
	}

}
