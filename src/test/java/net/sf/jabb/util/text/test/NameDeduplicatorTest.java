package net.sf.jabb.util.text.test;

import java.util.Map;
import java.util.TreeSet;
import java.util.concurrent.atomic.AtomicLong;

import junit.framework.Assert;

import net.sf.jabb.util.stat.BasicFrequencyCounter;
import net.sf.jabb.util.text.NameDeduplicator;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class NameDeduplicatorTest {
	static final int NUMBER_OF_THREADS = 50;
	
	NameDeduplicator nd;
	boolean stopNow = false;
	BasicFrequencyCounter fc;

	@Before
	public void setUp() throws Exception {
		nd = new NameDeduplicator();
		fc = new BasicFrequencyCounter();
		stopNow = false;
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void test() throws InterruptedException {
		//System.out.print("Starting threads: ");
		Thread[] threads = new Thread[NUMBER_OF_THREADS];
		for (int i = 0; i < NUMBER_OF_THREADS; i ++){
			threads[i] = new Thread(){

				public void run() {
					while (!stopNow){
						long l = nd.nextId("The Name");
						fc.count(l);
					}
					
				}
				
			};
			threads[i].start();
		}
		//System.out.println("\nStarted " + NUMBER_OF_THREADS + " threads.");
		Thread.sleep(10*1000);
		stopNow = true;
		//System.out.println("Stopping...");
		
		for (Thread thread: threads){
			thread.join();
		}
		
		//System.out.println("Checking...");
		long id = 0;
		for (Long k: new TreeSet<Long>(fc.getCounts().keySet())){
			Assert.assertEquals(Long.valueOf(id), k);
			Assert.assertEquals(1, fc.getCount(k));
			id++;
		}
		//System.out.println("Done.");
	}

}
