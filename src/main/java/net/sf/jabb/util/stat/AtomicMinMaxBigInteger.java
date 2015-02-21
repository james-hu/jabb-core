/**
 * 
 */
package net.sf.jabb.util.stat;

import java.io.Serializable;
import java.math.BigInteger;

/**
 * Holder of the minimum and maximum BigInteger values.
 * @author James Hu
 *
 */
public class AtomicMinMaxBigInteger implements Serializable{
	private static final long serialVersionUID = 8080025480251400931L;

	static final BigInteger MAX_LONG_VALUE = BigInteger.valueOf(Long.MAX_VALUE);
	static final BigInteger MIN_LONG_VALUE = BigInteger.valueOf(Long.MIN_VALUE);

	AtomicBigInteger minRef;
	AtomicBigInteger maxRef;
	
	public AtomicMinMaxBigInteger(){
	}
	
	public AtomicMinMaxBigInteger(BigInteger min, BigInteger max){
		reset(min, max);
	}
	
	/**
	 * Compare current minimum and maximum values with a new value and update the minimum and/or 
	 * maximum values if needed. If previously both minimum and maximum values are set set, both
	 * of them will be set to the input value.
	 * @param x the new value to be compared
	 */
	public void minMax(BigInteger x){
		if (minRef == null){
			minRef = new AtomicBigInteger(x);
			maxRef = new AtomicBigInteger(x);
			return;
		}

		BigInteger min = minRef.get();
		int c = min.compareTo(x);
		if (c == 0){
			return;
		}else if (c < 0){
			BigInteger max;
			do {
				max = maxRef.get();
			} while (max.compareTo(x) < 0 && !maxRef.compareAndSet(max, x));
		}else{ // c > 0
			while (c > 0 && !minRef.compareAndSet(min, x)){
				min = minRef.get();
				c = min.compareTo(x);
			}
		}
	}
	
	
	public void reset(){
		minRef = null;
		maxRef = null;
	}
	
	public void reset(BigInteger min, BigInteger max){
		if (min.compareTo(max) > 0){
			throw new IllegalArgumentException("min value must not be greater than max value");
		}
		minRef = new AtomicBigInteger(min);
		maxRef = new AtomicBigInteger(max);
	}
	

	/**
	 * Merge the min/max value from another instance into this one.
	 * @param another   another instance of AtomicMinMaxLong
	 */
	public void merge(AtomicMinMaxBigInteger another){
		BigInteger anotherMin = another.getMin();
		if (anotherMin != null){
			minMax(anotherMin);
		}
		BigInteger anotherMax = another.getMax();
		if (anotherMax != null){
			minMax(anotherMax);
		}
	}
	
	public BigInteger getMin(){
		return minRef == null ? null : minRef.get();
	}
	
	public BigInteger getMax(){
		return maxRef == null ? null : maxRef.get();
	}
	
	@Override
	public String toString(){
		return "(" + getMin() + ", " + getMax() + ")";
	}

}
