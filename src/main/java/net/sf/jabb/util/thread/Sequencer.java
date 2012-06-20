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

package net.sf.jabb.util.thread;

import java.util.concurrent.atomic.AtomicLong;

/**
 * It generates sequence of incremental numbers, ranging from 0 to Long.MAX_VALUE, 
 * without repeating or missing of any number.<br>
 * 生成序列数字，保证不重复渐增，范围从0到Long.MAX_VALUE，循环往复。
 * <p>
 * It is multi-thread safe, and has high performance.
 * <p>
 * 它是线程安全的，而且性能高。
 * 
 * @author Zhengmao HU (James)
 */
public class Sequencer {
	protected AtomicLong currentValue;
	
	/**
	 * Constructs an instance that generates numbers starting from specified value.<br>
	 * 创建一个实例，且初始值为指定的值。
	 * 
	 * @param initialValue	the first value that will be returned by next()
	 */
	public Sequencer(long initialValue){
		currentValue = new AtomicLong(Long.MIN_VALUE + initialValue*2);
	}
	
	/**
	 * Constructs an instance that generates numbers starting from 0.<br>
	 * 创建一个实例，且初始值为0。
	 * 
	 */
	public Sequencer(){
		this(0);
	}

	/**
	 * Gets the next number in sequence.<br>
	 * 获得下一个序列值。
	 * 
	 * @return	the next number in sequence
	 */
	public long next() {
		long l = currentValue.getAndAdd(2);
		return l/2 + (Long.MAX_VALUE-1)/2 + 1;
	}

}
