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
 * �Ӷ�����ȡ���ݣ����д���
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
	
	
	static protected ExecutorService threadPool;
	
	static{
		threadPool = Executors.newCachedThreadPool();
	}
	
	public void setName(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}
	
	/**
	 * ����һ��ʵ��
	 * @param name	���ƣ��ᱻ�����߳�����
	 * @param workQueue	���������ȡ�ô���������
	 * 
	 */
	public QueueConsumer(String name, BlockingQueue<E> workQueue){
		startStopLock = new Object();
		this.name = name;
		queue = workQueue;
		mode = new AtomicInteger(MODE_INIT);
	}
	
	/**
	 * ����һ��ʵ��
	 * @param workQueue	���������ȡ�ô���������
	 */
	public QueueConsumer(BlockingQueue<E> workQueue){
		this(QueueConsumer.class.getSimpleName(), workQueue);
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
			E obj = null;
			try {
				obj = queue.take();
			} catch (InterruptedException e) {
				//thread.isInterrupted();
				continue;
			}
			process(obj);
		}
		if (!mode.compareAndSet(MODE_STOP_WHEN_EMPTY, MODE_STOPPED) && !mode.compareAndSet(MODE_STOP_ASAP, MODE_STOPPED)){
			throw new IllegalStateException("Should be in state MODE_STOP_WHEN_EMPTY or MODE_STOP_ASAP, but actully not.");
		}
	}
	
	/**
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
