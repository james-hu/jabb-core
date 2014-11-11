package net.sf.jabb.util.stat.test;


import java.math.BigInteger;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

import net.sf.jabb.util.stat.BasicFrequencyCounter;
import net.sf.jabb.util.stat.FrequencyCounter;
import net.sf.jabb.util.stat.FrequencyCounterDefinition;
import net.sf.jabb.util.stat.PackagedFrequencyCounter;
import net.sf.jabb.util.text.DurationFormatter;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class CounterPerformanceTest {
	static final int NUMBER_OF_THREADS = 6;
	static final int NUMBER_OF_SECONDS = 30;

	protected PackagedFrequencyCounter createPackagedCounter(){
		FrequencyCounterDefinition d1s = 
				new FrequencyCounterDefinition("1 second", 1, TimeUnit.SECONDS);
		FrequencyCounterDefinition d1m = 
				new FrequencyCounterDefinition("1 minute", 1, TimeUnit.MINUTES);
		FrequencyCounterDefinition d5m = 
				new FrequencyCounterDefinition("5 minute", 5, TimeUnit.MINUTES);
		FrequencyCounterDefinition d10m = 
			new FrequencyCounterDefinition("10 minutes", 10, TimeUnit.MINUTES);
		FrequencyCounterDefinition d1h = 
				new FrequencyCounterDefinition("1 hour", 1, TimeUnit.HOURS);
		FrequencyCounterDefinition d1d = 
			new FrequencyCounterDefinition("1 day", 1, TimeUnit.DAYS);
		
		List<FrequencyCounterDefinition> defs = new LinkedList<FrequencyCounterDefinition>();
		defs.add(d1s);
		defs.add(d1m);
		defs.add(d5m);
		defs.add(d10m);
		defs.add(d1h);
		defs.add(d1d);
		
		return new PackagedFrequencyCounter(defs);
	}

	@Test
	public void generateEvent() throws InterruptedException{
		PackagedFrequencyCounter counter = createPackagedCounter();
		
		Thread[] threads = new Thread[NUMBER_OF_THREADS];
		
		long start = System.currentTimeMillis();
		for (int i = 0; i < NUMBER_OF_THREADS; i ++){
			threads[i] = startEventGenerator(counter);
		}
		Thread.sleep(TimeUnit.MILLISECONDS.convert(NUMBER_OF_SECONDS, TimeUnit.SECONDS));
		for (Thread thread: threads){
			thread.interrupt();
		}
		long finish = System.currentTimeMillis();

		BigInteger total  = counter.getCounter("1 second").getTotalCounts();
		
		System.out.println(counter);
		System.out.println();
		System.out.println("Duration: \t" + DurationFormatter.format(finish - start));
		System.out.println("Total: \t" + total);
		System.out.println("Performance: \t" 
				+ total.divide(
						BigInteger.valueOf((finish - start)/1000)) + "/s");
	}
	
	protected Thread startEventGenerator(final FrequencyCounter counter){
		Thread thread = new Thread(){
			@Override
			public void run(){
				while(!Thread.interrupted()){
					long t = System.currentTimeMillis();
					counter.count();
					counter.count();
					counter.count(t + 1111);
					counter.count(t - 1111);
					counter.count(t + 61111);
					counter.count(t + 761111);
					counter.count(t - 961111);
					counter.count(t + 12961111);
					counter.count(t + 15661111);
					counter.count(t - 135661111);
					counter.count(t + 235614611);
					counter.count(t + 256514611);
					counter.count(t + 335614611);
					counter.count();
				}
			}
		};
		thread.start();
		return thread;
	}
	

}
