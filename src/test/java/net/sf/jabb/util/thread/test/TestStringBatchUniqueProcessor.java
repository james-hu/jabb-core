package net.sf.jabb.util.thread.test;

import java.util.Set;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

import net.sf.jabb.util.thread.QueueBatchUniqueProcessor;

public class TestStringBatchUniqueProcessor extends
		QueueBatchUniqueProcessor<String> {

	public TestStringBatchUniqueProcessor(BlockingQueue<String> workQueue, long waitTime, TimeUnit waitUnit) {
		super(workQueue, 10, waitTime, waitUnit);
	}

	public TestStringBatchUniqueProcessor(BlockingQueue<String> workQueue) {
		super(workQueue, 10);
	}

	@Override
	public void process(Set<String> batch) {
		System.out.println("Batch processing: " + batch + " ... done.");
		try {
			Thread.sleep(800);
		} catch (InterruptedException e) {
			// do nothing
		}
		
	}

}
