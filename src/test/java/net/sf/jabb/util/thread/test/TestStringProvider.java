package net.sf.jabb.util.thread.test;

import java.util.concurrent.BlockingQueue;

public class TestStringProvider extends Thread {
	int maxRepeat;
	BlockingQueue<String> stringQueue;
	
	public TestStringProvider(BlockingQueue<String> queue, int repeat){
		maxRepeat = repeat;
		stringQueue = queue;
	}
	public void run(){
		for (int i = 0; i < maxRepeat; i ++){
			try {
				System.out.println("\t\t\t\t\t putting " + i);
				stringQueue.put("item_" + i);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

}
