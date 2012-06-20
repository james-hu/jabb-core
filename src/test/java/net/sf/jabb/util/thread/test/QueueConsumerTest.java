package net.sf.jabb.util.thread.test;


import java.util.ArrayList;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.TimeUnit;

import net.sf.jabb.util.thread.QueueConsumer;
import net.sf.jabb.util.thread.QueueConsumerGroup;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

public class QueueConsumerTest {

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
		try {
			Thread.sleep(time);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	@Test
	public void sample() {
		ArrayList<QueueConsumer<String>> processors = new ArrayList<QueueConsumer<String>>(20);
		for (int i = 0; i < 20; i ++){
			processors.add(new TestStringProcessor());
		}
		QueueConsumerGroup<String> group = new QueueConsumerGroup<String>(200, processors);

		group.start();
		for (int i = 0; i < 250; i ++){
			String s = "This is a string_" + i;
			try {
				group.queue(s);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			System.out.println("Queued: " + s);
		}
		group.stop();
		System.out.println("All finished!");
	}

	
	//@Ignore
	@Test
	public void simple(){
		ArrayBlockingQueue<String> stringQueue  = new ArrayBlockingQueue<String>(100);
		TestStringProcessor stringConsumer  = new TestStringProcessor(stringQueue);
		Thread provider = new TestStringProvider(stringQueue, 50);
		provider.start();
		
		System.out.print("simple stringConsumer.start()... ");
		stringConsumer.start();
		System.out.println("done.");
		
		sleep(3000);
		
		System.out.println(stringQueue);
		System.out.println("stringConsumer.stop(false)... ");
		stringConsumer.stop(false);
		System.out.println("done.");

		sleep(2000);

		System.out.print("stringConsumer.start()... ");
		stringConsumer.start();
		System.out.println("done.");
		
		System.out.println(stringQueue);
		System.out.println("stringConsumer.stop()... ");
		stringConsumer.stop();
		System.out.println("done.");
		System.out.println(stringQueue);
		
		try {
			provider.join();
			stringQueue.put("END");
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		stringConsumer.start();
		stringConsumer.stop();
		System.out.println("EXIT");
	}
	
	@SuppressWarnings("unchecked")
	@Test
	public void complex(){
		QueueConsumerGroup<String> consumers = new QueueConsumerGroup<String>(200,
				new TestStringProcessor(),
				new TestStringProcessor(),
				new TestStringProcessor()
				);
		Thread provider = new TestStringProvider(consumers.getQueue(), 60);
		provider.start();
		
		System.out.print("group stringConsumer.start()... ");
		consumers.start();
		System.out.println("done.");
		
		sleep(9000);
		
		System.out.println(consumers.getQueue());
		System.out.println("stringConsumer.stop(false)... ");
		consumers.stop(false);
		System.out.println("done.");

		sleep(1500);

		System.out.print("stringConsumer.start()... ");
		consumers.start();
		System.out.println("done.");
		
		System.out.println(consumers.getQueue());
		System.out.println("stringConsumer.stop()... ");
		consumers.stop();
		System.out.println("done.");
		System.out.println(consumers.getQueue());
		
		try {
			provider.join();
			consumers.getQueue().put("END");
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		consumers.start();
		consumers.stop();
		System.out.println("EXIT");
	}

	@Test
	public void batchNoWait(){
		ArrayBlockingQueue<String> stringQueue  = new ArrayBlockingQueue<String>(200);
		TestStringBatchUniqueProcessor stringConsumer  = new TestStringBatchUniqueProcessor(stringQueue);
		Thread provider = new TestStringProvider(stringQueue, 100);
		provider.start();

		System.out.print("simple_batch_no_wait stringConsumer.start()... ");
		stringConsumer.start();
		System.out.println("done.");
		
		sleep(5000);
		
		System.out.println(stringQueue);
		System.out.println("stringConsumer.stop(false)... ");
		stringConsumer.stop(false);
		System.out.println("done.");

		sleep(1500);

		System.out.print("stringConsumer.start()... ");
		stringConsumer.start();
		System.out.println("done.");
		
		System.out.println(stringQueue);
		System.out.println("stringConsumer.stop()... ");
		stringConsumer.stop();
		System.out.println("done.");
		System.out.println(stringQueue);
		
		try {
			provider.join();
			stringQueue.put("END");
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		stringConsumer.start();
		stringConsumer.stop();
		System.out.println("EXIT");
		
	}
	
	@SuppressWarnings("unchecked")
	@Test
	public void batchWait(){
		ArrayBlockingQueue<String> stringQueue  = new ArrayBlockingQueue<String>(200);
		QueueConsumerGroup<String> consumers = new QueueConsumerGroup<String>(stringQueue,
				new TestStringBatchUniqueProcessor(stringQueue, 500, TimeUnit.MILLISECONDS),
				new TestStringBatchUniqueProcessor(stringQueue, 500, TimeUnit.MILLISECONDS),
				new TestStringBatchUniqueProcessor(stringQueue, 500, TimeUnit.MILLISECONDS)
				);
		Thread provider = new TestStringProvider(stringQueue, 200);
		provider.start();
		
		System.out.print("group_batch_wait stringConsumer.start()... ");
		consumers.start();
		System.out.println("done.");
		
		sleep(5000);
		
		System.out.println(stringQueue);
		System.out.println("stringConsumer.stop(false)... ");
		consumers.stop(false);
		System.out.println("done.");
		
		sleep(1500);


		System.out.print("stringConsumer.start()... ");
		consumers.start();
		System.out.println("done.");
		
		System.out.println(stringQueue);
		System.out.println("stringConsumer.stop()... ");
		consumers.stop();
		System.out.println("done.");
		System.out.println(stringQueue);
		
		try {
			provider.join();
			stringQueue.put("END");
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		consumers.start();
		consumers.stop();
		System.out.println("EXIT");
		
	}

}
