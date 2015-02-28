/**
 * 
 */
package net.sf.jabb.util.stat;

import static org.junit.Assert.*;
import net.sf.jabb.util.stat.TimePeriod;

import org.junit.Test;

/**
 * @author James Hu
 *
 */
public class TimePeriodTest {

	@Test
	public void testIsDivisorOf() {
		assertIsDivisorOf("3 day", "1 year", false);
		assertIsDivisorOf("3 days", "3 years", true);
		assertIsDivisorOf("1 months", "1 year", true);
		assertIsDivisorOf("2 months", "1 year", true);
		assertIsDivisorOf("3 months", "1 year", true);
		assertIsDivisorOf("4 months", "1 year", true);
		assertIsDivisorOf("5 months", "1 year", false);
		assertIsDivisorOf("6 months", "1 year", true);
		assertIsDivisorOf("15 minutes", "1 hour", true);
		assertIsDivisorOf("16 minutes", "1 hour", false);
		assertIsDivisorOf("30 minutes", "1 week", true);
		assertIsDivisorOf("40 minutes", "2 hour", true);
		assertIsDivisorOf("40 minutes", "1 week", true);
		assertIsDivisorOf("1000 milliseconds", "1 second", true);
		assertIsDivisorOf("1000 milliseconds", "2 second", true);
		assertIsDivisorOf("2000 milliseconds", "1 second", false);
	}
	protected void assertIsDivisorOf(String divisor, String divident, boolean expectedResult){
		assertIsDivisorOf(TimePeriod.of(divisor), TimePeriod.of(divident), expectedResult);
	}
	
	protected void assertIsDivisorOf(TimePeriod divisor, TimePeriod divident, boolean expectedResult){
		boolean result = divisor.isDivisorOf(divident);
		assertEquals("" + divisor + " is" + (expectedResult? "" : " not") + " divisor of " + divident, 
				expectedResult, result);
	}

}
