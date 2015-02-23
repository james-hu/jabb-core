package net.sf.jabb.util.stat;

/**
 * Holder of the minimum and maximum long values
 * @author James Hu
 *
 */
public interface LongMinMaxHolder {

	/**
	 * Compare current minimum and maximum values with a new value and update the minimum and/or 
	 * maximum values if needed. If previously both minimum and maximum values have not been set, both
	 * of them will be set to the input value.
	 * @param x the new value to be compared
	 */
	public void minMax(long x);


	/**
	 * Get the minimum value in Long type
	 * @return	the long type presentation of the minimum value. Can be null if no value has ever been fed.
	 */
	public Long getLongMin();

	/**
	 * Get the maximum value in Long type
	 * @return  the long type presentation of the maximum value. Can be null if no value has ever been fed.
	 */
	public Long getLongMax();

	/**
	 * Reset to the initial status - no min/max values.
	 */
	public void reset();
	
	/**
	 * Reset and use the specified values as initial min/max values
	 * @param min	the initial minimum value
	 * @param max	the initial maximum value
	 */
	public void reset(long min, long max);
	
	/**
	 * Merge the min/max values from another instance to this one.
	 * @param another another instance
	 */
	public void merge(ConcurrentLongMinMaxHolder another);

}