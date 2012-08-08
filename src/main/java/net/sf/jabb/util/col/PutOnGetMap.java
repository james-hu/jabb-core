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
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.Map;
import java.util.NavigableMap;
import java.util.NavigableSet;
import java.util.Set;
import java.util.SortedMap;

/**
 * Encapsulates Map so that a new entry is put in to the Map whenever
 * there is a get for a non-existing entry.<br>
 * ��Map��һ����װ��ʹ��ÿ��get��ʱ�����û�У����Զ�put��
 * <p>
 * Please note that only these methods are synchronized: get/put/putAll/remove/clear.
 * <p>
 * ע��ֻ���⼸��������ͬ���ģ�get/put/putAll/remove/clear��
 * <p>
 * Although PutOnGetMap implemented SortedMap and NavigableMap, 
 * if the encapsulated Map does not support those interfaces, 
 * then if any method of those interfaces was called, Exception will be thrown.
 * <p>
 * ��ȻPutOnGetMapʵ����SortedMap��NavigableMap�ӿڣ�
 * �����������װ��Map����֧����Щ�ӿڣ�
 * ��ô������ʱ������Щ�ӿ������еķ�����ʱ�򣬻��׳�Exception��
 * 
 * @author Zhengmao HU (James)
 * 
 * @deprecated This class was renamed as PutIfAbsentMap. Please use PutIfAbsentMap instead.
 *
 * @param <K>	Type of the key of the Map entries<br>Map��key������
 * @param <V>	Type of the value of the Map entries<br>Map��value������
 */
public class PutOnGetMap<K, V> extends PutIfAbsentMap<K, V> implements SortedMap<K,V>, NavigableMap<K,V>{
	
	/**
	 * Constructs an instance with specified Map instance and value Class.<br>
	 * ����Mapʵ���Լ�value���࣬��һ����ͨ��Mapʵ����װ�ɡ�ÿ��get��ʱ�����û�У����Զ�put����
	 * 
	 * @param originalMap	The Map instance that will be encapsulated.<br>
	 * 						����װ������Mapʵ����
	 * @param valueClazz	Class of the value of the Map entry.<br>
	 * 						Map��value���ࡣ
	 */
	public PutOnGetMap(Map<K, V> originalMap, Class<? extends V> valueClazz){
		super(originalMap, valueClazz);
	}
	
	/**
	 * Constructs an instance with specified Map Class and value Class.<br>
	 * ����Map�����Լ�value���࣬��װ��һ����ÿ��get��ʱ�����û�У����Զ�put����ʵ����
	 * 
	 * @param mapClazz		The Map Class that its instance will be created and encapsulated.<br>
	 * 						����װ������Map����
	 * @param valueClazz	Class of the value of the Map entry.<br>
	 * 						Map��value���ࡣ
	 */
	@SuppressWarnings("unchecked")
	public PutOnGetMap(@SuppressWarnings("rawtypes") Class<? extends Map> mapClazz, Class<? extends V> valueClazz){
		super(mapClazz, valueClazz);
	}

	
	/**
	 * Constructs an instance with specified Map Class, value Class 
	 * and the constructor parameter of the value Class.<br>
	 * ����Map���ࡢvalue�����Լ�value�๹�췽���Ĳ�������װ��һ����ÿ��get��ʱ�����û�У����Զ�put����ʵ����
	 * 
	 * @param mapClazz		The Map Class that its instance will be created and encapsulated.<br>
	 * 						����װ������Map����
	 * @param valueClazz	Class of the value of the Map entry.<br>
	 * 						Map��value���ࡣ
	 * @param valueParam	Constructor parameter for the value Class.<br>	
	 * 						value����Ĺ��췽������Ҫ�Ĳ�����
	 */
	@SuppressWarnings("unchecked")
	public PutOnGetMap(@SuppressWarnings("rawtypes") Class<? extends Map> mapClazz, Class<? extends V> valueClazz, Object valueParam){
		super(mapClazz, valueClazz, valueParam);
	}

	
	/**
	 * Constructs an instance with specified Map Class, value Class 
	 * and the constructor parameter of the value Class.<br>
	 * ����Map���ࡢvalue�����Լ�value�๹�췽���Ĳ�������װ��һ����ÿ��get��ʱ�����û�У����Զ�put����ʵ����
	 * 
	 * @param mapClazz		The Map Class that its instance will be created and encapsulated.<br>
	 * 						����װ������Map����
	 * @param valueClazz	Class of the value of the Map entry.<br>
	 * 						Map��value���ࡣ
	 * @param valueParams	Constructor parameter for the value Class.<br>	
	 * 						value����Ĺ��췽������Ҫ�Ĳ�����
	 */
	@SuppressWarnings("unchecked")
	public PutOnGetMap(@SuppressWarnings("rawtypes") Class<? extends Map> mapClazz, Class<? extends V> valueClazz, Object... valueParams){
		super(mapClazz, valueClazz, valueParams);
	}


