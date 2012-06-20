package net.sf.jabb.util.col.test;

import java.util.TreeSet;

import junit.framework.Assert;
import net.sf.jabb.util.col.LongArray;

import org.junit.Test;


public class LongArrayTest {
	@Test
	public void single(){
		LongArray l1 = new LongArray(3);
		LongArray l2 = new LongArray(4);
		LongArray l3 = new LongArray(3);
		
		Assert.assertTrue(l1.compareTo(l2) == -1);
		Assert.assertTrue(l1.compareTo(l3) == 0);
		Assert.assertTrue(l1.equals(l3));
		Assert.assertTrue(l3.equals(l1));
		Assert.assertTrue(l2.compareTo(l3) == 1);
	}
	
	@Test
	public void multi(){
		LongArray l1 = new LongArray(3,4,5,Long.MAX_VALUE);
		LongArray l2 = new LongArray(4,4,5,6);
		LongArray l3 = new LongArray(3,4,5,Long.MAX_VALUE);
		LongArray l4 = new LongArray(3,4,6,Long.MAX_VALUE);
		LongArray l5 = new LongArray(3,5,5,Long.MAX_VALUE);
		LongArray l6 = new LongArray(3,4,5,-1);
		LongArray l7 = new LongArray(3,4,5,0);
		LongArray l8 = new LongArray(3,5,5,Long.MAX_VALUE-100);
		
		Assert.assertTrue(l1.compareTo(l2) == -1);
		Assert.assertTrue(l1.compareTo(l3) == 0);
		Assert.assertTrue(l1.equals(l3));
		Assert.assertTrue(l3.equals(l1));
		Assert.assertTrue(l2.compareTo(l3) == 1);
		
		Assert.assertTrue(l1.compareTo(l6) == 1);
		Assert.assertTrue(l6.compareTo(l1) == -1);
		Assert.assertTrue(l3.compareTo(l4) == -1);
		Assert.assertTrue(l5.compareTo(l4) == 1);
		
		System.out.println(l1.getValue(3));
		System.out.println(l1.getIntValue(3));
		
		System.out.println(l1.hashCode());
		System.out.println(l2.hashCode());
		System.out.println(l3.hashCode());
		System.out.println(l4.hashCode());
		System.out.println(l5.hashCode());
		System.out.println(l6.hashCode());
		System.out.println(l7.hashCode());
		System.out.println(l8.hashCode());
		
		TreeSet<LongArray> s = new TreeSet<LongArray>();
		s.add(l1);
		s.add(l2);
		s.add(l3);
		s.add(l4);
		s.add(l5);
		s.add(l6);
		s.add(l7);
		s.add(l8);
		System.out.println("****************");
		System.out.println(s);
	}

}
