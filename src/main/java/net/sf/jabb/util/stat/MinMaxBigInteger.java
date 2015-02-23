package net.sf.jabb.util.stat;

import java.math.BigInteger;

/**
 * Holder of the minimum and maximum long values
 * @author James Hu
 *
 */
public interface MinMaxBigInteger extends MinMaxLong{

	/**
	 * Compare current minimum and maximum values with a new value and update the minimum and/or 
	 * maximum values if needed. If previously both minimum and maximum values have not been set, both
	 * of them will be set to the input value.
	 * @param x the new value to be compared
	 */
	public void minMax(BigInteger x);

	public BigInteger getBigIntegerMin();
	public BigInteger getBigIntegerMax();

	public void reset(BigInteger min, BigInteger max);
	public void merge(AtomicMinMaxBigInteger another);

}