/**
 * 
 */
package net.sf.jabb.util.stat;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Random;

import com.google.common.base.Preconditions;

/**
 * Number generator can generate random numbers.
 * @author James Hu
 *
 */
public class NumberGenerator {
	static protected final BigInteger LONG_RANGE = BigInteger.valueOf(Long.MAX_VALUE).subtract(BigInteger.valueOf(Long.MIN_VALUE));
	
	/**
	 * Generate random long values
	 * @param start	the minimum value of the generated numbers, inclusive
	 * @param end  the maximum value of the generated numbers, inclusive
	 * @param size  number of random numbers to return. a.k.a. the size of the returned array.
	 * @return  the random values in an array. The values are guaranteed to be within [start, end] range.
	 */
	static public long[] randomLongs(long start, long end, int size){
		Preconditions.checkArgument(start < end, "Start must be less than end.");
		
		Random random = new Random();
		random.setSeed(System.currentTimeMillis());
		long[] result = new long[size];
		for (int i = 0; i < size; i ++){
			long l = random.nextLong();
			double d = (double)(end - start) / (Long.MAX_VALUE - Long.MIN_VALUE) * l + start;
			if (d <= start){
				l = start;
			}else if (d >= end){
				l = end;
			}else{
				l = (long)d;
			}
			result[i] = l;
		}
		return result;
	}

	/**
	 * Generate random BigInteger values in double range
	 * @param start	the minimum value of the generated numbers, inclusive
	 * @param end  the maximum value of the generated numbers, inclusive
	 * @param size  number of random numbers to return. a.k.a. the size of the returned array.
	 * @return  the random values in an array. The values are not guaranteed to be exactly 
	 * within [start, end] range because double type can not always represent numbers exactly.
	 */
	static public BigInteger[] randomBigIntegers(double start, double end, int size){
		Preconditions.checkArgument(start < end, "Start must be less than end.");
		
		Random random = new Random();
		random.setSeed(System.currentTimeMillis());
		BigInteger[] result = new BigInteger[size];
		for (int i = 0; i < size; i ++){
			double l = random.nextDouble();
			double d = (double)(end - start) * l + start;
			BigInteger b = new BigDecimal(d).toBigInteger();
			result[i] = b;
		}
		return result;
	}

}
