package net.sf.jabb.util.thread.test;

import java.util.concurrent.BlockingQueue;

import net.sf.jabb.util.thread.QueueProcessor;

public class TestStringProcessor extends QueueProcessor<String> {
	
	public TestStringProcessor(){
		super();
	}
	
	public TestStringProcessor(BlockingQueue<String> queue){
		super(queue);
	}

	@Override
	public void process(String obj) {
		try {
			Thread.sleep(800);
		} catch (InterruptedException e) {
			// do nothing
		}
		System.out.println("Processed by " + this.getName() + " : " + obj);
	}

}
