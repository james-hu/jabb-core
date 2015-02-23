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
public class AtomicMinMaxBigInteger implements Serializable, MinMaxBigInteger{
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
	
	/* (non-Javadoc)
	 * @see net.sf.jabb.util.stat.MinMaxBigInteger#minMax(java.math.BigInteger)
	 */
	@Override
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
	
	@Override
	public void minMax(long x) {
		minMax(BigInteger.valueOf(x));
	}

	
	/* (non-Javadoc)
	 * @see net.sf.jabb.util.stat.MinMaxBigInteger#reset()
	 */
	@Override
	public void reset(){
		minRef = null;
		maxRef = null;
	}
	
	/* (non-Javadoc)
	 * @see net.sf.jabb.util.stat.MinMaxBigInteger#reset(java.math.BigInteger, java.math.BigInteger)
	 */
	@Override
	public void reset(BigInteger min, BigInteger max){
		if (min.compareTo(max) > 0){
			throw new IllegalArgumentException("min value must not be greater than max value");
		}
		minRef = new AtomicBigInteger(min);
		maxRef = new AtomicBigInteger(max);
	}
	
	@Override
	public void reset(long min, long max) {
		reset(BigInteger.valueOf(min), BigInteger.valueOf(max));
	}



	/**
	 * Merge the min/max value from another instance into this one.
	 * @param another   another instance of AtomicMinMaxLong
	 */
	@Override
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
	
	@Override
	public void merge(AtomicMinMaxLong another) {
		Long anotherMin = another.getMin();
		if (anotherMin != null){
			minMax(anotherMin);
		}
		Long anotherMax = another.getMax();
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

	@Override
	public Long getLongMin() {
		return minRef == null ? null : minRef.get().longValue();
	}

	@Override
	public Long getLongMax() {
		return maxRef == null ? null : maxRef.get().longValue();
	}

	@Override
	public BigInteger getBigIntegerMin() {
		return minRef == null ? null : minRef.get();
	}

	@Override
	public BigInteger getBigIntegerMax() {
		return maxRef == null ? null : maxRef.get();
	}


}
