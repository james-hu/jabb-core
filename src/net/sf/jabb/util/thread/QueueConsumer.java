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
	protected int mode;
	protected String name;

	static protected final int MODE_INIT = 0;
	static protected final int MODE_RUN = 1;
	static protected final int MODE_STOP_ASAP = 2;
	static protected final int MODE_STOP_WHEN_EMPTY = 3;
	
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
		mode = MODE_INIT;
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
		synchronized(startStopLock){
			if (thread == null){
				thread = new Thread(this, name);
				mode = MODE_RUN;
				thread.start();
			}
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
		synchronized(startStopLock){
			if (thread != null){
				mode = afterQueueEmpty ? MODE_STOP_WHEN_EMPTY : MODE_STOP_ASAP;
				while(thread.isAlive()){
					thread.interrupt();
					try {
						// Ҳ�������ֿ����ԣ���while���жϵ�ʱ��queue.size()>0��
						// ��take()��ʱ���Ѿ�û����ȡ�ˣ�����join֮��Ҳ�������ȣ�Ҫ����interrupt��
						thread.join(1000);
					} catch (InterruptedException e) {
						// do nothing
					}
				}
				thread = null;
				mode = MODE_INIT;
			}
		}
	}

	@Override
	public void run() {
		int m;	//��ֵ֤һ��
		while(((m = mode) == MODE_RUN) || (m == MODE_STOP_WHEN_EMPTY && queue.size() > 0)){
			E obj = null;
			try {
				obj = queue.take();
			} catch (InterruptedException e) {
				//thread.isInterrupted();
				continue;
			}
			process(obj);
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
