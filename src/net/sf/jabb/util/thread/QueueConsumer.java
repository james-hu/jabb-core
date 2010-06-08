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
 * 从队列中取数据，进行处理。
 * 
 * @author Zhengmao HU (James)
 * 
 * @param <E>	队列中元素的类型
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
	 * 创建一个实例
	 * @param name	名称，会被用在线程名里
	 * @param workQueue	从这个队列取得待处理数据
	 * 
	 */
	public QueueConsumer(String name, BlockingQueue<E> workQueue){
		startStopLock = new Object();
		this.name = name;
		queue = workQueue;
		mode = MODE_INIT;
	}
	
	/**
	 * 创建一个实例
	 * @param workQueue	从这个队列取得待处理数据
	 */
	public QueueConsumer(BlockingQueue<E> workQueue){
		this(QueueConsumer.class.getSimpleName(), workQueue);
	}
	
	/**
	 * 把待处理数据放入队列，这个方法会立即返回而不是等待处理完成。
	 * @param obj
	 */
	public void queue(E obj){
		queue.add(obj);
	}
	
	/**
	 * 启动处理线程，这个方法立即返回。
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
	 * 等待队列处理空了之后停止处理线程，这个方法会等到处理线程结束才返回。
	 */
	public void stop(){
		stop(true);
	}

	/**
	 * 停止处理线程，这个方法会等到处理线程结束才返回。
	 * @param afterQueueEmpty	如果为true，则等队列处理空了才返回，否则就尽早返回。
	 */
	public void stop(boolean afterQueueEmpty){
		synchronized(startStopLock){
			if (thread != null){
				mode = afterQueueEmpty ? MODE_STOP_WHEN_EMPTY : MODE_STOP_ASAP;
				while(thread.isAlive()){
					thread.interrupt();
					try {
						// 也存在这种可能性：在while作判断的时候，queue.size()>0，
						// 而take()的时候已经没东西取了，所以join之后也不能死等，要反复interrupt。
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
		int m;	//保证值一致
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
	 * 这个方法在运行过程中可能会遇到线程的interrupt，所以如果有以下情况要注意正确处理：
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
