package net.sf.jabb.util.misctest;


import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import net.sf.jabb.util.text.DurationFormatter;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class MiscTest {

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
	public void dateTimeFormatTest(){
		DateFormat df = new SimpleDateFormat("HH:mm:ss.SSS");
		df.setTimeZone(TimeZone.getTimeZone("GMT"));
		System.out.println(df.format(1700000066L));
		System.out.println(df.format(66L));
		System.out.println(df.format(1000L*3600*24));
		
		long t = System.currentTimeMillis();
		System.out.println(DurationFormatter.format(1700000066L));
		System.out.println(DurationFormatter.format(66L));
		System.out.println(DurationFormatter.format(1000L*3600*24));
		System.out.println(DurationFormatter.formatSince(t));
	}
	
	@Test
	public void maxDateTest(){
		Date d;
		
		d = new Date(Long.MAX_VALUE);
		System.out.println(d);
		d = new Date(Long.MAX_VALUE/100000);
		System.out.println(d);
		
		System.out.println(Long.MAX_VALUE);
		
		
	}
	
	@Test 
	public void hashTest(){
		long[] a = new long[] {1,2,3,4};
		long[] b = new long[] {5,6,7,8};
		long[] c = new long[] {1,2,3,4};
		System.out.format("hash codes are %d, %d, %d\n", a.hashCode(), b.hashCode(), c.hashCode());

		Object[] x1 = new Object[] {1L, 2L, 3L, 4L};
		Object[] x2 = new Object[] {1L, 2L, 3L, "this is mine"};
		Object[] x3 = new Object[] {1L, 2L, "news", "this is mine"};
		System.out.format("hash codes are %d, %d, %d\n", x1.hashCode(), x2.hashCode(), x3.hashCode());
		
		HashMap<Object[], String> hm = new HashMap<Object[], String>();
		hm.put(x2, "Okay");
		System.out.println(hm.get(new Object[] {1L, 2L, 3L, "this is mine"}));
		System.out.println(x2.equals(new Object[] {1L, 2L, 3L, "this is mine"}));

		Map<List<Object>, String> lhm = new HashMap<List<Object>, String>();
		List<Object> la = new LinkedList<Object>();
		List<Object> lb = new LinkedList<Object>();
		la.add(1L);
		la.add(2L);
		la.add(3L);
		la.add("try me");
		lb.add(1L);
		lb.add(2L);
		lb.add(3L);
		lb.add("try me");
		lhm.put(la, "Okay");
		System.out.println(lhm.get(lb));
		System.out.println(la.equals(lb));

	}
	
	@Test
	public void splitTest(){
		for (String s: "abcd,\nzz,dd,eee,dsf\n,".split("[,\n\r]+")){
			System.out.println(s);
		}
	}
	
	@Test
	public void finallyTest(){
		System.out.println(finallyTestFunc(false));
		System.out.println(finallyTestFunc(true));
	}
	
	public int finallyTestFunc(boolean f){
		int i = 0;
		i = 5;
		try{
			for (; i<100; i++){}
		}finally{
			if (f){
				i = -1;
			}
		}
		return i;
	}

}
