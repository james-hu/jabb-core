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
public class SimpleLongStatistics implements NumberStatistics<Long>, Serializable {
	private static final long serialVersionUID = -386112646695591088L;

	protected long min;
	protected long max;
	protected long count;
	protected long sum;
	
	protected void evaluateMinMax(long x){
		if (count <= 0){
			min = x;
			max = x;
		}else{
			if (x < min){
				min = x;
			}else if (x > max){
				max = x;
			}
		}
	}

	@Override
	public void merge(NumberStatistics<? extends Number> other) {
		if (other != null){
			merge(other.getCount(), other.getSum().longValue(), other.getMin().longValue(), other.getMax().longValue());
		}
	}

	@Override
	public void merge(long count, Long sum, Long min, Long max) {
		this.count += count;
		if (sum != null){
			this.sum += sum;
		}
		if (min != null){
			evaluateMinMax(min);
		}
		if (max != null){
			evaluateMinMax(max);
		}
	}

	@Override
	public void evaluate(int value) {
		count ++;
		sum += value;
		evaluateMinMax(value);
	}

	@Override
	public void evaluate(long value) {
		count ++;
		sum += value;
		evaluateMinMax(value);
	}

	@Override
	public void evaluate(BigInteger value) {
		if (value != null){
			long x = value.longValue();
			count ++;
			sum += x;
			evaluateMinMax(x);
		}
	}

	@Override
	public Double getAvg() {
		if (count > 0){
			return (double)sum/count;
		}else{
			return null;
		}
	}

	@Override
	public BigDecimal getAvg(int scale) {
		return new BigDecimal(getAvg()).setScale(scale, BigDecimal.ROUND_HALF_UP);
	}

	@Override
	public Long getMin() {
		return count > 0? min : null;
	}

	@Override
	public Long getMax() {
		return count > 0? max : null;
	}

	@Override
	public Long getSum() {
		return sum;
	}

	@Override
	public long getCount() {
		return count;
	}

	@Override
	public void reset() {
		count = 0;
		sum = 0;
		min = 0;
		max = 0;
	}

	@Override
	public void reset(long newCount, Long newSum, Long newMin, Long newMax) {
		count = newCount;
		sum = newSum == null ? 0 : newSum;
		min = newMin == null ? 0: newMin;
		max = newMax == null ? 0 : newMax;
	}

	@Override
	public String toString(){
		return "(" + count + ", " + sum + ", " + getMin() + "/" + getMax() + ")";
	}


}
