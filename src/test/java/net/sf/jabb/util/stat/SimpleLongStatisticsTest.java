/**
 * 
 */
package net.sf.jabb.util.stat;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

/**
 * @author James Hu
 *
 */
public class SimpleLongStatisticsTest {

	@Test
	public void testAvg() {
		SimpleLongStatistics s = new SimpleLongStatistics(100L, 100L, 1L, 1L);
		
		assertEquals((double)1, s.getAvg().doubleValue(), 0.000001);
		
		s.reset(3L, 100L, -100L, 100L);
		assertEquals((double)33.33333333, s.getAvg().doubleValue(), 0.0001);
		assertEquals(33, s.getAvg(20).toBigInteger().intValue());
		assertEquals(23, s.getAvg(20).toString().length());
	}

	@Test
	public void testMerge(){
		SimpleLongStatistics s0 = new SimpleLongStatistics();
		s0.evaluate(-1);
		s0.evaluate(2);
		SimpleLongStatistics s = new SimpleLongStatistics();
		s.merge(s0);
		assertEquals(-1, s.getMin().intValue());
		assertEquals(2, s.getMax().intValue());
		assertEquals(1, s.getSum().intValue());
		assertEquals(2L, s.getCount());
	}
}
