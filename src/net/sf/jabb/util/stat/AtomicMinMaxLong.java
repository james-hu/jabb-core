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
 * The parent class for AtomicMinLong and AtomicMaxLong.<br>
 * AtomicMinLong��AtomicMaxLong�Ĺ����ĸ��ࡣ
 * 
 * @author Zhengmao HU (James)
 *
 */
abstract class AtomicMinMaxLong {
	protected long value;
	protected Object updateLock;
	
	protected AtomicMinMaxLong(long initialValue){
		value = initialValue;
		updateLock = new Object();
	}
	
	/**
	 * getCurrent value.<br>
	 * ��õ�ǰֵ��
	 * 
	 * @return Current value.
	 */
	public long get(){
		return value;
	}
	
	/**
	 * getCurrent value.<br>
	 * ��õ�ǰֵ��
	 * 
	 * @return Current value.
	 */
	public long longValue(){
		return value;
	}
	
	/**
	 * getCurrent value as int.<br>
	 * ��int���ͻ�õ�ǰֵ��
	 * 
	 * @return Current value as int.
	 */
	public int intValue(){
		return (int) value;
	}
	
	/**
	 * Reset to initial status.<br>
	 * �ظ�����ʼ״̬��
	 */
	abstract public void reset();

}
