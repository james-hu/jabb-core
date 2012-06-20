package net.sf.jabb.util.stat.test;


import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import net.sf.jabb.util.stat.FrequencyCounterDefinition;
import net.sf.jabb.util.stat.PackagedFrequencyCounter;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class CounterTest {
	static PackagedFrequencyCounter counter;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		FrequencyCounterDefinition d4 = 
			new FrequencyCounterDefinition("5s", 2, TimeUnit.SECONDS, 10, TimeUnit.MINUTES);
		FrequencyCounterDefinition d1 = 
			new FrequencyCounterDefinition("10S", 10, TimeUnit.SECONDS, 50, TimeUnit.MINUTES);
		FrequencyCounterDefinition d2 = 
			new FrequencyCounterDefinition("30S", 30, TimeUnit.SECONDS, 3, TimeUnit.HOURS);
		FrequencyCounterDefinition d3 = 
			new FrequencyCounterDefinition("1M", 1, TimeUnit.MINUTES, 7, TimeUnit.HOURS);
		List<FrequencyCounterDefinition> l = new LinkedList<FrequencyCounterDefinition>();
		l.add(d4);
		l.add(d1);
		l.add(d2);
		l.add(d3);
		
		counter = new PackagedFrequencyCounter(l);
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
		System.out.println(counter);
	}
	
	@Test
	public void generateEvent() throws InterruptedException{
//		for (long i = 0; i < 10 * 3600 * 10 ; i ++){
		for (long i = 0; i < 200 ; i ++){
			Thread.sleep(100);
			counter.count();
			if (i % 3 == 0){
				counter.count();
			}
			if (i % 16 == 0){
				counter.count();
			}
		}
	}
	

}
