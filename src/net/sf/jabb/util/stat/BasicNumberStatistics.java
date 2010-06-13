/*
Copyright 2010 Zhengmao HU (James)

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

import java.util.concurrent.atomic.AtomicLong;

/**
 * 提供基本的统计信息，包括：
 * 最大值、最小值、平均值、总计、个数。
 * 它是多线程安全的。
 * @author Zhengmao HU (James)
 *
 */
public class BasicNumberStatistics {
	protected AtomicMinLong min;
	protected AtomicMaxLong max;
	protected AtomicLong sum;
	protected AtomicLong count;
	
	public BasicNumberStatistics(){
		min = new AtomicMinLong();
		max = new AtomicMaxLong();
		sum = new AtomicLong();
		count = new AtomicLong();
	}
	
	/**
	 * 把一个值提供给统计
	 * @param value		需要提供给统计的值
	 */
	public void put(long value){
		min.min(value);
		max.max(value);
		sum.addAndGet(value);
		count.incrementAndGet();
	}
	
	/**
	 * 检查是否供统计的数据存在，如果没有则抛异常
	 */
	protected void ensureDataExists(){
		if (getCount() <= 0){
			throw new IllegalStateException("No data for statistics.");
		}
	}
	
	/**
	 * 取得平均值。如果此前并没有提供任何值给统计用，则会抛出异常。
	 * @return
	 */
	public double getAvg(){
		ensureDataExists();
		return (double)getSum()/getCount();
	}
	
	/**
	 * 取得最小值。如果此前并没有提供任何值给统计用，则会抛出异常。
	 * @return
	 */
	public long getMin() {
		ensureDataExists();
		return min.get();
	}
	
	/**
	 * 取得最大值。如果此前并没有提供任何值给统计用，则会抛出异常。
	 * @return
	 */
	public long getMax() {
		ensureDataExists();
		return max.get();
	}
	
	/**
	 * 取得加总值。如果此前并没有提供任何值给统计用，则返回0。
	 * @return
	 */
	public long getSum() {
		return sum.get();
	}
	
	/**
	 * 取得已经提供给统计的值的个数。如果此前并没有提供任何值给统计用，则返回0。
	 * @return
	 */
	public long getCount() {
		return count.get();
	}

}
