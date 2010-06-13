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
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * �Ӷ�����ȡ���ݣ����д��������������߳��Ǵ��ڲ����̳߳�ȡ�ġ�
 * 
 * @author Zhengmao HU (James)
 * 
 * @param <E>	������Ԫ�ص�����
 *
 */
public abstract class QueueConsumer<E> implements Runnable{
	protected BlockingQueue<E> queue;
	protected Thread thread;
	protected Object startStopLock;
	protected AtomicInteger mode;
	protected String name;

	static protected final int MODE_INIT = 0;
	static protected final int MODE_START = 1;
	static protected final int MODE_RUNNING = 2;
	static protected final int MODE_STOP_ASAP = 3;
	static protected final int MODE_STOP_WHEN_EMPTY = 4;
	static protected final int MODE_STOPPED = 5;
	
	static protected ExecutorService defaultThreadPool;
	
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
	 * ����һ��ʵ����
	 * @param name			���ƣ��ᱻ�����߳�����
	 * @param workQueue			���������ȡ�ô���������
	 * @param executorService	ָ�����������߳�
	 */
	public QueueConsumer(BlockingQueue<E> workQueue, String name, ExecutorService executorService){
		threadPool = executorService;
		startStopLock = new Object();
		this.name = name;
		queue = workQueue;
		mode = new AtomicInteger(MODE_INIT);
	}
	
	
	/**
	 * ����һ��ʵ����ʹ��ȱʡ���̳߳ء�
	 * @param name	���ƣ��ᱻ�����߳�����
	 * @param workQueue	���������ȡ�ô���������
	 * 
	 */
	public QueueConsumer(BlockingQueue<E> workQueue, String name){
		this(workQueue, name, defaultThreadPool);
	}
	
	/**
	 * ����һ��ʵ����ʹ��ȱʡ���߳����ƺ�ȱʡ���̳߳ء�
	 * @param workQueue	���������ȡ�ô���������
	 */
	public QueueConsumer(BlockingQueue<E> workQueue){
		this(workQueue, QueueConsumer.class.getSimpleName());
	}
	
	/**
	 * �Ѵ��������ݷ�����У�����������������ض����ǵȴ�������ɡ�
	 * @param obj
	 */
	public void queue(E obj){
		queue.add(obj);
	}
	
	/**
	 * ���������̣߳���������������ء�
	 */
	public void start(){
		if (mode.compareAndSet(MODE_INIT, MODE_START)){
			threadPool.execute(this);
		}
	}
	
	/**
	 * �ȴ����д������֮��ֹͣ�����̣߳����������ȵ������߳̽����ŷ��ء�
	 */
	public void stop(){
		stop(true);
	}

	/**
	 * ֹͣ�����̣߳����������ȵ������߳̽����ŷ��ء�
	 * @param afterQueueEmpty	���Ϊtrue����ȶ��д�����˲ŷ��أ�����;��緵�ء�
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
	 * ʹ�ö����е�����
	 */
	abstract protected void consume();  
	
}
