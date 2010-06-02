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

import java.util.NavigableSet;
import java.util.concurrent.ConcurrentSkipListMap;

import org.apache.commons.lang.mutable.MutableLong;

/**
 * 能进行并发计数的放在Map里的计数器，内部实现基于ConcurrentSkipListMap
 * 
 * @author Zhengmao HU (James)
 * @param <K>	Map里key的类型
 *
 */
public class ConcurrentCounters<K> {
	protected ConcurrentSkipListMap<K, MutableLong> counterMap;
	Object recordLock = new Object();
	
	/**
	 * 创建一个实例
	 */
	public ConcurrentCounters(){
		counterMap = new ConcurrentSkipListMap<K, MutableLong>();
	}
	
	/**
	 * 返回底层的Map
	 * @return
	 */
	public ConcurrentSkipListMap<K, MutableLong> getCounterMap(){
		return counterMap;
	}
	
	/**
	 * 获得所有的Key
	 * @return
	 */
	public NavigableSet<K> keySet(){
		return counterMap.keySet();
	}
	
	/**
	 * 取得指定key所对应的值
	 * @param key
	 * @return
	 */
	public MutableLong get(Object key){
		return counterMap.get(key);
	}
	
	
	/**
	 * 把key所对应的值加上value。如果没有key所对应的值，则添加一个，并把值设置为value。
	 * 这个方法在内部是同步的，所以不会有并发问题。
	 * @param key
	 * @param value
	 */
	public void count(K key, long value){
		MutableLong count = null;
		synchronized(recordLock){
			if (counterMap.containsKey(key)){
				count = counterMap.get(key);
			}else{
				counterMap.put(key, new MutableLong(value));
			}
		}
		if (count != null){
			synchronized(count){
				count.add(value);
			}
		}
	}


}
