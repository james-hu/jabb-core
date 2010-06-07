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

/**
 * 存放最小值，修改时是同步的，所以是多线程安全的。
 * @author Zhengmao HU (James)
 *
 */
public class AtomicMinLong extends AtomicMinMaxLong {
	/**
	 * 创建一个实例，用来保存最小值
	 */
	public AtomicMinLong(){
		super(Long.MAX_VALUE);
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
	 * 设置为当前值与新值之间的最小的一个
	 * @param newValue
	 */
	public void min(long newValue){
		minAndGet(newValue);
	}
	

}
