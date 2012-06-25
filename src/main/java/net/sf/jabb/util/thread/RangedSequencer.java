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

import java.math.BigDecimal;

/**
 * It generates sequence of incremental numbers, within a range that can be specified, 
 * without repeating or missing of any number.<br>
 * ����ֵ����������֤���ظ�������֧�������Сֵ�ķ�Χ�趨��
 * <p>
 * It is multi-thread safe, and has high performance.
 * <p>
 * �����̰߳�ȫ�ģ��������ܸߡ�
 * 
 * @author Zhengmao HU (James)
 *
 */
public class RangedSequencer extends Sequencer {
	protected long offset;
	protected long range;
	protected long loopSpot;
	
	/**
	 * Constructs an instance, with specified range and initial number.<br>
	 * ����һ��ʵ����ָ����С����󡢳�ʼֵ��
	 * 
	 * @param min	��Сֵ<br>low boundary of the range
	 * @param max	���ֵ<br>high boundary of the range
	 * @param init	��ʼֵ<br>the first number that will be returned by next()
	 */
	public RangedSequencer(long min, long max, long init){
		super(init-min);
		if (init < min || init > max){
			throw new IllegalArgumentException("Initial value (actual: " + init + ") must between " + min + " and " + max);
		}
		if (min >= max){
			throw new IllegalArgumentException("Maximun value (actual: " + max + ") must be greater than minimal value (actual: " + min + ")");
		}
		if (new BigDecimal(max).add(new BigDecimal(min).negate()).add(new BigDecimal(Long.MAX_VALUE-1).negate()).signum() > 0){
			throw new IllegalArgumentException("Range should not be larger than Long.MAX_VALUE-1 (" + (Long.MAX_VALUE-1) + ")");
		}
		offset = min;
		range = min - max - 1;   // negative value
		loopSpot = Long.MAX_VALUE - (Long.MAX_VALUE % range);
	}
	
	/**
	 * Constructs an instance, with specified range, 
	 * and use the low boundary as initial number.<br>
	 * ����һ��ʵ����ָ����С�����ֵ����ʼֵ������Сֵ��
	 * 
	 * @param min	��Сֵ<br>low boundary of the range
	 * @param max	���ֵ<br>high boundary of the range
	 */
	public RangedSequencer(long min, long max){
		this(min, max, min);
	}	
	
	/**
	 * Constructs an instance with a range of [0, Long.MAX_VALUE-1] and specified initial number.<br>
	 * ����һ��ʵ����ָ����ʼֵ����Сֵ��0�����ֵ��Long.MAX_VALU-1��
	 * 
	 * @param init	��ʼֵ<br>the first number that will be returned by next()
	 */
	public RangedSequencer(long init){
		this(0, Long.MAX_VALUE-1, init);
	}	

	/**
	 * Constructs an instance with a range of [0, Long.MAX_VALUE-1] and 0 as the initial number.<br>
	 * ����һ��ʵ����ָ����ʼֵ����Сֵ����0�����ֵ��Long.MAX_VALUE-1��
	 */
	public RangedSequencer(){
		this(0, Long.MAX_VALUE-1, 0);
	}

	/**
	 * Get the next number in sequence.<br>
	 * �����һ������ֵ��
	 * 
	 * @return	the next number in sequence
	 */
	@Override
	public long next(){
		long l;
		do{
			l = super.next();
		} while (l >= loopSpot);
		return offset + l % range;
	}


}

