/**
 * 
 */
package net.sf.jabb.util.col;

import static org.junit.Assert.*;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.junit.Test;

/**
 * @author James Hu
 *
 */
public class SizeLimitableBlockingQueueTest {

	@Test
	public void test1() throws InterruptedException {
		SizeLimitableBlockingQueue<Runnable> queue = testWith(100, 40, 10, 50);
		int length;
		do {
			Thread.sleep(100);
			length = queue.size();
			assertTrue("queue length must be smaller than " + queue.getSizeLimit() + ", but it is actually " + length, length <= queue.getSizeLimit());
		}while(length > 0);
	}
	
	@Test(expected=java.util.concurrent.RejectedExecutionException.class)
	public void test2() throws InterruptedException {
		SizeLimitableBlockingQueue<Runnable> queue = testWith(100, 40, 10, 60);
		int length;
		do {
			Thread.sleep(100);
			length = queue.size();
			assertTrue("queue length must be smaller than " + queue.getSizeLimit() + ", but it is actually " + length, length <= queue.getSizeLimit());
		}while(length > 0);
	}
	
	protected SizeLimitableBlockingQueue<Runnable> testWith(int physicalSize, int sizeLimit, int threadPoolSize, int taskCount){
		SizeLimitableBlockingQueue<Runnable> queue = new SizeLimitableBlockingQueue<Runnable>(new ArrayBlockingQueue<Runnable>(physicalSize));
		queue.setSizeLimit(sizeLimit);
		
		ThreadPoolExecutor threadPool = new ThreadPoolExecutor(0, threadPoolSize, 5, TimeUnit.SECONDS, queue);
		
		for (int i = 0; i < taskCount; i ++){
			threadPool.execute(new Sleep1SecTask());
		}
		return queue;
	}

	static private class Sleep1SecTask implements Runnable{
		@Override
		public void run() {
			try {
				Thread.sleep(1000L);
			} catch (InterruptedException e) {
				Thread.currentThread().interrupt();
				e.printStackTrace();
			}
		}
		
	}
}
