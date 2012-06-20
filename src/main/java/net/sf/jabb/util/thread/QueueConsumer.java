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
 * A template for consuming data from a queue.<br>
 * һ���Ӷ�����ȡ���ݽ��д����ģ�塣
 * <p>
 * One working thread will be created for each instance of this class when necessary.
 * <p>
 * �����ÿ��ʵ����Ӧ�Ļ���һ�������߳�����Ҫ��ʱ�򱻴�����
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
	 * Use this method if you don't want the queue to be passed in from constructor.<br>
	 * ����㲻��ͨ�����췽�������ݶ��ж��󣬾������������
	 * 
	 * @param workQueue 	The queue that data for processing will be fetched from.<br>
	 * 						��ʵ�������������ȡ�ô��������ݡ�
	 */
	public void setQueue(BlockingQueue<E> workQueue) {
		this.queue = workQueue;
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
	 * Constructor to create an instance.<br>
	 * ����һ��ʵ����
	 * 
	 * @param name			Name of this instance, which determines the naming of working thread.<br>
	 * 						����ʵ�������ƣ��ᱻ���ڹ����߳����
	 * @param executorService	Thread pool that working thread will be get from.<br>
	 * 							ָ���ñ�ʵ���������ù����̡߳�
	 */
	public QueueConsumer(String name, ExecutorService executorService){
		this(null, name, executorService);
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
	 * Constructor to create an instance using default thread pool.<br>
	 * ����һ��ʹ��ȱʡ�̳߳ص�ʵ����
	 * 
	 * @param name			Name of this instance, which determines the naming of working thread.<br>
	 * 						����ʵ�������ƣ��ᱻ���ڹ����߳����
	 */
	public QueueConsumer(String name){
		this(null, name, defaultThreadPool);
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
	 * Constructor to create an instance with default name and using default thread pool.<br>
	 * ����һ��ʵ����ʹ��ȱʡ�����ƺ�ȱʡ���̳߳ء�
	 */
	public QueueConsumer(){
		this(null, QueueConsumer.class.getSimpleName());
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
		preStop(afterQueueEmpty);
		while(!(mode.compareAndSet(MODE_STOPPED, MODE_INIT) 
				|| mode.get() == MODE_INIT)){
			try {
				thread.join(100);
			} catch (InterruptedException e) {
				// do nothing
			}
			preStop(afterQueueEmpty);
			// Ҳ�������ֿ����ԣ���while���жϵ�ʱ��queue.size()>0��
			// ��take()��ʱ���Ѿ�û����ȡ�ˣ�����join֮��Ҳ�������ȣ�Ҫ����interrupt��
			thread.interrupt();
		}
	}
	
	/**
	 * Ask the working thread to stop;
	 * This method will return immediately.<br>
	 * Ҫ�����߳�ֹͣ����������������ء�
	 * 
	 * @param afterQueueEmpty	true if working thread should keep processing until the queue is empty;<br>
	 * 							false if working thread should stop after finished current work;<br>
	 * 							���Ϊtrue�������߳�Ҫ�ȵ����д�����˲Ž�����<br>
	 * 							���Ϊfalse�������̴߳����굱ǰ���ݾͽ�����
	 */
	public void preStop(boolean afterQueueEmpty){
		mode.compareAndSet(MODE_RUNNING, afterQueueEmpty ? MODE_STOP_WHEN_EMPTY : MODE_STOP_ASAP);
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
		thread.interrupt();
	}
	
	/**
	 * Consume the data in queue - this method should be overridden in subclass.<br>
	 * ��������е����ݡ����������Ӧ���������б����ء�
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
	 */
	abstract protected void consume();  
	
}
