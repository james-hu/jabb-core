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

import java.util.concurrent.atomic.AtomicLong;

/**
 * �ṩ������ͳ����Ϣ��������
 * ���ֵ����Сֵ��ƽ��ֵ���ܼơ�������
 * ���Ƕ��̰߳�ȫ�ġ�
 * @author Zhengmao HU (James)
 *
 */
public class BasicNumberStatistics {
	protected AtomicMinLong min;
	protected AtomicMaxLong max;
	protected AtomicLong sum;
	protected AtomicLong count;
	
	public BasicNumberStatistics(){
		min = new AtomicMinLong();
		max = new AtomicMaxLong();
		sum = new AtomicLong();
		count = new AtomicLong();
	}
	
	/**
	 * ��һ��ֵ�ṩ��ͳ��
	 * @param value		��Ҫ�ṩ��ͳ�Ƶ�ֵ
	 */
	public void put(long value){
		min.min(value);
		max.max(value);
		sum.addAndGet(value);
		count.incrementAndGet();
	}
	
	/**
	 * ����Ƿ�ͳ�Ƶ����ݴ��ڣ����û�������쳣
	 */
	protected void ensureDataExists(){
		if (getCount() <= 0){
			throw new IllegalStateException("No data for statistics.");
		}
	}
	
	/**
	 * ȡ��ƽ��ֵ�������ǰ��û���ṩ�κ�ֵ��ͳ���ã�����׳��쳣��
	 * @return
	 */
	public double getAvg(){
		ensureDataExists();
		return (double)getSum()/getCount();
	}
	
	/**
	 * ȡ����Сֵ�������ǰ��û���ṩ�κ�ֵ��ͳ���ã�����׳��쳣��
	 * @return
	 */
	public long getMin() {
		ensureDataExists();
		return min.get();
	}
	
	/**
	 * ȡ�����ֵ�������ǰ��û���ṩ�κ�ֵ��ͳ���ã�����׳��쳣��
	 * @return
	 */
	public long getMax() {
		ensureDataExists();
		return max.get();
	}
	
	/**
	 * ȡ�ü���ֵ�������ǰ��û���ṩ�κ�ֵ��ͳ���ã��򷵻�0��
	 * @return
	 */
	public long getSum() {
		return sum.get();
	}
	
	/**
	 * ȡ���Ѿ��ṩ��ͳ�Ƶ�ֵ�ĸ����������ǰ��û���ṩ�κ�ֵ��ͳ���ã��򷵻�0��
	 * @return
	 */
	public long getCount() {
		return count.get();
	}

}
