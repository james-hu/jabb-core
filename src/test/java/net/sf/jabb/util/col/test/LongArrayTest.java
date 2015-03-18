package net.sf.jabb.util.col.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.TreeSet;

import junit.framework.Assert;
import net.sf.jabb.util.col.LongArray;

import org.junit.Test;

import com.google.common.collect.Iterables;


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
		
		assertEquals(9223372036854775807l, l1.getValue(3));
		assertEquals(-1, l1.getIntValue(3));
		
		assertEquals(-2146548937, l1.hashCode());
		assertEquals(939630, l2.hashCode());
		assertEquals(-2146548937, l3.hashCode());
		assertEquals(-2146548920, l4.hashCode());
		assertEquals(-2146548648, l5.hashCode());
		assertEquals(934711, l6.hashCode());
		assertEquals(934711, l7.hashCode());
		assertEquals(-2146548548, l8.hashCode());
		
		TreeSet<LongArray> s = new TreeSet<LongArray>();
		s.add(l1);
		s.add(l2);
		s.add(l3);
		s.add(l4);
		s.add(l5);
		s.add(l6);
		s.add(l7);
		s.add(l8);
		assertTrue(Iterables.elementsEqual(Arrays.asList(new LongArray[]{l6, l7, l1, l4, l8, l5, l2}), s));
	}

}
