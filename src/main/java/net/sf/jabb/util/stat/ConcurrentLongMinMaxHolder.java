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
import java.util.concurrent.atomic.AtomicLong;


/**
 * Holder of the minimum and maximum Long values. It is thread-safe.
 * 
 * @author Zhengmao HU (James)
 *
 */
public class ConcurrentLongMinMaxHolder  implements Serializable, LongMinMaxHolder{
	private static final long serialVersionUID = -2426326997756055169L;
	
	protected AtomicLong minRef;
	protected AtomicLong maxRef;
	
	public ConcurrentLongMinMaxHolder(){
	}
	
	public ConcurrentLongMinMaxHolder(long min, long max){
		reset(min, max);
	}

	
	/* (non-Javadoc)
	 * @see net.sf.jabb.util.stat.MinMaxLong#minMax(long)
	 */
	@Override
	public void minMax(long x){
		if (minRef == null){
			minRef = new AtomicLong(x);
			maxRef = new AtomicLong(x);
			return;
		}

		long min = minRef.get();
		if (min < x){
			long max;
			do {
				max = maxRef.get();
			} while (max < x && !maxRef.compareAndSet(max, x));
		}else if (min > x){ // min > x
			while (min > x && !minRef.compareAndSet(min, x)){
				min = minRef.get();
			}
		}
		// if min == x do nothing
	}
	
	/* (non-Javadoc)
	 * @see net.sf.jabb.util.stat.MinMaxLong#reset()
	 */
	@Override
	public void reset(){
		minRef = null;
	}
	
	/* (non-Javadoc)
	 * @see net.sf.jabb.util.stat.MinMaxLong#reset(long, long)
	 */
	@Override
	public void reset(long min, long max){
		if (min > max){
			throw new IllegalArgumentException("min value must not be greater than max value");
		}
		minRef = new AtomicLong(min);
		maxRef = new AtomicLong(max);
	}

	
	/**
	 * Merge the min/max value from another instance into this one.
	 * @param another   another instance of AtomicMinMaxLong
	 */
	@Override
	public void merge(ConcurrentLongMinMaxHolder another){
		Long anotherMin = another.getMin();
		if (anotherMin != null){
			minMax(anotherMin);
		}
		Long anotherMax = another.getMax();
		if (anotherMax != null){
			minMax(anotherMax);
		}
	}
	
	public Long getMin(){
		return minRef == null ? null : minRef.get();
	}
	
	public Long getMax(){
		return maxRef == null ? null : maxRef.get();
	}
	
	@Override
	public Long getLongMin() {
		return minRef == null ? null : minRef.get();
	}

	@Override
	public Long getLongMax() {
		return maxRef == null ? null : maxRef.get();
	}
	
	@Override
	public String toString(){
		return "(" + getMin() + ", " + getMax() + ")";
	}

	

}
