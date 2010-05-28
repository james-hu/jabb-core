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

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

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
	 * 根据ID，获取ID所对应的BasicFrequencyCounter
	 * @param id
	 * @return
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
	public void record(long when, int times) {
		for (BasicFrequencyCounter counter: counters.values()){
			counter.record(when, times);
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
		
		boolean isFirst = true;
		for (Object id: counters.keySet()){
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
		sb.setLength(sb.length() - 1);
		return sb.toString();
	}
	

}
