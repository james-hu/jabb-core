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

import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 * Ƶ�μ������Ļ��࣬��ʵ����һЩ�����ķ���������������ʵ�ַ���������ȥʵ�֡�
 * 
 * @author Zhengmao HU (James)
 *
 */
public abstract class FrequencyCounter {


	/**
	 * ��¼��ĳʱ�̷����˶��ٴ�
	 * @param when		������ʱ��
	 * @param times		�����Ĵ���
	 */
	public abstract void record(long when, int times);

	/**
	 * ���ĳʱ�̵�ͳ����
	 * @param when	ʱ��
	 * @return		ͳ����
	 */
	public abstract long getCount(long when);

	/**
	 * �����ĳʱ���ڵ���ͳ����
	 * @param fromWhen		��ʼʱ��
	 * @param toWhen		����ʱ��
	 * @param fromInclusive	�Ƿ������ʼʱ��
	 * @param toInclusive	�Ƿ��������ʱ��
	 * @return
	 */
	public abstract long getCount(long fromWhen, long toWhen, boolean fromInclusive,
			boolean toInclusive);

	/**
	 * ������ɵ���ʷ����
	 * @param tillWhen	������ĸ�ʱ���Ϊֹ
	 */
	public abstract void purge(long tillWhen);

	/**
	 * ��¼��ĳ��ʱ�䷢���˼���
	 * @param when	����ʱ��
	 * @param times	��������
	 */
	public void record(Date when, int times) {
		record(when.getTime(), times);
	}

	/**
	 * ��¼��ĳ��ʱ�䷢����һ��
	 * @param when	������ʱ��
	 */
	public void record(long when) {
		record(when, 1);
	}
	
	/**
	 * ��¼�ڵ�ǰʱ�䷢����һ��
	 */
	public void record(){
		record(System.currentTimeMillis(), 1);
	}

	/**
	 * ��¼�ڵ�ǰʱ�䷢���˼���
	 * @param times	�����Ĵ���
	 */
	public void record(int times){
		record(System.currentTimeMillis(), times);
	}

	/**
	 * ��¼��ĳ��ʱ�䷢���˼���
	 * @param when
	 */
	public void record(Date when) {
		record(when.getTime(), 1);
	}

	/**
	 * ���ĳ��ʱ��ļ���
	 * @param when
	 * @return
	 */
	public long getCount(Date when) {
		return getCount(when.getTime());
	}

	/**
	 * ���ĳʱ������ļ���
	 * @param fromWhen
	 * @param toWhen
	 * @return
	 */
	public long getCount(long fromWhen, long toWhen) {
		return getCount(fromWhen, toWhen, true, false);
	}

	/**
	 * ���ĳʱ������ļ���
	 * @param fromWhen
	 * @param toWhen
	 * @param fromInclusive
	 * @param toInclusive
	 * @return
	 */
	public long getCount(Date fromWhen, Date toWhen, boolean fromInclusive, boolean toInclusive) {
		return getCount(fromWhen.getTime(), toWhen.getTime(), true, false);
	}

	/**
	 * ���ĳʱ������ļ���
	 * @param fromWhen
	 * @param toWhen
	 * @return
	 */
	public long getCount(Date fromWhen, Date toWhen) {
		return getCount(fromWhen.getTime(), toWhen.getTime(), true, false);
	}

	/**
	 * ���ĳʱ������ļ���
	 * @param toWhen	��ĳ��ʱ��Ϊֹ
	 * @param period	��ǰ�ƶ���ʱ��
	 * @param unit		��ǰ�ƶ���ʱ��ĵ�λ
	 * @return
	 */
	public long getCount(long toWhen, long period, TimeUnit unit) {
		return getCount(toWhen - TimeUnit.MILLISECONDS.convert(period, unit), toWhen);
	}

	/**
	 * ���ĳʱ������ļ���
	 * @param toWhen	��ĳ��ʱ��Ϊֹ
	 * @param period	��ǰ�ƶ���ʱ��
	 * @param unit		��ǰ�ƶ���ʱ��ĵ�λ
	 * @return
	 */
	public long getCount(Date toWhen, long period, TimeUnit unit) {
		return getCount(toWhen.getTime(), period, unit);
	}

	/**
	 * ������һ��ʱ��ļ���
	 * @param period	��ǰ�ƶ���ʱ��
	 * @param unit		��ǰ�ƶ���ʱ��ĵ�λ
	 * @return
	 */
	public long getCount(long period, TimeUnit unit) {
		return getCount(System.currentTimeMillis(), period, unit);
	}

	/**
	 * ɾ��������һ��ʱ��ļ�¼
	 * @param tillWhen
	 */
	public void purge(Date tillWhen) {
		purge(tillWhen.getTime());
	}

	/**
	 * ɾ��������һ��ʱ��ļ�¼
	 * @param baseTime			��ʲôʱ������
	 * @param periodBefore		����ʱ��֮ǰ��Ҫɾ��
	 * @param unit				ʱ�䵥λ
	 */
	public void purge(long baseTime, long periodBefore, TimeUnit unit) {
		purge(baseTime - TimeUnit.MILLISECONDS.convert(periodBefore, unit));
	}

	/**
	 * ɾ��������һ��ʱ��ļ�¼
	 * @param base			��ʲôʱ������
	 * @param periodBefore	����ʱ��֮ǰ��Ҫɾ��
	 * @param unit			ʱ�䵥λ
	 */
	public void purge(Date baseTime, long periodBefore, TimeUnit unit) {
		purge(baseTime.getTime(), periodBefore, unit);
	}

	/**
	 * ɾ��������һ��ʱ��ļ�¼
	 * @param periodBefore	���������𣬶���ʱ��֮ǰ
	 * @param unit			ʱ�䵥λ
	 */
	public void purge(long periodBefore, TimeUnit unit) {
		purge(System.currentTimeMillis() - TimeUnit.MILLISECONDS.convert(periodBefore, unit));
	}

}