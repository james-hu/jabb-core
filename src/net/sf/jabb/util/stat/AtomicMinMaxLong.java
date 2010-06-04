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
 * 支持同步的设置最大、最小的操作
 * @author Zhengmao HU (James)
 *
 */
public class AtomicMinMaxLong {
	protected long value;
	protected Object updateLock = new Object();
	
	protected AtomicMinMaxLong(long initialValue){
		value = initialValue;
	}
	
	/**
	 * 创建一个实例，用来保存最小值
	 * @return
	 */
	static public AtomicMinMaxLong newInstanceForMin(){
		return new AtomicMinMaxLong(Long.MAX_VALUE);
	}
	
	/**
	 * 创建一个实例，用来保存最大值
	 * @return
	 */
	static public AtomicMinMaxLong newInstanceForMax(){
		return new AtomicMinMaxLong(Long.MIN_VALUE);
	}
	
	/**
	 * 获得当前值
	 * @return
	 */
	public long get(){
		return value;
	}
	
	/**
	 * 获得当前值
	 * @return
	 */
	public long longValue(){
		return value;
	}
	
	/**
	 * 获得当前值
	 * @return
	 */
	public int intValue(){
		return (int) value;
	}
	
	/**
	 * 设置为当前值与新值之间的最小的一个
	 * @param newValue
	 * @return			最小值
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
	 * 设置为当前值与新值之间最大的一个
	 * @param newValue
	 * @return		最大值
	 */
	public long maxAndGet(long newValue){
		synchronized(updateLock){
			if (newValue > value){
				value = newValue;
			}
			return value;
		}
	}

	/**
	 * 设置为当前值与新值之间的最小的一个
	 * @param newValue
	 * @return		原来的值
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
	 * 设置为当前值与新值之间最大的一个
	 * @param newValue
	 * @return		原来的值
	 */
	public long getAndMax(long newValue){
		long oldValue;
		synchronized(updateLock){
			oldValue = value;
			if (newValue > value){
				value = newValue;
			}
			return oldValue;
		}
	}

}
