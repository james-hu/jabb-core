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

/**
 * It stores the maximum value; 
 * the compare-and-change procedure is synchronized so that multi-thread safe is ensured.<br>
 * 存放最大值，“比较然后交换”的过程是同步的，所以它是多线程安全的。
 * 
 * @author Zhengmao HU (James)
 * @deprecated use AtomicLongMinMaxHolder instead
 */
public class AtomicMaxLong extends ConcurrentLongMinMaxHolder {
	private static final long serialVersionUID = 8925676360071717966L;

	/**
	 * Constructs an instance to store the maximum value.<br>
	 * 创建一个实例，用来保存最大值。
	 */
	public AtomicMaxLong(){
		super();
	}
	
	/**
	 * Compare a value with current maximum value and make the greater one the new maximum value;
	 * New maximum value after comparison is returned.<br>
	 * 拿一个值同当前值比较，把其中大的那个设置为新的最大值，返回比较之后的新的最大值。
	 * 
	 * @param newValue	拿来作比较的值。
	 * @return		New maximum value after comparison<br>比较之后的新的最大值
	 */
	public Long maxAndGet(long newValue){
		evaluate(newValue);
		return getMaxAsLong();
	}

	/**
	 * Compare a value with current maximum value and make the greater one the new maximum value;
	 * Previous maximum value before comparison is returned.<br>
	 * 拿一个值同当前值比较，把其中大的那个设置为新的最大值，返回比较之前的老的最大值。
	 * 
	 * @param newValue	拿来作比较的值。
	 * @return		Previous maximum value before comparison<br>比较之前的老的最大值
	 */
	public Long getAndMax(long newValue){
		Long oldValue = getMaxAsLong();
		evaluate(newValue);
		return oldValue;
	}
	
	/**
	 * Compare a value with current maximum value and make the greater one the new maximum value.<br>
	 * 拿一个值同当前值比较，把其中大的那个设置为新的最大值。
	 * 
	 * @param newValue	拿来作比较的值。
	 */
	public void max(long newValue){
		evaluate(newValue);
	}

	/**
	 * getCurrent value.<br>
	 * 获得当前值。
	 * 
	 * @return Current value.
	 */
	public Long get(){
		return getMaxAsLong();
	}
	

}
