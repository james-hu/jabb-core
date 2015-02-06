package net.sf.jabb.util.col.test;

import static org.junit.Assert.*;
import junit.framework.Assert;

import net.sf.jabb.util.col.ComparableArray;
import net.sf.jabb.util.col.LongArray;

import org.junit.Test;

public class ComparableArrayTest {

	@Test
	public void test() {
		LongArray l1 = new LongArray(3,4,5,Long.MAX_VALUE);
		LongArray l2 = new LongArray(4,4,5,6);
		LongArray l3 = new LongArray(3,4,5,Long.MAX_VALUE);
		LongArray l4 = new LongArray(3,4,6,Long.MAX_VALUE);
		LongArray l5 = new LongArray(3,5,5,Long.MAX_VALUE);
		LongArray l6 = new LongArray(3,4,5,-1);
		LongArray l7 = new LongArray(3,4,5,0);
		LongArray l8 = new LongArray(3,5,5,Long.MAX_VALUE-100);
		
		ComparableArray c1 = new ComparableArray(l1, "String 1", "String 2");
		ComparableArray c2 = new ComparableArray(l3, "String 1", "String 3");
		ComparableArray c3 = new ComparableArray(l3, "String 1", "String 2");
		
		Assert.assertTrue(c1.compareTo(c2) == -1);
		Assert.assertTrue(c1.compareTo(c3) == 0);
		Assert.assertTrue(c1.equals(c3));
		Assert.assertTrue(c3.equals(c1));
		Assert.assertTrue(c1.hashCode() == c3.hashCode());
		Assert.assertTrue(c2.compareTo(c3) == 1);
		
		ComparableArray c4 = new ComparableArray("new 1", l1, new Long(8));
		ComparableArray c5 = new ComparableArray("new 1", "String 1", "String 3");
		ComparableArray c6 = new ComparableArray("new 1", l1, new Long(9));
		
		Assert.assertTrue(c4.compareTo(c6) == -1);
		
	}
	
	@Test(expected = ClassCastException.class)
	public void testUncompatible(){
		ComparableArray c4 = new ComparableArray("new 1", new Long(0), new Long(8));
		ComparableArray c5 = new ComparableArray("new 1", "String 1", "String 3");
		Assert.assertFalse(c4.equals(c5));
	}

}
