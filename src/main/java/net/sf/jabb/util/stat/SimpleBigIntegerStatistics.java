/**
 * 
 */
package net.sf.jabb.util.stat;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.BigInteger;

/**
 * A very simple and low foot print number statistics implementation that is not thread safe
 * @author James Hu
 *
 */
public class SimpleBigIntegerStatistics implements NumberStatistics<BigInteger>, Serializable {
	private static final long serialVersionUID = 8899260732756582695L;

	protected BigInteger min;
	protected BigInteger max;
	protected long count = 0;
	protected BigInteger sum = BigInteger.ZERO;
	
	protected void evaluateMinMax(BigInteger x){
		if (count <= 0){
			min = x;
			max = x;
		}else{
			if (x != null){
				if (x.compareTo(min) < 0){
					min = x;
				}else if (x.compareTo(max) > 0){
					max = x;
				}
			}
		}
	}

	public void mergeBigInteger(NumberStatistics<? extends BigInteger> other) {
		if (other != null){
			merge(other.getCount(), other.getSum(), other.getMin(), other.getMax());
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public void merge(NumberStatistics<? extends Number> other) {
		if (other != null && other.getCount() > 0){
			if (other.getSum() instanceof BigInteger){
				mergeBigInteger((NumberStatistics<? extends BigInteger>)other);
			}else{
				long otherCount = other.getCount();
				if (otherCount > 0){
					evaluateMinMax(BigInteger.valueOf(other.getMin().longValue()));
					evaluateMinMax(BigInteger.valueOf(other.getMax().longValue()));
					count += otherCount;
					sum = sum.add(BigInteger.valueOf(other.getSum().longValue()));
				}
			}
		}
	}

	@Override
	public void merge(long count, BigInteger sum, BigInteger min, BigInteger max) {
		if (min != null){
			evaluateMinMax(min);
		}
		if (max != null){
			evaluateMinMax(max);
		}
		this.count += count;
		this.sum = this.sum.add(sum);
	}

	@Override
	public void evaluate(int value) {
		evaluate(BigInteger.valueOf(value));
	}

	@Override
	public void evaluate(long value) {
		evaluate(BigInteger.valueOf(value));
	}

	@Override
	public void evaluate(BigInteger value) {
		if (value != null){
			evaluateMinMax(value);
			count ++;
			sum = sum.add(value);
		}
	}

	@Override
	public Double getAvg() {
		BigDecimal avg = getAvg(30);
		return avg == null? null : avg.doubleValue();
	}

	@Override
	public BigDecimal getAvg(int scale) {
		if (count > 0){
			BigDecimal avg = new BigDecimal(sum, scale);
			return avg.divide(new BigDecimal(count), BigDecimal.ROUND_HALF_UP);
		}else{
			return null;
		}
	}

	@Override
	public BigInteger getMin() {
		return min;
	}

	@Override
	public BigInteger getMax() {
		return max;
	}

	@Override
	public BigInteger getSum() {
		return sum;
	}

	@Override
	public long getCount() {
		return count;
	}

	@Override
	public void reset() {
		count = 0;
		sum = BigInteger.ZERO;
		min = null;
		max = null;
	}

	@Override
	public void reset(long newCount, BigInteger newSum, BigInteger newMin, BigInteger newMax) {
		count = newCount;
		sum = newSum;
		min = newMin;
		max = newMax;
	}

	@Override
	public String toString(){
		return "(" + count + ", " + sum + ", " + min + "/" + max + ")";
	}


}
