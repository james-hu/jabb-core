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

import java.util.concurrent.TimeUnit;

/**
 * 频次计数器的设置信息
 * 
 * @author Zhengmao HU (James)
 *
 */
public class FrequencyCounterDefinition {
	protected Object id;
	protected long granularity;
	protected long purgeBefore;
	
	/**
	 * 创建一个不自动删除历史数据的频次计数器配置信息对象
	 * @param id			频次计数器的ID，如果频次计数器需要被作为PackagedFrequencyCounter中
	 * 						的一部分，那么就有必要设置ID，如果只是单独作为BasicFrequencyCounter
	 * 						使用，那么就可以设置为null。
	 * @param granularity	频次统计的颗粒度，比如30、60。
	 * @param unit			颗粒度的单位，比如TimeUnit.SECONDS、TimeUnit.MINUTES。
	 */
	public FrequencyCounterDefinition(Object id, long granularity, TimeUnit unit){
		this.id = id;
		this.granularity = TimeUnit.MILLISECONDS.convert(granularity, unit);
		this.purgeBefore = 0;
	}

	/**
	 * 创建一个自动删除历史数据的频次计数器配置信息对象
	 * @param id			频次计数器的ID，如果频次计数器需要被作为PackagedFrequencyCounter中
	 * 						的一部分，那么就有必要设置ID，如果只是单独作为BasicFrequencyCounter
	 * 						使用，那么就可以设置为null。
	 * @param granularity	频次统计的颗粒度，比如30、60。
	 * @param unit			颗粒度的单位，比如TimeUnit.SECONDS、TimeUnit.MINUTES。
	 * @param purgePeriod	多少时间之前的历史数据需要自动清除，比如1、3。
	 * @param purgeUnit		purgePeriod的时间单位，比如TimeUnit.HOURS。
	 */
	public FrequencyCounterDefinition(Object id, long granularity, TimeUnit unit,
			long purgePeriod, TimeUnit purgeUnit){
		this(id, granularity, unit);
		this.purgeBefore = TimeUnit.MILLISECONDS.convert(purgePeriod, purgeUnit);
	}

	/**
	 * 获得颗粒度
	 * @return the granularity
	 */
	public long getGranularity() {
		return granularity;
	}

	/**
	 * 获得保留多少时间之内的历史数据
	 * @return 以毫秒为单位的保留时间长度
	 */
	public long getPurgeBefore() {
		return purgeBefore;
	}

	/**
	 * 获得ID
	 * @return the id
	 */
	public Object getId() {
		return id;
	}
}
