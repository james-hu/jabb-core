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
 * �����Сֵ�����Ƚ�Ȼ�󽻻����Ĺ�����ͬ���ģ��������Ƕ��̰߳�ȫ�ġ�
 *
 */
public class AtomicMinLong extends AtomicMinMaxLong {
	/**
	 * Constructs an instance to store the minimum value.<br>
	 * ����һ��ʵ��������������Сֵ��
	 */
	public AtomicMinLong(){
		super(Long.MAX_VALUE);
	}

	/**
	 * Compare a value with current minimal value and make the less one the new minimum value;
	 * New minimum value after comparison is returned.<br>
	 * ��һ��ֵͬ��ǰֵ�Ƚϣ�������С���Ǹ�����Ϊ�µ���Сֵ�����رȽ�֮����µ���Сֵ��
	 * 
	 * @param newValue	�������Ƚϵ�ֵ��
	 * @return		New minimum value after comparison<br>�Ƚ�֮����µ���Сֵ
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
	 * ��һ��ֵͬ��ǰֵ�Ƚϣ�������С���Ǹ�����Ϊ�µ���Сֵ�����رȽ�֮ǰ�ľɵ���Сֵ��
	 * 
	 * @param newValue	�������Ƚϵ�ֵ��
	 * @return		Old minimum value before comparison<br>�Ƚ�֮ǰ�ľɵ���Сֵ
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
	 * ��һ��ֵͬ��ǰֵ�Ƚϣ�������С���Ǹ�����Ϊ�µ���Сֵ��
	 * 
	 * @param newValue	�������Ƚϵ�ֵ��
	 */
	public void min(long newValue){
		minAndGet(newValue);
	}
	

}