	public Comparator<? super K> comparator() {
		return ((SortedMap<K, V>)map).comparator();
	}

	public K firstKey() {
		return ((SortedMap<K, V>)map).firstKey();
	}

	public SortedMap<K, V> headMap(K key) {
		return ((SortedMap<K, V>)map).headMap(key);
	}

	public K lastKey() {
		return ((SortedMap<K, V>)map).lastKey();
	}

	public SortedMap<K, V> subMap(K from, K to) {
		return ((SortedMap<K, V>)map).subMap(from, to);
	}

	public SortedMap<K, V> tailMap(K key) {
		return ((SortedMap<K, V>)map).tailMap(key);
	}


	public java.util.Map.Entry<K, V> ceilingEntry(K key) {
		return ((NavigableMap<K, V>)map).ceilingEntry(key);
	}


	public K ceilingKey(K key) {
		return ((NavigableMap<K, V>)map).ceilingKey(key);
	}


	public NavigableSet<K> descendingKeySet() {
		return ((NavigableMap<K, V>)map).descendingKeySet();
	}


	public NavigableMap<K, V> descendingMap() {
		return ((NavigableMap<K, V>)map).descendingMap();
	}


	public java.util.Map.Entry<K, V> firstEntry() {
		return ((NavigableMap<K, V>)map).firstEntry();
	}


	public java.util.Map.Entry<K, V> floorEntry(K key) {
		return ((NavigableMap<K, V>)map).floorEntry(key);
	}


	public K floorKey(K key) {
		return ((NavigableMap<K, V>)map).floorKey(key);
	}


	public NavigableMap<K, V> headMap(K arg0, boolean arg1) {
		return ((NavigableMap<K, V>)map).headMap(arg0, arg1);
	}


	public java.util.Map.Entry<K, V> higherEntry(K arg0) {
		return ((NavigableMap<K, V>)map).higherEntry(arg0);
	}


	public K higherKey(K arg0) {
		return ((NavigableMap<K, V>)map).higherKey(arg0);
	}


	public java.util.Map.Entry<K, V> lastEntry() {
		return ((NavigableMap<K, V>)map).lastEntry();
	}


	public java.util.Map.Entry<K, V> lowerEntry(K arg0) {
		return ((NavigableMap<K, V>)map).lowerEntry(arg0);
	}


	public K lowerKey(K arg0) {
		return ((NavigableMap<K, V>)map).lowerKey(arg0);
	}


	public NavigableSet<K> navigableKeySet() {
		return ((NavigableMap<K, V>)map).navigableKeySet();
	}


	public java.util.Map.Entry<K, V> pollFirstEntry() {
		return ((NavigableMap<K, V>)map).pollFirstEntry();
	}


	public java.util.Map.Entry<K, V> pollLastEntry() {
		return ((NavigableMap<K, V>)map).pollLastEntry();
	}


	public NavigableMap<K, V> subMap(K arg0, boolean arg1, K arg2, boolean arg3) {
		return ((NavigableMap<K, V>)map).subMap(arg0, arg1, arg2, arg3);
	}


	public NavigableMap<K, V> tailMap(K arg0, boolean arg1) {
		return ((NavigableMap<K, V>)map).tailMap(arg0, arg1);
	}

}
