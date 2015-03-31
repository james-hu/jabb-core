/**
 * 
 */
package net.sf.jabb.util.stat;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.BigInteger;

import org.apache.commons.lang3.Validate;
import org.apache.commons.lang3.builder.EqualsBuilder;

/**
 * Immutable statistics holder.
 * @author James Hu
 *
 */
public class ImmutableNumberStatistics<T extends Number> implements NumberStatistics<T>, Serializable{
	private static final long serialVersionUID = -7258668267927497635L;
	
	protected long count;
	protected T sum;
	protected T min;
	protected T max;
	
	/**
	 * Create an immutable copy of another NumberStatistics
	 * @param statistics  the original object
	 * @return 	the immutable copy
	 */
	public static <T extends Number> ImmutableNumberStatistics<T> copyOf(NumberStatistics<? extends T> statistics){
		return new ImmutableNumberStatistics<T>(statistics);
	}
	
	public ImmutableNumberStatistics(long count, T sum, T min, T max){
		Validate.notNull(sum);
		this.count = count;
		this.sum = sum;
		this.min = min;
		this.max = max;
	}

	public ImmutableNumberStatistics(T count, T sum, T min, T max){
		this(count.longValue(), sum, min, max);
	}
	
	public ImmutableNumberStatistics(NumberStatistics<? extends T> statistics){
		this(statistics.getCount(), statistics.getSum(), 
				statistics.getMin(), statistics.getMax());
	}
	
	@Override
	public boolean equals(Object other){
		if (other == this){
			return true;
		}
		if (other == null || !(other instanceof NumberStatistics<?>)){
			return false;
		}
		NumberStatistics<?> that = (NumberStatistics<?>) other;
		return new EqualsBuilder()
			.append(this.count, that.getCount())
			.append(this.sum, that.getSum())
			.append(this.min, that.getMin())
			.append(this.max, that.getMax())
			.isEquals();
	}


	@Override
	public String toString(){
		return "(" + count + ", " + sum + ", " + min + "/" + max + ")";
	}

	@Override
	public void merge(NumberStatistics<? extends Number> other) {
		throw new UnsupportedOperationException("This object is immutable");
	}

	@Override
	public void evaluate(int value) {
		throw new UnsupportedOperationException("This object is immutable");
	}

	@Override
	public void evaluate(long value) {
		throw new UnsupportedOperationException("This object is immutable");
	}

	@Override
	public void evaluate(BigInteger value) {
		throw new UnsupportedOperationException("This object is immutable");
	}

	@Override
	public Double getAvg() {
		BigDecimal avg = getAvg(30);
		return avg == null? null : avg.doubleValue();
	}

	@Override
	public BigDecimal getAvg(int scale) {
		if (count > 0 && sum != null){
			BigDecimal avg;
			Class<?> sumClass = sum.getClass();
			if (sumClass == BigInteger.class ){
				avg = new BigDecimal((BigInteger)sum, scale);
			}else if (sumClass == Long.class || sumClass == Integer.class){
				avg = BigDecimal.valueOf(sum.longValue());
			}else if (sumClass == BigDecimal.class){
				avg = (BigDecimal)sum;
			}else{
				avg = BigDecimal.valueOf(sum.doubleValue());
			}
			return avg.divide(new BigDecimal(count), BigDecimal.ROUND_HALF_UP);
		}else{
			return null;
		}
	}

	@Override
	public T getMin() {
		return min;
	}

	@Override
	public T getMax() {
		return max;
	}

	@Override
	public T getSum() {
		return sum;
	}

	@Override
	public long getCount() {
		return count;
	}

	@Override
	public void reset() {
		this.count = 0L;
		this.sum = null;
		this.min = null;
		this.max = null;
	}

	@Override
	public void reset(T newCount, T newSum, T newMin, T newMax) {
		Validate.notNull(newCount);
		this.count = newCount.longValue();
		this.sum = newSum;
		this.min = newMin;
		this.max = newMax;
	}

}
