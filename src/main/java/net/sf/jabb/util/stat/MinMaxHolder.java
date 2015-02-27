package net.sf.jabb.util.stat;

import java.math.BigDecimal;
import java.math.BigInteger;

/**
 * Holder of the minimum and maximum values
 * @author James Hu
 *
 */
public interface MinMaxHolder {

	/**
	 * Compare current minimum and maximum values with a new value and update the minimum and/or 
	 * maximum values if needed. If previously both minimum and maximum values have not been set, both
	 * of them will be set to the new value.
	 * @param x the new value to be evaluated
	 */
	public void evaluate(long x);

	/**
	 * Compare current minimum and maximum values with a new value and update the minimum and/or 
	 * maximum values if needed. If previously both minimum and maximum values have not been set, both
	 * of them will be set to the new value.
	 * @param x the new value to be evaluated
	 */
	public void evaluate(Long x);

	/**
	 * Compare current minimum and maximum values with a new value and update the minimum and/or 
	 * maximum values if needed. If previously both minimum and maximum values have not been set, both
	 * of them will be set to the new value.
	 * @param x the new value to be evaluated
	 */
	public void evaluate(float x);

	/**
	 * Compare current minimum and maximum values with a new value and update the minimum and/or 
	 * maximum values if needed. If previously both minimum and maximum values have not been set, both
	 * of them will be set to the new value.
	 * @param x the new value to be evaluated
	 */
	public void evaluate(Float x);

	/**
	 * Compare current minimum and maximum values with a new value and update the minimum and/or 
	 * maximum values if needed. If previously both minimum and maximum values have not been set, both
	 * of them will be set to the new value.
	 * @param x the new value to be evaluated
	 */
	public void evaluate(double x);

	/**
	 * Compare current minimum and maximum values with a new value and update the minimum and/or 
	 * maximum values if needed. If previously both minimum and maximum values have not been set, both
	 * of them will be set to the new value.
	 * @param x the new value to be evaluated
	 */
	public void evaluate(Double x);

	/**
	 * Compare current minimum and maximum values with a new value and update the minimum and/or 
	 * maximum values if needed. If previously both minimum and maximum values have not been set, both
	 * of them will be set to the new value.
	 * @param x the new value to be evaluated
	 */
	public void evaluate(BigInteger x);

	/**
	 * Compare current minimum and maximum values with a new value and update the minimum and/or 
	 * maximum values if needed. If previously both minimum and maximum values have not been set, both
	 * of them will be set to the new value.
	 * @param x the new value to be evaluated
	 */
	public void evaluate(BigDecimal x);

	/**
	 * Compare current minimum and maximum values with a new value and update the minimum and/or 
	 * maximum values if needed. If previously both minimum and maximum values have not been set, both
	 * of them will be set to the new value.
	 * @param x the new value to be evaluated
	 */
	public void evaluate(Number x);

	/**
	 * Get the minimum value in Number type. No exception will be thrown if the type cannot hold the value without
	 * losing precision or correctness.
	 * @return	the minimum value. Can be null if no value has ever been evaluated.
	 */
	public Number getMin();

	/**
	 * Get the maximum value in Number type. No exception will be thrown if the type cannot hold the value without
	 * losing precision or correctness.
	 * @return	the maximum value. Can be null if no value has ever been evaluated.
	 */
	public Number getMax();

	/**
	 * Get the minimum value in Long type. No exception will be thrown if the type cannot hold the value without
	 * losing precision or correctness.
	 * @return	the minimum value. Can be null if no value has ever been evaluated.
	 */
	public Long getMinAsLong();

	/**
	 * Get the maximum value in Long type. No exception will be thrown if the type cannot hold the value without
	 * losing precision or correctness.
	 * @return	the maximum value. Can be null if no value has ever been evaluated.
	 */
	public Long getMaxAsLong();

	/**
	 * Get the minimum value in Float type. No exception will be thrown if the type cannot hold the value without
	 * losing precision or correctness.
	 * @return	the minimum value. Can be null if no value has ever been evaluated.
	 */
	public Float getMinAsFloat();

	/**
	 * Get the maximum value in Float type. No exception will be thrown if the type cannot hold the value without
	 * losing precision or correctness.
	 * @return	the maximum value. Can be null if no value has ever been evaluated.
	 */
	public Float getMaxAsFloat();

	/**
	 * Get the minimum value in Double type. No exception will be thrown if the type cannot hold the value without
	 * losing precision or correctness.
	 * @return	the minimum value. Can be null if no value has ever been evaluated.
	 */
	public Double getMinAsDouble();

	/**
	 * Get the maximum value in Double type. No exception will be thrown if the type cannot hold the value without
	 * losing precision or correctness.
	 * @return	the minimum value. Can be null if no value has ever been evaluated.
	 */
	public Double getMaxAsDouble();

	/**
	 * Get the minimum value in BigInteger type. No exception will be thrown if the type cannot hold the value without
	 * losing precision or correctness.
	 * @return	the minimum value. Can be null if no value has ever been evaluated.
	 */
	public BigInteger getMinAsBigInteger();

	/**
	 * Get the maximum value in BigInteger type. No exception will be thrown if the type cannot hold the value without
	 * losing precision or correctness.
	 * @return	the minimum value. Can be null if no value has ever been evaluated.
	 */
	public BigInteger getMaxAsBigInteger();

	/**
	 * Get the minimum value in BigDecimal type. No exception will be thrown if the type cannot hold the value without
	 * losing precision or correctness.
	 * @return	the minimum value. Can be null if no value has ever been evaluated.
	 */
	public BigDecimal getMinAsBigDecimal();

	/**
	 * Get the maximum value in BigDecimal type. No exception will be thrown if the type cannot hold the value without
	 * losing precision or correctness.
	 * @return	the minimum value. Can be null if no value has ever been evaluated.
	 */
	public BigDecimal getMaxAsBigDecimal();

	/**
	 * Reset to the initial status - no min/max values.
	 */
	public void reset();
	
	/**
	 * Reset and use the specified values as initial min/max values
	 * @param min	the initial minimum value
	 * @param max	the initial maximum value
	 */
	public void reset(Number min, Number max);
	
	/**
	 * Merge the min/max values from another holder to this one.
	 * @param another another holder
	 */
	public void merge(MinMaxHolder another);

}