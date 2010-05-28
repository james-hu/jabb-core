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
 * 频次计数器的基类，它实现了一些公共的方法，并留出具体实现方法给子类去实现。
 * 
 * @author Zhengmao HU (James)
 *
 */
public abstract class FrequencyCounter {


	/**
	 * 记录在某时刻发生了多少次
	 * @param when		发生的时刻
	 * @param times		发生的次数
	 */
	public abstract void record(long when, int times);

	/**
	 * 获得某时刻的统计数
	 * @param when	时刻
	 * @return		统计数
	 */
	public abstract long getCount(long when);

	/**
	 * 获得在某时段内的总统计数
	 * @param fromWhen		开始时间
	 * @param toWhen		结束时间
	 * @param fromInclusive	是否包含开始时间
	 * @param toInclusive	是否包含结束时间
	 * @return
	 */
	public abstract long getCount(long fromWhen, long toWhen, boolean fromInclusive,
			boolean toInclusive);

	/**
	 * 清除过旧的历史数据
	 * @param tillWhen	清除到哪个时间点为止
	 */
	public abstract void purge(long tillWhen);

	/**
	 * 记录在某个时间发生了几次
	 * @param when	发生时间
	 * @param times	发生次数
	 */
	public void record(Date when, int times) {
		record(when.getTime(), times);
	}

	/**
	 * 记录在某个时间发生了一次
	 * @param when	发生的时间
	 */
	public void record(long when) {
		record(when, 1);
	}
	
	/**
	 * 记录在当前时间发生了一次
	 */
	public void record(){
		record(System.currentTimeMillis(), 1);
	}

	/**
	 * 记录在当前时间发生了几次
	 * @param times	发生的次数
	 */
	public void record(int times){
		record(System.currentTimeMillis(), times);
	}

	/**
	 * 记录在某个时间发生了几次
	 * @param when
	 */
	public void record(Date when) {
		record(when.getTime(), 1);
	}

	/**
	 * 获得某个时间的计数
	 * @param when
	 * @return
	 */
	public long getCount(Date when) {
		return getCount(when.getTime());
	}

	/**
	 * 获得某时间区间的计数
	 * @param fromWhen
	 * @param toWhen
	 * @return
	 */
	public long getCount(long fromWhen, long toWhen) {
		return getCount(fromWhen, toWhen, true, false);
	}

	/**
	 * 获得某时间区间的计数
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
	 * 获得某时间区间的计数
	 * @param fromWhen
	 * @param toWhen
	 * @return
	 */
	public long getCount(Date fromWhen, Date toWhen) {
		return getCount(fromWhen.getTime(), toWhen.getTime(), true, false);
	}

	/**
	 * 获得某时间区间的计数
	 * @param toWhen	到某个时间为止
	 * @param period	向前推多少时间
	 * @param unit		向前推多少时间的单位
	 * @return
	 */
	public long getCount(long toWhen, long period, TimeUnit unit) {
		return getCount(toWhen - TimeUnit.MILLISECONDS.convert(period, unit), toWhen);
	}

	/**
	 * 获得某时间区间的计数
	 * @param toWhen	到某个时间为止
	 * @param period	向前推多少时间
	 * @param unit		向前推多少时间的单位
	 * @return
	 */
	public long getCount(Date toWhen, long period, TimeUnit unit) {
		return getCount(toWhen.getTime(), period, unit);
	}

	/**
	 * 获得最近一段时间的计数
	 * @param period	向前推多少时间
	 * @param unit		向前推多少时间的单位
	 * @return
	 */
	public long getCount(long period, TimeUnit unit) {
		return getCount(System.currentTimeMillis(), period, unit);
	}

	/**
	 * 删除掉早于一定时间的记录
	 * @param tillWhen
	 */
	public void purge(Date tillWhen) {
		purge(tillWhen.getTime());
	}

	/**
	 * 删除掉早于一定时间的记录
	 * @param baseTime			从什么时间算起
	 * @param periodBefore		多少时间之前的要删掉
	 * @param unit				时间单位
	 */
	public void purge(long baseTime, long periodBefore, TimeUnit unit) {
		purge(baseTime - TimeUnit.MILLISECONDS.convert(periodBefore, unit));
	}

	/**
	 * 删除掉早于一定时间的记录
	 * @param base			从什么时间算起
	 * @param periodBefore	多少时间之前的要删掉
	 * @param unit			时间单位
	 */
	public void purge(Date baseTime, long periodBefore, TimeUnit unit) {
		purge(baseTime.getTime(), periodBefore, unit);
	}

	/**
	 * 删除掉早于一定时间的记录
	 * @param periodBefore	从现在算起，多少时间之前
	 * @param unit			时间单位
	 */
	public void purge(long periodBefore, TimeUnit unit) {
		purge(System.currentTimeMillis() - TimeUnit.MILLISECONDS.convert(periodBefore, unit));
	}

}