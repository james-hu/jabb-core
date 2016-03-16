/**
 * 
 */
package net.sf.jabb.util.stat;

import static org.junit.Assert.*;

import java.math.BigInteger;

import org.junit.Test;

/**
 * @author James Hu
 *
 */
public class SimpleBigIntegerStatisticsTest {

	@Test
	public void testAvg() {
		SimpleBigIntegerStatistics s = new SimpleBigIntegerStatistics(100L, BigInteger.valueOf(100L), BigInteger.ONE, BigInteger.ONE);
		
		assertEquals((double)1, s.getAvg().doubleValue(), 0.000001);
		
		s.reset(3L, BigInteger.valueOf(100L), BigInteger.valueOf(-100), BigInteger.valueOf(100));
		assertEquals((double)33.33333333, s.getAvg().doubleValue(), 0.0001);
		assertEquals(33, s.getAvg(20).toBigInteger().intValue());
		assertEquals(23, s.getAvg(20).toString().length());
	}

	@Test
	public void testMerge(){
		SimpleBigIntegerStatistics s0 = new SimpleBigIntegerStatistics();
		s0.evaluate(-1);
		s0.evaluate(2);
		SimpleBigIntegerStatistics s = new SimpleBigIntegerStatistics();
		s.merge(s0);
		assertEquals(-1, s.getMin().intValue());
		assertEquals(2, s.getMax().intValue());
		assertEquals(1, s.getSum().intValue());
		assertEquals(2L, s.getCount());
	}
}
