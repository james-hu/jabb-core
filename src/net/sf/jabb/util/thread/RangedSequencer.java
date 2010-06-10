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
 * 
 * @author Zhengmao HU (James)
 *
 */
public class RangedSequencer extends Sequencer {
	protected long offset;
	protected long range;
	
	/**
	 * ����һ��ʵ����ָ����С����󡢳�ʼֵ��
	 * @param min	��Сֵ
	 * @param max	���ֵ
	 * @param init	��ʼֵ
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
	 * @param min	��Сֵ
	 * @param max	���ֵ
	 */
	public RangedSequencer(long min, long max){
		this(min, max, min);
	}	
	
	/**
	 * ����һ��ʵ����ָ����ʼֵ����Сֵ��0�����ֵ��Long.MAX_VALUE��
	 * @param init	��ʼֵ
	 */
	public RangedSequencer(long init){
		this(0, Long.MAX_VALUE, init);
	}	

	/**
	 * ����һ��ʵ����ָ����ʼֵ����Сֵ����0�����ֵ��Long.MAX_VALUE��
	 */
	public RangedSequencer(){
		this(0, Long.MAX_VALUE, 0);
	}

	public long next(){
		long l = super.next();
		return offset + l % range;
	}


}

