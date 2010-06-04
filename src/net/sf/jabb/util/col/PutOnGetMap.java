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

package net.sf.jabb.util.col;

import java.util.Collection;
import java.util.Comparator;
import java.util.Map;
import java.util.NavigableMap;
import java.util.NavigableSet;
import java.util.Set;
import java.util.SortedMap;

/**
 * 把Map进一步封装，使得每次get的时候，如果没有，就自动put。
 * 注意只有这几个方法是同步的：get/put/putAll/remove/clear。
 * 
 * @author Zhengmao HU (James)
 *
 * @param <K>	Map的key的类型
 * @param <V>	Map的value的类型
 */
public class PutOnGetMap<K, V> implements Map<K, V>, SortedMap<K,V>, NavigableMap<K,V>{
	protected Map<K, V> map;
	protected Class<?> valueClass;
	protected Object structureLock = new Object();
	
	/**
	 * 把一个普通的Map封装成“每次get的时候，如果没有，就自动put”
	 * @param originalMap	被封装进来的Map
	 * @param valueClazz	Map的value的类
	 */
	public PutOnGetMap(Map<K, V> originalMap, Class<?> valueClazz){
		map = originalMap;
		valueClass = valueClazz;
	}
	
	/**
	 * 获得最初被封装的那个Map
	 * @return
	 */
	public Map<K, V> getMap(){
		return map;
	}
	

	@SuppressWarnings("unchecked")
	@Override
	public V get(Object key) {
		V result;
		result = map.get(key);
		if (result == null){
			synchronized(structureLock){
				if (!map.containsKey(key)){
					try {
						result = (V)(valueClass.newInstance());
						map.put((K)key, result);
					} catch (Exception e) {
						throw new IllegalArgumentException("Error putting key and new instance of " + valueClass.getCanonicalName(), e);
					} 
				}else{
					result = map.get(key);
				}
			}
		}
		return result;
	}


	@Override
	public V put(K key, V value) {
		V result;
		synchronized(structureLock){
			result = map.put(key, value);
		}
		return result;
	}

	@Override
	public void putAll(Map<? extends K, ? extends V> anotherMap) {
		synchronized(structureLock){
			map.putAll(anotherMap);
		}
	}

	@Override
	public V remove(Object obj) {
		V result;
		synchronized(structureLock){
			result = map.remove(obj);
		}
		return result;
	}

	@Override
	public void clear() {
		synchronized(structureLock){
			map.clear();
		}
	}

	@Override
	public boolean containsKey(Object obj) {
		return map.containsKey(obj);
	}

	@Override
	public boolean containsValue(Object obj) {
		return map.containsValue(obj);
	}

	@Override
	public Set<java.util.Map.Entry<K, V>> entrySet() {
		return map.entrySet();
	}

	@Override
	public boolean isEmpty() {
		return map.isEmpty();
	}

	@Override
	public Set<K> keySet() {
		return map.keySet();
	}

	@Override
	public int size() {
		return map.size();
	}

	@Override
	public Collection<V> values() {
		return map.values();
	}

	@Override
	public Comparator<? super K> comparator() {
		return ((SortedMap<K, V>)map).comparator();
	}

	@Override
	public K firstKey() {
		return ((SortedMap<K, V>)map).firstKey();
	}

	@Override
	public SortedMap<K, V> headMap(K key) {
		return ((SortedMap<K, V>)map).headMap(key);
	}

	@Override
	public K lastKey() {
		return ((SortedMap<K, V>)map).lastKey();
	}

	@Override
	public SortedMap<K, V> subMap(K from, K to) {
		return ((SortedMap<K, V>)map).subMap(from, to);
	}

	@Override
	public SortedMap<K, V> tailMap(K key) {
		return ((SortedMap<K, V>)map).tailMap(key);
	}


	@Override
	public java.util.Map.Entry<K, V> ceilingEntry(K key) {
		return ((NavigableMap<K, V>)map).ceilingEntry(key);
	}


	@Override
	public K ceilingKey(K key) {
		return ((NavigableMap<K, V>)map).ceilingKey(key);
	}


	@Override
	public NavigableSet<K> descendingKeySet() {
		return ((NavigableMap<K, V>)map).descendingKeySet();
	}


	@Override
	public NavigableMap<K, V> descendingMap() {
		return ((NavigableMap<K, V>)map).descendingMap();
	}


	@Override
	public java.util.Map.Entry<K, V> firstEntry() {
		return ((NavigableMap<K, V>)map).firstEntry();
	}


	@Override
	public java.util.Map.Entry<K, V> floorEntry(K key) {
		return ((NavigableMap<K, V>)map).floorEntry(key);
	}


	@Override
	public K floorKey(K key) {
		return ((NavigableMap<K, V>)map).floorKey(key);
	}


	@Override
	public NavigableMap<K, V> headMap(K arg0, boolean arg1) {
		return ((NavigableMap<K, V>)map).headMap(arg0, arg1);
	}


	@Override
	public java.util.Map.Entry<K, V> higherEntry(K arg0) {
		return ((NavigableMap<K, V>)map).higherEntry(arg0);
	}


	@Override
	public K higherKey(K arg0) {
		return ((NavigableMap<K, V>)map).higherKey(arg0);
	}


	@Override
	public java.util.Map.Entry<K, V> lastEntry() {
		return ((NavigableMap<K, V>)map).lastEntry();
	}


	@Override
	public java.util.Map.Entry<K, V> lowerEntry(K arg0) {
		return ((NavigableMap<K, V>)map).lowerEntry(arg0);
	}


	@Override
	public K lowerKey(K arg0) {
		return ((NavigableMap<K, V>)map).lowerKey(arg0);
	}


	@Override
	public NavigableSet<K> navigableKeySet() {
		return ((NavigableMap<K, V>)map).navigableKeySet();
	}


	@Override
	public java.util.Map.Entry<K, V> pollFirstEntry() {
		return ((NavigableMap<K, V>)map).pollFirstEntry();
	}


	@Override
	public java.util.Map.Entry<K, V> pollLastEntry() {
		return ((NavigableMap<K, V>)map).pollLastEntry();
	}


	@Override
	public NavigableMap<K, V> subMap(K arg0, boolean arg1, K arg2, boolean arg3) {
		return ((NavigableMap<K, V>)map).subMap(arg0, arg1, arg2, arg3);
	}


	@Override
	public NavigableMap<K, V> tailMap(K arg0, boolean arg1) {
		return ((NavigableMap<K, V>)map).tailMap(arg0, arg1);
	}

}
