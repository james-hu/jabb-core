package net.sf.jabb.util.thread.test;

import java.util.concurrent.BlockingQueue;

import net.sf.jabb.util.thread.QueueProcessor;

public class StressStringProcessor extends QueueProcessor<String> {
	
	public StressStringProcessor(BlockingQueue<String> queue){
		super(queue);
	}

	@Override
	public void process(String obj) {
		System.out.println("Processing: " + obj + " ... done.");
	}

}
