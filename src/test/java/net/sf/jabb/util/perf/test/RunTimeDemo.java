/*
Copyright 2011 Zhengmao HU (James)

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
*/
package net.sf.jabb.util.perf.test;

import net.sf.jabb.util.perf.RunTime;
import net.sf.jabb.util.thread.QueueConsumerGroup;
import net.sf.jabb.util.thread.QueueProcessor;

public class RunTimeDemo {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		int numOfThreads = 3;
		int numOfStrings = 100000;
		QueueProcessor<String>[] processors = new QueueProcessor[numOfThreads];
		final RunTime queueRT = new RunTime("Queue Processing");
		for (int i = 0; i < numOfThreads; i ++){
			final String processorName = "Processor " + i;
			processors[i] = new QueueProcessor<String>(){
				RunTime processorRT= queueRT.addDetail(processorName);
				@Override
				public void process(String obj) {
					processorRT.start();
					for (int j = 0; j < 2000; j ++){
						// just to consume some time
						processorRT.setAttachment(obj);
					}
					processorRT.end();
					
				}
				
			};
		}
		QueueConsumerGroup<String> group = new QueueConsumerGroup<String>(numOfStrings, processors);
		for (int i = 0; i < numOfStrings; i ++){
			try {
				group.getQueue().put("String " + i);
			} catch (InterruptedException e) {
				// continue with next
				e.printStackTrace();
			}
		}
		queueRT.start();
		group.start();
		group.stop();
		queueRT.end();

		System.out.println(queueRT);
		
		RunTime grandParentRT = new RunTime("Grandparent");
		grandParentRT.addDetail("Parent").addDetail(queueRT);
		System.out.println(grandParentRT);
	}

}
