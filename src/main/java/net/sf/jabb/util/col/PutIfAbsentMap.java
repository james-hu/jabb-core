/*
Copyright 2010-2012 Zhengmao HU (James)

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

import java.lang.reflect.Constructor;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Encapsulates Map so that a new entry is put in to the Map whenever
 * there is a get for a non-existing entry.<br>
 * 把Map进一步封装，使得每次get的时候，如果没有，就自动put。
 * <p>
 * Please note that only these methods are synchronized: get/put/putAll/remove/clear.
 * <p>
 * 注意只有这几个方法是同步的：get/put/putAll/remove/clear。
 * <p>
 * 
 * @author Zhengmao HU (James)
 *
 * @param <K>	Type of the key of the Map entries<br>Map的key的类型
 * @param <V>	Type of the value of the Map entries<br>Map的value的类型
 */
public class PutIfAbsentMap<K, V> implements Map<K, V>{
	protected Map<K, V> map;
	protected MapValueFactory<K, V> valueFactory;
	protected Object structureLock = new Object();
	
	/**
	 * Constructor.
	 * @param originalMap	The map to be encapsulated.
	 * @param valueFactory	The factory for creating values to be put into this map.
	 */
	public PutIfAbsentMap(Map<K, V> originalMap, MapValueFactory<K, V> valueFactory){
		this.map = originalMap;
		this.valueFactory = valueFactory;
	}
	
	/**
	 * Constructs an instance with specified Map instance and value Class.<br>
	 * 给定Map实例以及value的类，把一个普通的Map实例封装成“每次get的时候，如果没有，就自动put”。
	 * 
	 * @param originalMap	The Map instance that will be encapsulated.<br>
	 * 						被封装进来的Map实例。
	 * @param valueClazz	Class of the value of the Map entry.<br>
	 * 						Map的value的类。
	 * @throws RuntimeException If no non-argument constructor can be found in valueClazz.
	 */
	public PutIfAbsentMap(Map<K, V> originalMap, final Class<? extends V> valueClazz) {
		this(originalMap, new MapValueFactory<K, V>(){
			@Override
			public V createValue(K key) {
				try {
					return valueClazz.newInstance();
				} catch (Exception e) {
					throw new RuntimeException("Cannot create new instance for value class.", e);
				}
			}
			
		});
	}
	
	
	/**
	 * Constructs an instance with specified Map instance and value Class.<br>
	 * 给定Map实例以及value的类，把一个普通的Map实例封装成“每次get的时候，如果没有，就自动put”。
	 * 
	 * @param originalMap	The Map instance that will be encapsulated.<br>
	 * 						被封装进来的Map实例。
	 * @param valueClazz	Class of the value of the Map entry.<br>
	 * 						Map的value的类。
	 * @param valueParam	Parameter to be passed in to the constructor of valueClazz.
	 * @throws RuntimeException 	if no correct constructor in valueClazz can be found.
	 */
	public PutIfAbsentMap(Map<K, V> originalMap, final Class<? extends V> valueClazz, final Object valueParam){
		this(originalMap, new MapValueFactory<K, V>(){
			Constructor<? extends V> constructor;
			{
				try{
					constructor = valueParam == null? null : valueClazz.getConstructor(new Class<?>[] {valueParam.getClass()});
				}catch(Exception e){
					throw new RuntimeException ("Cannot access the constructor of value class.", e);
				}
			}
			@Override
			public V createValue(K key) {
				try {
					if (constructor == null){
						return valueClazz.newInstance();
					}else{
						return constructor.newInstance(valueParam);
					}
				} catch (Exception e) {
					throw new RuntimeException("Cannot create new instance for value class.", e);
				}
			}
			
		});
	}
	
	
	protected static Map instantiateMap(Class<? extends Map> mapClazz){
		try {
			return mapClazz.newInstance();
		} catch (Exception e) {
			throw new RuntimeException("Cannot instantiate map class.", e);
		}
	}
	
	/**
	 * Constructor.
	 * @param mapClazz		The class type of the encapsulated class.
	 * @param valueFactory	The factory of values to be put into this map.
	 * @throws RuntimeException	if cannot instantiate a new map instance.
	 */
	public PutIfAbsentMap(Class<? extends Map> mapClazz, MapValueFactory<K, V> valueFactory){
		this(instantiateMap(mapClazz), valueFactory);
	}

	/**
	 * Constructor.
	 * @param mapClazz		The class type of the encapsulated class.
	 * @param valueClazz	The class type of the value class.
	 * @throws RuntimeException	if cannot instantiate a new map instance.
	 * @throws RuntimeException 	if no non-argument constructor can be found in valueClazz
	 */
	public PutIfAbsentMap(Class<? extends Map> mapClazz, final Class<? extends V> valueClazz){
		this(instantiateMap(mapClazz), valueClazz);
	}
	
	/**
	 * Constructor.
	 * @param mapClazz		The class type of the encapsulated class.
	 * @param valueClazz	The class type of the value class.
	 * @param valueParam	Parameter that will be passed in to the constructor of valueClazz
	 * @throws RuntimeException	if cannot instantiate a new map instance.
	 * @throws RuntimeException 	if no non-argument constructor can be found in valueClazz
	 */
	public PutIfAbsentMap(Class<? extends Map> mapClazz, final Class<? extends V> valueClazz, final Object valueParam){
		this(instantiateMap(mapClazz), valueClazz, valueParam);
	}
	
	
	/**
	 * Constructor. A HashMap will be created and encapsulated.
	 * @param valueFactory	The factory of map values.
	 */
	public PutIfAbsentMap(MapValueFactory<K, V> valueFactory){
		this(new HashMap<K, V>(), valueFactory);
	}
	
	/**
	 * Constructor. A HashMap will be created and encapsulated.
	 * @param valueClazz	Type of the value classes
	 * @throws NoSuchMethodException	if valueClazz does not have a non-argument constructor.
	 */
	public PutIfAbsentMap(final Class<? extends V> valueClazz) throws NoSuchMethodException{
		this(new HashMap<K, V>(), valueClazz);
	}
	
	/**
	 * Constructor. A HashMap will be created and encapsulated.
	 * @param valueClazz	Type of the value classes
	 * @param valueParam	Parameter that will be passed in to the constructor of valueClazz
	 * @throws NoSuchMethodException	if valueClazz does not have a non-argument constructor.
	 */
	public PutIfAbsentMap(final Class<? extends V> valueClazz, final Object valueParam){
		this(new HashMap<K, V>(), valueClazz, valueParam);
	}
	

	/**
	 * Get the encapsulated Map instance.<br>
	 * 获得最初被封装的那个Map。
	 * @return The map instance that is encapsulated inside.
	 */
	public Map<K, V> getMap(){
		return map;
	}
	

	/**
	 * Get the value object corresponding to the key object specified, 
	 * if such entry does not exist in the Map, then create one and put
	 * into the Map and return the value object in the newly created entry.<br>
	 * 取得key所对应的value，如果目前在Map里没有，则在Map里新建一个并返回新建
	 * 的这个value对象。
	 * 
	 * @param key	The key object that will be used to look for the value object.
	 */
	@Override
	@SuppressWarnings("unchecked")
	public V get(Object key) {
		V result;
		result = map.get(key);
		if (result == null){
			synchronized(structureLock){
				if (!map.containsKey(key)){
					try {
						result = valueFactory.createValue((K)key);
						map.put((K) key, result);
					} catch (Exception e) {
						throw new RuntimeException("Error creating or puting value for the map.", e);
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


}
