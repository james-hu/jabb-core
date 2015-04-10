/**
 * 
 */
package net.sf.jabb.util.stat;

import org.junit.Test;
import static org.junit.Assert.*;

/**
 * @author James Hu
 *
 */
public class ConcurrentMinMaxHoldersTest {
	static final int NUM_THREADS = 50;
	volatile boolean start;
	volatile boolean stop;
	
	@Test
	public void testLong() throws InterruptedException{
		ConcurrentLongMinMaxHolder holder = new ConcurrentLongMinMaxHolder();
		doTest(holder);
	}
	
	@Test
	public void testBigInteger() throws InterruptedException{
		ConcurrentBigIntegerMinMaxHolder holder = new ConcurrentBigIntegerMinMaxHolder();
		doTest(holder);
	}
	
	protected void doTest(MinMaxHolder holder) throws InterruptedException{
		start = false;
		stop = false;
		
		for (int i = 0; i < NUM_THREADS; i ++){
			TestWorker worker = new TestWorker(holder);
			new Thread(worker).start();
		}
		
		start = true;
		Thread.sleep(10000L);
		stop = true;
		assertNotNull(holder);
		assertNotNull(holder.getMin());
		assertNotNull(holder.getMax());
		assertEquals(0, holder.getMinAsLong().intValue());
		assertEquals(4999, holder.getMaxAsLong().intValue());
	}
	
	class TestWorker implements Runnable{
		MinMaxHolder holder;
		
		public TestWorker(MinMaxHolder holder){
			this.holder = holder;
		}
		
		@Override
		public void run() {
			while(!start){
				// do nothing
			}
			while(!stop){
				try{
					holder.evaluate(System.currentTimeMillis() % 5000L);
					/*
					if (null == holder.getMin()){
						System.out.println("null == holder.getMin()");
					}
					if (null == holder.getMax()){
						System.out.println("null == holder.getMax()");
					}*/
				}catch(Exception e){
					e.printStackTrace();
				}
			}
		}
	}
}
