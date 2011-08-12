/*
Copyright 2010-2011 Zhengmao HU (James)

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

/**
 * It stores the minimum value; 
 * the compare-and-change procedure is synchronized so that multi-thread safe is ensured.<br>
 * 存放最小值，“比较然后交换”的过程是同步的，所以它是多线程安全的。
 *
 */
public class AtomicMinLong extends AtomicMinMaxLong {
	/**
	 * Constructs an instance to store the minimum value.<br>
	 * 创建一个实例，用来保存最小值。
	 */
	public AtomicMinLong(){
		super(Long.MAX_VALUE);
	}

	/**
	 * Compare a value with current minimal value and make the less one the new minimum value;
	 * New minimum value after comparison is returned.<br>
	 * 拿一个值同当前值比较，把其中小的那个设置为新的最小值，返回比较之后的新的最小值。
	 * 
	 * @param newValue	拿来作比较的值。
	 * @return		New minimum value after comparison<br>比较之后的新的最小值
	 */
	public long minAndGet(long newValue){
		synchronized(updateLock){
			if (newValue < value){
				value = newValue;
			}
			return value;
		}
	}
	
	/**
	 * Compare a value with current minimal value and make the less one the new minimum value;
	 * Old minimum value before comparison is returned.<br>
	 * 拿一个值同当前值比较，把其中小的那个设置为新的最小值，返回比较之前的旧的最小值。
	 * 
	 * @param newValue	拿来作比较的值。
	 * @return		Old minimum value before comparison<br>比较之前的旧的最小值
	 */
	public long getAndMin(long newValue){
		long oldValue;
		synchronized(updateLock){
			oldValue = value;
			if (newValue < value){
				value = newValue;
			}
			return oldValue;
		}
	}
	
	/**
	 * Compare a value with current minimum value and make the less one the new minimum value.<br>
	 * 拿一个值同当前值比较，把其中小的那个设置为新的最小值。
	 * 
	 * @param newValue	拿来作比较的值。
	 */
	public void min(long newValue){
		minAndGet(newValue);
	}
	

}
