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
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * A template for processing data from a queue.<br>
 * һ���Ӷ�����ȡ���ݽ��д����ģ�塣
 * <p>
 * One working thread will be created for one instance of this class.
 * <p>
 * �����ÿ��ʵ����Ӧ�Ļ���һ�������̡߳�
 * 
 * @author Zhengmao HU (James)
 * 
 * @param <E>	Type of the data in the queue.<br>���������ݵ�����
 *
 */
public abstract class QueueConsumer<E> implements Runnable{
	protected BlockingQueue<E> queue;
	/**
	 * The working thread.<br>
	 * �����̡߳�
	 */
	protected Thread thread;
	protected AtomicInteger mode;
	/**
	 * Name of this instance, which determines the naming of working thread.<br>
	 * ����ʵ�������֣��������˹����̵߳����֡�
	 */
	protected String name;

	static protected final int MODE_INIT = 0;
	static protected final int MODE_START = 1;
	static protected final int MODE_RUNNING = 2;
	static protected final int MODE_STOP_ASAP = 3;
	static protected final int MODE_STOP_WHEN_EMPTY = 4;
	static protected final int MODE_STOPPED = 5;
	
	/**
	 * If no ExecutorService is specified in constructor,
	 * then the working thread will come from this thread pool.<br>
	 * ����ڹ��췽����û��ָ��ExecutorService�������Ĺ����߳�
	 * ��ȡ������̳߳ء�
	 * <p>
	 * defaultThreadPool = Executors.newCachedThreadPool()
	 */
	static protected ExecutorService defaultThreadPool;
	
	/**
	 * Effective thread pool of this instance.<br>
	 * ����ʵ��ʵ��ʹ�õ��̳߳ء�
	 */
	protected ExecutorService threadPool;
	
	static{
		defaultThreadPool = Executors.newCachedThreadPool();
	}
	
	public void setName(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}
	
	public void setExecutorService(ExecutorService threadPool) {
		this.threadPool = threadPool;
	}

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
	public QueueConsumer(BlockingQueue<E> workQueue, String name, ExecutorService executorService){
		threadPool = executorService;
		this.name = name;
		queue = workQueue;
		mode = new AtomicInteger(MODE_INIT);
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
	public QueueConsumer(BlockingQueue<E> workQueue, String name){
		this(workQueue, name, defaultThreadPool);
	}
	
	/**
	 * Constructor to create an instance with default name and using default thread pool.<br>
	 * ����һ��ʵ����ʹ��ȱʡ�����ƺ�ȱʡ���̳߳ء�
	 * 
	 * @param workQueue			The queue that data for processing will be fetched from.<br>
	 * 							��ʵ�������������ȡ�ô��������ݡ�
	 */
	public QueueConsumer(BlockingQueue<E> workQueue){
		this(workQueue, QueueConsumer.class.getSimpleName());
	}
	
	/**
	 * Put data into the queue for processing, 
	 * this method will return immediately 
	 * without waiting for the data to be actually processed.<br>
	 * �Ѵ��������ݷ�����У�����������������ض����ǵȴ�ʵ�ʴ�����ɡ�
	 * 
	 * @param obj	Data need to be processed<br>
	 * 				����������ݡ�
	 */
	public void queue(E obj){
		queue.add(obj);
	}
	
	/**
	 * Start working thread; This method will return immediately 
	 * without waiting for anything to be actually processed.<br>
	 * �������������̣߳���������������أ������ȴ��κ�ʵ�ʴ�������
	 */
	public void start(){
		if (mode.compareAndSet(MODE_INIT, MODE_START)){
			threadPool.execute(this);
		}
	}
	
	/**
	 * Stop working thread after the queue is empty; 
	 * This method will not return until working thread finishes.<br>
	 * �ȴ����д������֮��ֹͣ�����̣߳����������ȵ����������߳̽����ŷ��ء�
	 */
	public void stop(){
		stop(true);
	}

	/**
	 * Stop working thread; 
	 * This method will not return until working thread finishes.<br>
	 * ֹͣ�����̣߳����������ȵ����������߳̽����ŷ��ء�
	 * 
	 * @param afterQueueEmpty	true if working thread should keep processing until the queue is empty;<br>
	 * 							false if working thread should stop after finished current work;<br>
	 * 							���Ϊtrue�������߳�Ҫ�ȵ����д�����˲Ž�����<br>
	 * 							���Ϊfalse�������̴߳����굱ǰ���ݾͽ�����
	 */
	public void stop(boolean afterQueueEmpty){
		if (mode.compareAndSet(MODE_RUNNING, afterQueueEmpty ? MODE_STOP_WHEN_EMPTY : MODE_STOP_ASAP)){
			while(! mode.compareAndSet(MODE_STOPPED, MODE_INIT)){
				thread.interrupt();
				try {
					// Ҳ�������ֿ����ԣ���while���жϵ�ʱ��queue.size()>0��
					// ��take()��ʱ���Ѿ�û����ȡ�ˣ�����join֮��Ҳ�������ȣ�Ҫ����interrupt��
					thread.join(1000);
				} catch (InterruptedException e) {
					// do nothing
				}
			}
		}
	}

	@Override
	public void run() {
		thread = Thread.currentThread();	// run from thread in executor
		thread.setName(name);
		if (!mode.compareAndSet(MODE_START, MODE_RUNNING)){
			throw new IllegalStateException("Should be in state MODE_START, but actully not.");
		}
		
		int m;	//��ֵ֤һ��
		while(((m = mode.get()) == MODE_RUNNING) || (m == MODE_STOP_WHEN_EMPTY && queue.size() > 0)){
			consume();
		}
		if (!mode.compareAndSet(MODE_STOP_WHEN_EMPTY, MODE_STOPPED) && !mode.compareAndSet(MODE_STOP_ASAP, MODE_STOPPED)){
			throw new IllegalStateException("Should be in state MODE_STOP_WHEN_EMPTY or MODE_STOP_ASAP, but actully not.");
		}
	}
	
	/**
	 * Process the data in queue - this method should be override in subclass.<br>
	 * ��������е����ݡ����������Ӧ���������б����ء�
	 */
	abstract protected void consume();  
	
}
