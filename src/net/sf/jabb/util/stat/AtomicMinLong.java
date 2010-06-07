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
 * �����Сֵ���޸�ʱ��ͬ���ģ������Ƕ��̰߳�ȫ�ġ�
 * @author Zhengmao HU (James)
 *
 */
public class AtomicMinLong extends AtomicMinMaxLong {
	/**
	 * ����һ��ʵ��������������Сֵ
	 */
	public AtomicMinLong(){
		super(Long.MAX_VALUE);
	}

	/**
	 * ����Ϊ��ǰֵ����ֵ֮�����С��һ��
	 * @param newValue
	 * @return			��Сֵ
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
	 * ����Ϊ��ǰֵ����ֵ֮�����С��һ��
	 * @param newValue
	 * @return		ԭ����ֵ
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
	 * ����Ϊ��ǰֵ����ֵ֮�����С��һ��
	 * @param newValue
	 */
	public void min(long newValue){
		minAndGet(newValue);
	}
	

}
