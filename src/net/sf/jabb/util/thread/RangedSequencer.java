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
 * ����ֵ����������֤���ظ�������֧�������Сֵ�ķ�Χ�趨��
 * �����̰߳�ȫ�ģ��������ܸߡ�
 * ����ÿ����ȡ��Long.MAX_VALUE��ֵ֮����һ��ȡ��ֵ���ܻ����һ����Ծ���ӵ�ǰֵ������Сֵ����
 * <p>
 * It generates sequence of incremental numbers, with a range that can be specified, 
 * without repeating or missing of any number.
 * It is multi-thread safe, and has high performance.
 * After Long.MAX_VALUE of numbers generated, the next generated number may jump from
 * the-next-value-should-be to the low boundary of the specified range.
 * 
 * @author Zhengmao HU (James)
 *
 */
public class RangedSequencer extends Sequencer {
	protected long offset;
	protected long range;
	
	/**
	 * ����һ��ʵ����ָ����С����󡢳�ʼֵ��
	 * <p>
	 * Constructs an instance, with specified range and initial number.
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
		if ((double)max - min > Long.MAX_VALUE){
			throw new IllegalArgumentException("Range should not be larger than Long.MAX_VALUE (" + Long.MAX_VALUE + ")");
		}
		offset = min;
		range = min - max - 1;   // negative value
	}
	
	/**
	 * ����һ��ʵ����ָ����С�����ֵ����ʼֵ������Сֵ��
	 * <p>
	 * Constructs an instance, with specified range, 
	 * and use the low boundary as initial number.  
	 * 
	 * @param min	��Сֵ<br>low boundary of the range
	 * @param max	���ֵ<br>high boundary of the range
	 */
	public RangedSequencer(long min, long max){
		this(min, max, min);
	}	
	
	/**
	 * ����һ��ʵ����ָ����ʼֵ����Сֵ��0�����ֵ��Long.MAX_VALUE��
	 * <p>
	 * Constructs an instance with a range of [0, Long.MAX_VALUE] and specified initial number.
	 * 
	 * @param init	��ʼֵ<br>the first number that will be returned by next()
	 */
	public RangedSequencer(long init){
		this(0, Long.MAX_VALUE, init);
	}	

	/**
	 * ����һ��ʵ����ָ����ʼֵ����Сֵ����0�����ֵ��Long.MAX_VALUE��
	 * <p>
	 * Constructs an instance with a range of [0, Long.MAX_VALUE] and 0 as the initial number.
	 * 
	 */
	public RangedSequencer(){
		this(0, Long.MAX_VALUE, 0);
	}

	/**
	 * �����һ������ֵ��
	 * <p>
	 * Get the next number in sequence.
	 * @return	the next number in sequence
	 */
	@Override
	public long next(){
		long l = super.next();
		return offset + l % range;
	}


}

