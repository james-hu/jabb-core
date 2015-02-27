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

/**
 * 提供基本的统计信息，包括：
 * 最大值、最小值、平均值、总计、个数。
 * 它是多线程安全的。
 * @author Zhengmao HU (James)
 * @deprecated use ConcurrentLongStatistics in jabb-core-java8 instead if you can use Java 8
 */
public class AtomicLongStatistics implements NumberStatistics<Long>, Serializable {
	private static final long serialVersionUID = 2001318020408834046L;

	protected ConcurrentLongMinMaxHolder minMax;
	protected AtomicLong sum;
	protected AtomicLong count;
	
	public AtomicLongStatistics(){
		minMax = new ConcurrentLongMinMaxHolder();
		sum = new AtomicLong();
		count = new AtomicLong();
	}
	
	/* (non-Javadoc)
	 * @see net.sf.jabb.util.stat.NumberStatistics#merge(net.sf.jabb.util.stat.BasicNumberStatistics)
	 */
	@Override
	public void merge(NumberStatistics<? extends Number> other){
		if (other != null){
			long otherCount = other.getCount();
			if (otherCount  > 0){
				minMax.evaluate(other.getMin().longValue());
				minMax.evaluate(other.getMax().longValue());
				sum.addAndGet(other.getSum().longValue());
				count.addAndGet(otherCount);
			}
		}
	}
	
	@Override
	public void put(int value) {
		minMax.evaluate(value);
		sum.addAndGet(value);
		count.incrementAndGet();
	}

	/* (non-Javadoc)
	 * @see net.sf.jabb.util.stat.NumberStatistics#put(long)
	 */
	@Override
	public void put(long value){
		minMax.evaluate(value);
		sum.addAndGet(value);
		count.incrementAndGet();
	}
	
	@Override
	public void put(BigInteger value) {
		long x = value.longValue();
		minMax.evaluate(x);
		sum.addAndGet(x);
		count.incrementAndGet();
	}

	/* (non-Javadoc)
	 * @see net.sf.jabb.util.stat.NumberStatistics#getAvg()
	 */
	@Override
	public Double getAvg(){
		long countValue = count.get();
		if (countValue > 0){
			return sum.doubleValue()/countValue;
		}else{
			return null;
		}
	}
	
	@Override
	public BigDecimal getAvg(int scale) {
		return new BigDecimal(getAvg()).setScale(scale, BigDecimal.ROUND_HALF_UP);
	}

	/* (non-Javadoc)
	 * @see net.sf.jabb.util.stat.NumberStatistics#getMin()
	 */
	@Override
	public Long getMin() {
		return minMax.getMinAsLong();
	}
	
	/* (non-Javadoc)
	 * @see net.sf.jabb.util.stat.NumberStatistics#getMax()
	 */
	@Override
	public Long getMax() {
		return minMax.getMaxAsLong();
	}
	
	/* (non-Javadoc)
	 * @see net.sf.jabb.util.stat.NumberStatistics#getSum()
	 */
	@Override
	public Long getSum() {
		return sum.get();
	}
	
	/* (non-Javadoc)
	 * @see net.sf.jabb.util.stat.NumberStatistics#getCount()
	 */
	@Override
	public long getCount() {
		return count.get();
	}
	
	/* (non-Javadoc)
	 * @see net.sf.jabb.util.stat.NumberStatistics#reset()
	 */
	@Override
	public void reset(){
		minMax.reset();
		sum.set(0);
		count.set(0);
	}

	@Override
	public void reset(Long newCount, Long newSum, Long newMin, Long newMax) {
		minMax.reset(newMin, newMax);
		sum.set(newSum);
		count.set(newCount);
	}

	@Override
	public String toString(){
		return "(" + count.get() + ", " + sum.get() + ", " + getMin() + "/" + getMax() + ")";
	}

}
