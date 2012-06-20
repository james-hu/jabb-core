package net.sf.jabb.util.text.test;

import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

import junit.framework.Assert;

import net.sf.jabb.util.stat.BasicFrequencyCounter;
import net.sf.jabb.util.text.NameDeduplicator;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class NameDeduplicatorTest {
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
		for (int i = 0; i < 100; i ++){
			new Thread(new Runnable(){

				@Override
				public void run() {
					while (!stopNow){
						long l = nd.nextId("The Name");
						fc.count(l);
					}
					
				}
				
			}).start();
		}
		Thread.sleep(20*1000);
		stopNow = true;
		
		Thread.sleep(2*1000);
		long id = -1;
		for (Map.Entry<Long, AtomicLong> e: fc.getCounts().entrySet()){
			Assert.assertTrue(e.getKey() == id + 1);
			id = e.getKey();
			Assert.assertTrue(e.getValue().get() == 1);
		}
	}

}
