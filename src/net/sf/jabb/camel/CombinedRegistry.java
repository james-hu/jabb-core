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

package net.sf.jabb.camel;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.camel.impl.SimpleRegistry;
import org.apache.camel.spi.Registry;

/**
 * It enables you to combine several Registry(s) into one.<br>
 * ��ʹ������԰Ѻü���Registry�ϲ���һ��
 * <p>
 * When looking up something in it, it will try those encapsulated Registry(s) one by one. 
 * And it has an internal SimpleRegistry, as the last one to try. You can also write code to
 * add entries to the internal SimpleRegistry.
 * <p>
 * �����Ҷ�����ʱ���������������Щ��Registry�����ң�������һ��SimpleRegistry��
 * ��Ϊ���һ�����Ե�Registry���㻹����ͨ�����õ�SimpleRegistry������
 * ��������Լ���������ݡ�
 * 
 * @author Zhengmao HU (James)
 *
 */
public class CombinedRegistry implements Registry {
	protected SimpleRegistry defaultSimpleRegistry;
	protected List<Registry> registryList;
	
	/**
	 * Constructs an instance that contains only an internal SimpleRegistry.<br>
	 * ����һ��ʵ���������������õ�SimpleRegistry��
	 */
	public CombinedRegistry(){
		defaultSimpleRegistry = new SimpleRegistry();
		registryList = new LinkedList<Registry>();
		registryList.add(defaultSimpleRegistry);
	}
	
	/**
	 * Constructs an instance that contains not only an internal SimpleRegistry, 
	 * but also the Registry specified.<br>
	 * ����һ���������õ�SimpleRegistry֮�⣬������ָ��Registry��ʵ����
	 * 
	 * @param registry	The Registry that will be encapsulated.
	 */
	public CombinedRegistry(Registry registry){
		this();
		addRegistry(registry);
	}
	
	/**
	 * Constructs an instance that contains not only an internal SimpleRegistry, 
	 * but also several Registry(s) specified.<br>
	 * ����һ���������õ�SimpleRegistry֮�⣬������ָ����һЩRegistry��ʵ����
	 * 
	 * @param registries	The Registry(s) that will be encapsulated.
	 */
	public CombinedRegistry(Registry... registries){
		this();
		addRegistry(registries);
	}
	
	/**
	 * Adds a Registry which will be put after all others but just before the internal SimpleRegistry.<br>
	 * ���һ��Registry������λ�û�λ������Registry֮�󣬵�����ȱʡ��SimpleRegistry֮ǰ��
	 */
	public void addRegistry(Registry registry){
		registryList.add(registryList.size()-1, registry);
	}
	
	/**
	 * Adds several Registry(s) which will be put after all others but just before the internal SimpleRegistry.<br>
	 * ���һЩRegistry�����ǵ�λ�û�λ������Registry֮�󣬵�����ȱʡ��SimpleRegistry֮ǰ��
	 */
	public void addRegistry(Registry... registries){
		for (Registry registry: registries){
			registryList.add(registryList.size()-1, registry);
		}
	}
	

	/** 
	 * Looks up from all encapsulated Registry(s) one by one, and returns the first result found.<br>
	 * �����������װ��Registry�в��ң����ص�һ���ҵ��Ľ����
	 * <p>
	 * If no result can be found, null will be returned.
	 * <p>
	 * ���ȫ���Ҳ������򷵻�null��
	 * 
	 * @see org.apache.camel.spi.Registry#lookup(java.lang.String)
	 */
	@Override
	public Object lookup(String name) {
		Object result = null;
		for (Registry reg: registryList){
			result = reg.lookup(name);
			if (result != null){
				break;
			}
		}
		return result;
	}

	/**
	 * Looks up from all encapsulated Registry(s) one by one, and returns the first result found.<br>
	 * �����������װ��Registry�в��ң����ص�һ���ҵ��Ľ����
	 * <p>
	 * If no result can be found, null will be returned.
	 * <p>
	 * ���ȫ���Ҳ������򷵻�null��
	 * 
	 * @see org.apache.camel.spi.Registry#lookup(java.lang.String, java.lang.Class)
	 */
	@Override
	public <T> T lookup(String name, Class<T> type) {
		T result = null;
		for (Registry reg: registryList){
			result = reg.lookup(name, type);
			if (result != null){
				break;
			}
		}
		return result;
	}

	/**
	 * Looks up from all encapsulated Registry(s) one by one, and returns the first result found.<br>
	 * �����������װ��Registry�в��ң����ص�һ���ҵ��Ľ����
	 * <p>
	 * If no result can be found, null will be returned.
	 * <p>
	 * ���ȫ���Ҳ������򷵻�null��
	 * 
	 * @see org.apache.camel.spi.Registry#lookupByType(java.lang.Class)
	 */
	@Override
	public <T> Map<String, T> lookupByType(Class<T> type) {
		Map<String, T> result = new HashMap<String, T>();
		for (Registry reg: registryList){
			Map<String, T> r = reg.lookupByType(type);
			result.putAll(r);
		}
		return result;
	}

	/**
	 * Gets the internal SimpleRegistry which can be manipulated later.<br>
	 * ��������õ�ȱʡSimpleRegistry������������������ɾ�Ĳ������
	 * 
	 * @return	The internal SimpleRegistry.
	 */
	public SimpleRegistry getDefaultSimpleRegistry() {
		return defaultSimpleRegistry;
	}

}
