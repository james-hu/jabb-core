/**
 * 
 */
package net.sf.jabb.util.stat;

import static org.junit.Assert.assertEquals;

import java.math.BigInteger;

import org.junit.Test;

/**
 * @author James Hu
 *
 */
public class ImmutableNumberStatisticsTest {

	@Test
	public void testAvg() {
		ImmutableNumberStatistics<BigInteger> s = new ImmutableNumberStatistics<BigInteger>(100L, BigInteger.valueOf(100L), BigInteger.ONE, BigInteger.ONE);
		
		assertEquals((double)1, s.getAvg().doubleValue(), 0.000001);
		
		s = new ImmutableNumberStatistics<BigInteger>(3L, BigInteger.valueOf(100L), BigInteger.valueOf(-100), BigInteger.valueOf(100));
		assertEquals((double)33.33333333, s.getAvg().doubleValue(), 0.0001);
		assertEquals(33, s.getAvg(20).toBigInteger().intValue());
		assertEquals(23, s.getAvg(20).toString().length());
	}

}
