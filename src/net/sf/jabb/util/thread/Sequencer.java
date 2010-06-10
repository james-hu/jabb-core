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

package net.sf.jabb.util.thread;

import java.util.concurrent.atomic.AtomicLong;

/**
 * 生成序列数字，保证不重复渐增，范围从0到Long.MAX_VALUE，循环往复。
 * 它是线程安全的，而且性能高。
 * 
 * @author Zhengmao HU (James)
 */
public class Sequencer {
	protected AtomicLong currentValue;
	
	/**
	 * 初始值为指定的值
	 * @param initialValue
	 */
	public Sequencer(long initialValue){
		currentValue = new AtomicLong(Long.MIN_VALUE + initialValue*2);
	}
	
	/**
	 * 初始值为0
	 */
	public Sequencer(){
		this(0);
	}

	public long next() {
		long l = currentValue.getAndAdd(2);
		return l/2 + (Long.MAX_VALUE-1)/2 + 1;
	}

}
