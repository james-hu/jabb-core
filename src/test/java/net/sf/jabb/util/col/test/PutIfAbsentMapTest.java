package net.sf.jabb.util.col.test;


import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import net.sf.jabb.util.col.PutIfAbsentMap;
import net.sf.jabb.util.stat.BasicFrequencyCounter;
import net.sf.jabb.util.stat.FrequencyCounterDefinition;
import net.sf.jabb.util.stat.PackagedFrequencyCounter;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class PutIfAbsentMapTest {

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
		PutIfAbsentMap<Long, String> map = new PutIfAbsentMap<Long, String>(HashMap.class, String.class);
		for (int i = 0; i < 10; i ++){
			assertEquals("", map.get(i));
		}
	}
	
	@Test
	public void createInstance2(){
		PutIfAbsentMap<Long, AtomicInteger> map = new PutIfAbsentMap<Long, AtomicInteger>(TreeMap.class, AtomicInteger.class);
		for (int i = 0; i < 10; i ++){
			assertEquals(0, map.get(i).get());
		}
	}
	
	
	@Test
	public void createInstance4(){
		PutIfAbsentMap<Long, AtomicInteger> map = new PutIfAbsentMap<Long, AtomicInteger>(TreeMap.class, AtomicInteger.class);
		for (int i = 0; i < 10; i ++){
			assertEquals(4, map.get(i).addAndGet(4));
		}
	}
	
	@Test(expected=RuntimeException.class)
	public void createInstance5(){
		PutIfAbsentMap<Long, AtomicInteger> map = new PutIfAbsentMap<Long, AtomicInteger>(Map.class, AtomicInteger.class);
		for (int i = 0; i < 10; i ++){
			assertEquals(5, map.get(i).addAndGet(5));
		}
	}
	
	@Test
	public void createInstance6(){
		PutIfAbsentMap<Long, AtomicInteger> map = new PutIfAbsentMap<Long, AtomicInteger>(ConcurrentSkipListMap.class, AtomicInteger.class);
		for (int i = 0; i < 10; i ++){
			assertEquals(6, map.get(i).addAndGet(6));
		}
	}
	
	@Test
	public void withParam(){
		FrequencyCounterDefinition def = new FrequencyCounterDefinition("me", 10, TimeUnit.MINUTES, 1, TimeUnit.HOURS);
		PutIfAbsentMap<Integer, BasicFrequencyCounter> map = 
			new PutIfAbsentMap<Integer, BasicFrequencyCounter>(HashMap.class, BasicFrequencyCounter.class, def);
		for (int i = 0; i < 3; i ++){
			assertTrue(map.get(i).toString().contains("granularity=600000(ms)"));
			assertTrue(map.get(i).toString().contains("purgeBefore=3600000(ms)"));
		}
	}
	
	@Test
	public void withParam2(){
		//FrequencyCounterDefinition def = new FrequencyCounterDefinition("me", 10, TimeUnit.MINUTES, 1, TimeUnit.HOURS);
		PutIfAbsentMap<Integer, PackagedFrequencyCounter> map = 
			new PutIfAbsentMap<Integer, PackagedFrequencyCounter>(HashMap.class, PackagedFrequencyCounter.class,
				(Object[]) new FrequencyCounterDefinition[] {
						new FrequencyCounterDefinition("me", 10, TimeUnit.MINUTES, 1, TimeUnit.HOURS),
						new FrequencyCounterDefinition("him", 30, TimeUnit.MINUTES, 1, TimeUnit.HOURS),
						new FrequencyCounterDefinition("her", 45, TimeUnit.MINUTES, 2, TimeUnit.HOURS),
						});
		for (int i = 0; i < 3; i ++){
			assertTrue(map.get(i).toString().contains("me"));
			assertTrue(map.get(i).toString().contains("him"));
			assertTrue(map.get(i).toString().contains("her"));
		}
	}
}
