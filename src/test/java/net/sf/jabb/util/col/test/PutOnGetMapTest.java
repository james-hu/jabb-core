package net.sf.jabb.util.col.test;


import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import net.sf.jabb.util.col.PutOnGetMap;
import net.sf.jabb.util.stat.BasicFrequencyCounter;
import net.sf.jabb.util.stat.FrequencyCounterDefinition;
import net.sf.jabb.util.stat.PackagedFrequencyCounter;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class PutOnGetMapTest {

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

	@Test
	public void createInstance(){
		PutOnGetMap<Long, String> map = new PutOnGetMap<Long, String>(HashMap.class, String.class);
		for (int i = 0; i < 10; i ++){
			System.out.println(i + " - '" + map.get(i) + "'");
		}
	}
	
	@Test
	public void createInstance2(){
		PutOnGetMap<Long, AtomicInteger> map = new PutOnGetMap<Long, AtomicInteger>(TreeMap.class, AtomicInteger.class);
		for (int i = 0; i < 10; i ++){
			System.out.println(i + " - '" + map.get(i) + "'");
		}
	}
	
	
	@Test
	public void createInstance4(){
		PutOnGetMap<Long, AtomicInteger> map = new PutOnGetMap<Long, AtomicInteger>(TreeMap.class, AtomicInteger.class);
		for (int i = 0; i < 10; i ++){
			System.out.println(i + " - '" + map.get(i).addAndGet(4) + "'");
		}
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void createInstance5(){
		PutOnGetMap<Long, AtomicInteger> map = new PutOnGetMap<Long, AtomicInteger>(Map.class, AtomicInteger.class);
		for (int i = 0; i < 10; i ++){
			System.out.println(i + " - '" + map.get(i).addAndGet(5) + "'");
		}
	}
	
	@Test
	public void createInstance6(){
		PutOnGetMap<Long, AtomicInteger> map = new PutOnGetMap<Long, AtomicInteger>(ConcurrentSkipListMap.class, AtomicInteger.class);
		for (int i = 0; i < 10; i ++){
			System.out.println(i + " - '" + map.get(i).addAndGet(6) + "'");
		}
	}
	
	@Test
	public void withParam(){
		FrequencyCounterDefinition def = new FrequencyCounterDefinition("me", 10, TimeUnit.MINUTES, 1, TimeUnit.HOURS);
		PutOnGetMap<Integer, BasicFrequencyCounter> map = 
			new PutOnGetMap<Integer, BasicFrequencyCounter>(HashMap.class, BasicFrequencyCounter.class, def);
		for (int i = 0; i < 3; i ++){
			System.out.println(i + " - '" + map.get(i) + "'");
		}
	}
	
	@Test
	public void withParam2(){
		//FrequencyCounterDefinition def = new FrequencyCounterDefinition("me", 10, TimeUnit.MINUTES, 1, TimeUnit.HOURS);
		PutOnGetMap<Integer, PackagedFrequencyCounter> map = 
			new PutOnGetMap<Integer, PackagedFrequencyCounter>(HashMap.class, PackagedFrequencyCounter.class,
				(Object[]) new FrequencyCounterDefinition[] {
						new FrequencyCounterDefinition("me", 10, TimeUnit.MINUTES, 1, TimeUnit.HOURS),
						new FrequencyCounterDefinition("him", 30, TimeUnit.MINUTES, 1, TimeUnit.HOURS),
						new FrequencyCounterDefinition("her", 45, TimeUnit.MINUTES, 2, TimeUnit.HOURS),
						});
		for (int i = 0; i < 3; i ++){
			System.out.println(i + " - '" + map.get(i) + "'");
		}
	}
}