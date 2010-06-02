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
 * �ܽ��в��������ķ���Map��ļ��������ڲ�ʵ�ֻ���ConcurrentSkipListMap
 * 
 * @author Zhengmao HU (James)
 * @param <K>	Map��key������
 *
 */
public class ConcurrentCounters<K> {
	protected ConcurrentSkipListMap<K, MutableLong> counterMap;
	Object recordLock = new Object();
	
	/**
	 * ����һ��ʵ��
	 */
	public ConcurrentCounters(){
		counterMap = new ConcurrentSkipListMap<K, MutableLong>();
	}
	
	/**
	 * ���صײ��Map
	 * @return
	 */
	public ConcurrentSkipListMap<K, MutableLong> getCounterMap(){
		return counterMap;
	}
	
	/**
	 * ������е�Key
	 * @return
	 */
	public NavigableSet<K> keySet(){
		return counterMap.keySet();
	}
	
	/**
	 * ȡ��ָ��key����Ӧ��ֵ
	 * @param key
	 * @return
	 */
	public MutableLong get(Object key){
		return counterMap.get(key);
	}
	
	
	/**
	 * ��key����Ӧ��ֵ����value�����û��key����Ӧ��ֵ�������һ��������ֵ����Ϊvalue��
	 * ����������ڲ���ͬ���ģ����Բ����в������⡣
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
