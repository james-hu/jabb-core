/*
Copyright 2010-2011, 2015 Zhengmao HU (James)

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
*/

package net.sf.jabb.util.stat;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.concurrent.atomic.AtomicLong;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

/**
 * 提供基本的统计信息，包括：
 * 最大值、最小值、平均值、总计、个数。
 * 它是多线程安全的。
 * @author Zhengmao HU (James)
 * @deprecated use ConcurrentLongStatistics in jabb-core-java8 instead if you can use Java 8
 */
public class AtomicLongStatistics implements NumberStatistics<Long>, Serializable {
	private static final long serialVersionUID = 2001318020408834046L;

	protected AtomicLong count;
	protected AtomicLong sum;
	protected ConcurrentLongMinMaxHolder minMax;
	
	public AtomicLongStatistics(){
		count = new AtomicLong();
		sum = new AtomicLong();
		minMax = new ConcurrentLongMinMaxHolder();
	}
	
	public AtomicLongStatistics(long count, Long sum, Long min, Long max){
		this();
		reset(count, sum, min, max);
	}
	
	@Override
	public int hashCode(){
		return new HashCodeBuilder()
				.append(count)
				.append(sum)
				.append(minMax)
				.toHashCode();
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
			.append(this.getCount(), that.getCount())
			.append(this.getSum(), that.getSum() == null ? null : Long.valueOf(that.getSum().longValue()))
			.append(this.getMin(), that.getMin() == null ? null : Long.valueOf(that.getMin().longValue()))
			.append(this.getMax(), that.getMax() == null ? null : Long.valueOf(that.getMax().longValue()))
			.isEquals();
	}

	
	@Override
	public void evaluate(int value) {
		count.incrementAndGet();
		sum.addAndGet(value);
		minMax.evaluate(value);
	}

	@Override
	public void evaluate(long value){
		count.incrementAndGet();
		sum.addAndGet(value);
		minMax.evaluate(value);
	}
	
	@Override
	public void evaluate(BigInteger value) {
		long x = value.longValue();
		count.incrementAndGet();
		sum.addAndGet(x);
		minMax.evaluate(x);
	}

	@Override
	public Double getAvg(){
		long countValue = count.get();
		if (countValue > 0){
			if (minMax.getMinAsLong() == minMax.getMaxAsLong()){
				return minMax.getMinAsLong().doubleValue();
			}else{
				return sum.doubleValue()/countValue;
			}
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
		return minMax.getMinAsLong();
	}
	
	@Override
	public Long getMax() {
		return minMax.getMaxAsLong();
	}
	
	@Override
	public Long getSum() {
		return sum.get();
	}
	
	@Override
	public long getCount() {
		return count.get();
	}
	
	@Override
	public void reset(){
		count.set(0);
		sum.set(0);
		minMax.reset();
	}

	@Override
	public void reset(long newCount, Long newSum, Long newMin, Long newMax) {
		count.set(newCount);
		sum.set(newSum);
		minMax.reset(newMin, newMax);
	}

	@Override
	public String toString(){
		return "(" + count.get() + ", " + sum.get() + ", " + getMin() + "/" + getMax() + ")";
	}

	@Override
	public void merge(long count, Long sum, Long min, Long max) {
		this.count.addAndGet(count);
		if (sum != null){
			this.sum.addAndGet(sum);
		}
		if (min != null){
			this.minMax.evaluate(min);
		}
		if (max != null){
			this.minMax.evaluate(max);
		}
	}
	
	@Override
	public void merge(NumberStatistics<? extends Number> other){
		if (other != null && other.getCount() > 0){
			merge(other.getCount(), other.getSum().longValue(), other.getMin().longValue(), other.getMax().longValue());
		}
	}
	

}
