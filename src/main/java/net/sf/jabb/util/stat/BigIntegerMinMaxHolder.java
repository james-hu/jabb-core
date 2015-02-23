package net.sf.jabb.util.stat;

import java.math.BigInteger;

/**
 * Holder of the minimum and maximum BigInteger values
 * @author James Hu
 *
 */
public interface BigIntegerMinMaxHolder extends LongMinMaxHolder{

	/**
	 * Compare current minimum and maximum values with a new value and update the minimum and/or 
	 * maximum values if needed. If previously both minimum and maximum values have not been set, both
	 * of them will be set to the input value.
	 * @param x the new value to be compared
	 */
	public void minMax(BigInteger x);

	/**
	 * Get current min value.
	 * @return	current min value or null if there is none.
	 */
	public BigInteger getBigIntegerMin();
	
	/**
	 * Get current max value.
	 * @return	current max value or null if there is none.
	 */
	public BigInteger getBigIntegerMax();

	/**
	 * Reset to specified min/max values.
	 * @param min	the value to be set as current min.
	 * @param max	the value to be set as current max.
	 */
	public void reset(BigInteger min, BigInteger max);
	
	/**
	 * Merge the min/max values from another instance to this one.
	 * @param another another instance
	 */
	public void merge(ConcurrentBigIntegerMinMaxHolder another);

}