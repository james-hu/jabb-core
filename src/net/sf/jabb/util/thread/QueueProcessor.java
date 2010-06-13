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
 * �Ӷ�����ȡ���ݣ����д��������������߳��Ǵ��ڲ����̳߳�ȡ�ġ�
 * ����ʽ��һ��һ��ȡ��һ��һ������ 
 * 
 * @author Zhengmao HU (James)
 *
 * @param <E>	������Ԫ�ص�����
 */
abstract public class QueueProcessor<E> extends QueueConsumer<E> {

	/**
	 * ����һ��ʵ����
	 * @param name			���ƣ��ᱻ�����߳�����
	 * @param workQueue			���������ȡ�ô���������
	 * @param executorService	ָ�����������߳�
	 */
	public QueueProcessor(BlockingQueue<E> workQueue, String name, ExecutorService executorService){
		super(workQueue, name, executorService);
	}
	
	
	/**
	 * ����һ��ʵ����ʹ��ȱʡ���̳߳ء�
	 * @param name	���ƣ��ᱻ�����߳�����
	 * @param workQueue	���������ȡ�ô���������
	 * 
	 */
	public QueueProcessor(BlockingQueue<E> workQueue, String name){
		this(workQueue, name, defaultThreadPool);
	}
	
	/**
	 * ����һ��ʵ����ʹ��ȱʡ���߳����ƺ�ȱʡ���̳߳ء�
	 * @param workQueue	���������ȡ�ô���������
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
	 * ����Ĵ�������һ�δ���һ������
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
	 * @param obj
	 */
	abstract public void process(E obj );


}
