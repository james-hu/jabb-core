package net.sf.jabb.util.thread.test;


import java.util.concurrent.ArrayBlockingQueue;

import net.sf.jabb.util.text.DurationFormatter;
import net.sf.jabb.util.thread.QueueConsumerGroup;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class ProcessorStressing {

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}
	
	protected void sleep(long time){
		System.out.println("==== Sleeping...");
		try {
			Thread.sleep(time);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println("===== Awake.");
	}

	//@Test
	public void stress(){
		ArrayBlockingQueue<String> stringQueue  = new ArrayBlockingQueue<String>(600000);
		QueueConsumerGroup<String> consumers = new QueueConsumerGroup<String>(stringQueue,
				new StressStringProcessor(stringQueue),
				new StressStringProcessor(stringQueue),
				new StressStringProcessor(stringQueue)
				);
		Thread provider = new StressStringProvider(stringQueue, 600000);
		consumers.start();
		provider.start();
		sleep(9000);
		consumers.stop(false);
		sleep(1000);
		consumers.start();
		sleep(5000);
		consumers.stop();
		System.out.println("********* All done. ********");
	}

	@Test
	public void stressSpeed(){
		ArrayBlockingQueue<String> stringQueue  = new ArrayBlockingQueue<String>(600000);
		QueueConsumerGroup<String> consumers = new QueueConsumerGroup<String>(stringQueue,
				new StressStringProcessor(stringQueue),
				new StressStringProcessor(stringQueue),
				new StressStringProcessor(stringQueue),
				new StressStringProcessor(stringQueue),
				new StressStringProcessor(stringQueue)
				);
		Thread provider = new StressStringProvider(stringQueue, 60000);
		Thread provider2 = new StressStringProvider(stringQueue, 60000);
		Thread provider3 = new StressStringProvider(stringQueue, 60000);
		provider.start();
		//provider2.start();
		//provider3.start();
		long t0 = System.currentTimeMillis();
		consumers.start();
		sleep(2000);
		consumers.stop();
		System.out.println("********* " + DurationFormatter.formatSince(t0) + " ********");
	}


}
