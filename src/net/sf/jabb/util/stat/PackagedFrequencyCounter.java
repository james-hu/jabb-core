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

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

/**
 * 打包封装好的频次计数工具
 * 
 * @author Zhengmao HU (James)
 *
 */
public class PackagedFrequencyCounter extends FrequencyCounter{
	protected Map<Object, BasicFrequencyCounter> counters;
	
	/**
	 * 创建包含多个BasicFrequencyCounter对象的计数器组合
	 * @param counterDefinitions	各个BasicFrequencyCounter的配置信息，请注意它们的ID必须设置
	 */
	public PackagedFrequencyCounter(Collection<FrequencyCounterDefinition> counterDefinitions){
		counters = new HashMap<Object, BasicFrequencyCounter>(counterDefinitions.size());
		for (FrequencyCounterDefinition def: counterDefinitions){
			BasicFrequencyCounter counter = new BasicFrequencyCounter(def);
			counters.put(def.getId(), counter);
		}
	}
	
	/**
	 * 创建包含多个BasicFrequencyCounter对象的计数器组合
	 * @param counterDefinitions	各个BasicFrequencyCounter的配置信息，请注意它们的ID必须设置
	 */
	public PackagedFrequencyCounter(FrequencyCounterDefinition... counterDefinitions){
		counters = new HashMap<Object, BasicFrequencyCounter>(counterDefinitions.length);
		for (FrequencyCounterDefinition def: counterDefinitions){
			BasicFrequencyCounter counter = new BasicFrequencyCounter(def);
			counters.put(def.getId(), counter);
		}
	}

	/**
	 * 根据ID，获取ID所对应的BasicFrequencyCounter
	 * @param id	要获得的BasicFrequencyCounter的ID
	 * @return		与指定ID所对应的BasicFrequencyCounter
	 */
	public BasicFrequencyCounter getCounter(Object id){
		return counters.get(id);
	}
	

	@Override
	public void purge(long tillWhen) {
		for (BasicFrequencyCounter counter: counters.values()){
			counter.purge(tillWhen);
		}
	}

	@Override
	public void count(long when, int times) {
		for (BasicFrequencyCounter counter: counters.values()){
			counter.count(when, times);
		}
		
	}
	


	@Override
	public long getCount(long when){
		throw new UnsupportedOperationException("Please use getCounter(id).getCount(...) instead.");
	}

	@Override
	public long getCount(long fromWhen, long toWhen, boolean fromInclusive,
			boolean toInclusive) {
		throw new UnsupportedOperationException("Please use getCounter(id).getCount(...) instead.");
	}
	
	/**
	 * 转为字符串
	 */
	public String toString(){
		StringBuilder sb = new StringBuilder();
		Set<Object> keySet = new TreeSet<Object>();
		keySet.addAll(counters.keySet());
		
		boolean isFirst = true;
		for (Object id: keySet){
			if (isFirst){
				isFirst = false;
			}else{
				sb.append('\n');
			}
			sb.append("id=\"");
			sb.append(id);
			sb.append("\" ");
			sb.append(counters.get(id));
		}
		return sb.toString();
	}
	

}
