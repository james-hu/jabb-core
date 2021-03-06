/*
Copyright 2010-2012, 2014 Zhengmao HU (James)

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

import java.io.PrintWriter;
import java.io.StringWriter;
import java.math.BigInteger;
import java.util.HashMap;
import java.util.Map;
import java.util.NavigableMap;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

import net.sf.jabb.util.col.PutIfAbsentMap;

/**
 * 频次计数器
 * 
 * @author Zhengmao HU (James)
 *
 */
public class BasicFrequencyCounter extends FrequencyCounter {
	protected PutIfAbsentMap<Long, AtomicLong> counters;
	protected long granularity;
	protected long purgeBefore;
	protected Object recordLock = new Object();
	

	/**
	 * 创建一个实例，如果purgePeriod参数不为0则会自动进行历史数据清除。
	 * The granularity must not exceed one hour.
	 * @param granularity	计数颗粒度
	 * @param unit			颗粒度的单位
	 * @param purgePeriod	历史数据的保留时间长度
	 * @param purgeUnit		历史数据保留时间长度的单位
	 */
	public BasicFrequencyCounter(long granularity, TimeUnit unit,
			long purgePeriod, TimeUnit purgeUnit){
		Map<Long, AtomicLong> map = null;
		this.granularity = TimeUnit.MILLISECONDS.convert(granularity, unit);
		if (this.granularity > TimeUnit.MILLISECONDS.convert(1, TimeUnit.HOURS)){
			throw new IllegalArgumentException("The granularity cannot exceed 1 hour.");
		}
		if (purgePeriod == 0){
			this.purgeBefore = 0;
			map = new HashMap<Long, AtomicLong>();	// better performance, but doesn't no guarantee on ordering
		} else {
			this.purgeBefore = TimeUnit.MILLISECONDS.convert(purgePeriod, purgeUnit);
			map = new ConcurrentSkipListMap<Long, AtomicLong>();	// ordering guaranteed
		}
		counters = new PutIfAbsentMap<Long, AtomicLong>(map, AtomicLong.class);
	}

	/**
	 * 创建一个不对历史数据进行自动清除的实例。
	 * @param granularity	计数颗粒度
	 * @param unit			颗粒度的单位
	 */
	public BasicFrequencyCounter(long granularity, TimeUnit unit){
		this(granularity, unit, 0, null);
	}
	
	/**
	 * 创建一个不对历史数据进行自动清除，且不针对时间颗粒度
	 * （比如，对于一批短信，对不同长度短信各有多少条进行统计）的实例。
	 */
	public BasicFrequencyCounter(){
		this(1, TimeUnit.MILLISECONDS, 0, null);
	}

	/**
	 * 根据配置信息创建一个实例。
	 * @param definition	配置信息
	 */
	public BasicFrequencyCounter(FrequencyCounterDefinition definition){
		this(definition.getGranularity(), TimeUnit.MILLISECONDS,
				definition.getPurgeBefore(), TimeUnit.MILLISECONDS);
	}
	
	/**
	 * 记录在某时刻发生了多少次。
	 * @param when	发生的时刻
	 * @param times	次数
	 */
	@Override
	public void count(long when, int times){
		long recWhen = when - (when % granularity);
		counters.get(recWhen).addAndGet(times);
		if (purgeBefore != 0){
			purge(when - purgeBefore);
		}
	}
	
	/**
	 * 获得全部计数统计
	 * @return	返回的Map的Key是以毫秒为单位的时间，value是计数值。
	 */
	public Map<Long, AtomicLong> getCounts(){
		return counters.getMap();
	}
	
	/**
	 * Get the summary value of all the counts
	 * @return the summary value of all the counts
	 */
	public BigInteger getTotalCounts(){
		BigInteger result = BigInteger.ZERO;
		for (AtomicLong c: counters.getMap().values()){
			result = result.add(BigInteger.valueOf(c.get()));
		}
		return result;
	}
	
	/**
	 * 获取在某时刻的计数值。
	 * @param when	时刻
	 * @return		统计数
	 */
	@Override
	public long getCount(long when){
		Long recWhen = when - (when % granularity);
		AtomicLong times = counters.get(recWhen);
		return times == null ? 0 : times.longValue(); 
	}
	
	/**
	 * 获得在指定时间范围内的总频次.
	 * Please not that this method is not usable if the counter was created with a zero purgePeriod.
	 * @param fromWhen		开始时间
	 * @param toWhen		结束时间
	 * @param fromInclusive	是否包含开始时间
	 * @param toInclusive	是否包含结束时间
	 * @return				统计数
	 */
	@Override
	public long getCount(long fromWhen, long toWhen,  boolean fromInclusive, boolean toInclusive){
		NavigableMap<Long, AtomicLong> range = ((NavigableMap<Long, AtomicLong>)counters.getMap()).subMap(fromWhen, fromInclusive, toWhen, toInclusive);
		long count = 0;
		for(AtomicLong c: range.values()){
			count += c.longValue();
		}
		return count;
	}
	
	/**
	 * 删除掉早于一定时间的记录
	 * @param tillWhen	清除到哪个时间点为止
	 */
	@Override
	public void purge(long tillWhen){
		Long t;
		while((t = ((NavigableMap<Long, AtomicLong>)counters.getMap()).firstKey()) != null && t < tillWhen){
			counters.remove(t);
		}
	}
	
	/**
	 * 转成String
	 */
	@Override
	public String toString(){
		StringWriter sw = new StringWriter();
		PrintWriter pw = new PrintWriter(sw);
		pw.format("granularity=%d(ms) purgeBefore=%d(ms)\n", granularity, purgeBefore);
		
		boolean isFirst = true;
		for (Long t: new TreeSet<Long>(counters.keySet())){
			if (isFirst){
				isFirst = false;
			}else{
				pw.print('\n');
			}
			pw.format(" %1$tY%1$tm%1$td %1$tH:%1$tM:%1$tS.%1$tL (%1$20d) -> %2$20d", t, counters.get(t).longValue());
		}
		
		return sw.toString();
	}

}
