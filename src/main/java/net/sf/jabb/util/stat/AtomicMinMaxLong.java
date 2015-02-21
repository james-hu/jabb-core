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
 * Holder of the minimum and maximum BigInteger values.
 * It is also the parent class for AtomicMinLong and AtomicMaxLong.<br>
 * AtomicMinLong和AtomicMaxLong的公共的父类。
 * <p>It is thread-safe.</p>
 * 
 * @author Zhengmao HU (James)
 *
 */
public class AtomicMinMaxLong  implements Serializable{
	private static final long serialVersionUID = -2426326997756055169L;
	
	AtomicLong minRef;
	AtomicLong maxRef;
	
	public AtomicMinMaxLong(){
	}
	
	public AtomicMinMaxLong(long min, long max){
		reset(min, max);
	}

	
	/**
	 * Compare current minimum and maximum values with a new value and update the minimum and/or 
	 * maximum values if needed. If previously both minimum and maximum values are set set, both
	 * of them will be set to the input value.
	 * @param x the new value to be compared
	 */
	public void minMax(long x){
		if (minRef == null){
			minRef = new AtomicLong(x);
			maxRef = new AtomicLong(x);
			return;
		}

		long min = minRef.get();
		if (min == x){
			return;
		}else if (min < x){
			long max;
			do {
				max = maxRef.get();
			} while (max < x && !maxRef.compareAndSet(max, x));
		}else{ // min > x
			while (min > x && !minRef.compareAndSet(min, x)){
				min = minRef.get();
			}
		}
	}
	
	public void reset(){
		minRef = null;
	}
	
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
	public void merge(AtomicMinMaxLong another){
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
	public String toString(){
		return "(" + getMin() + ", " + getMax() + ")";
	}
	
	

}
