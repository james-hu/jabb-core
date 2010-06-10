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

/**
 * 序列值生成器，保证不重复渐增，支持最大最小值的范围设定。
 * 它是线程安全的，而且性能高。
 * 但是每当获取了Long.MAX_VALUE个值之后，下一个取得值可能会出现一次跳跃（从当前值跳到最小值）。
 * 
 * @author Zhengmao HU (James)
 *
 */
public class RangedSequencer extends Sequencer {
	protected long offset;
	protected long range;
	
	/**
	 * 创建一个实例，指定最小、最大、初始值。
	 * @param min	最小值
	 * @param max	最大值
	 * @param init	初始值
	 */
	public RangedSequencer(long min, long max, long init){
		super(init-min);
		if (init < min || init > max){
			throw new IllegalArgumentException("Initial value (actual: " + init + ") must between " + min + " and " + max);
		}
		if (min >= max){
			throw new IllegalArgumentException("Maximun value (actual: " + max + ") must be greater than minimal value (actual: " + min + ")");
		}
		if ((double)max - min > Long.MAX_VALUE){
			throw new IllegalArgumentException("Range should not be larger than Long.MAX_VALUE (" + Long.MAX_VALUE + ")");
		}
		offset = min;
		range = min - max - 1;   // negative value
	}
	
	/**
	 * 创建一个实例，指定最小、最大值，初始值就是最小值。
	 * @param min	最小值
	 * @param max	最大值
	 */
	public RangedSequencer(long min, long max){
		this(min, max, min);
	}	
	
	/**
	 * 创建一个实例，指定初始值。最小值是0，最大值是Long.MAX_VALUE。
	 * @param init	初始值
	 */
	public RangedSequencer(long init){
		this(0, Long.MAX_VALUE, init);
	}	

	/**
	 * 创建一个实例，指定初始值和最小值都是0，最大值是Long.MAX_VALUE。
	 */
	public RangedSequencer(){
		this(0, Long.MAX_VALUE, 0);
	}

	public long next(){
		long l = super.next();
		return offset + l % range;
	}


}

