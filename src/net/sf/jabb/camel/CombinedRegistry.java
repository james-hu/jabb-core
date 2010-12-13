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

package net.sf.jabb.camel;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.camel.impl.SimpleRegistry;
import org.apache.camel.spi.Registry;

/**
 * ��ʹ������԰Ѻü���Registry�ϲ���һ�𣬵����Ҷ�����ʱ���������������Щ��Registry�����ң�
 * ������һ��SimpleRegistry����Ϊ���һ�����Ե�Registry���㻹����ͨ�����õ�SimpleRegistry������
 * ��������Լ���������ݡ�
 * 
 * @author Zhengmao HU (James)
 *
 */
public class CombinedRegistry implements Registry {
	protected SimpleRegistry defaultSimpleRegistry;
	protected List<Registry> registryList;
	
	/**
	 * ����һ��ʵ���������������õ�SimpleRegistry��
	 */
	public CombinedRegistry(){
		defaultSimpleRegistry = new SimpleRegistry();
		registryList = new LinkedList<Registry>();
		registryList.add(defaultSimpleRegistry);
	}
	
	/**
	 * ����һ���������õ�SimpleRegistry֮�⣬������ָ��Registry��ʵ����
	 * @param registry
	 */
	public CombinedRegistry(Registry registry){
		this();
		addRegistry(registry);
	}
	
	/**
	 * ����һ���������õ�SimpleRegistry֮�⣬������ָ����һЩRegistry��ʵ����
	 * @param registries
	 */
	public CombinedRegistry(Registry... registries){
		this();
		addRegistry(registries);
	}
	
	/**
	 * ���һ��Registry������λ�û�Ϊ������Registry֮�󣬵�����ȱʡ��SimpleRegistry֮ǰ��
	 */
	public void addRegistry(Registry registry){
		registryList.add(registryList.size()-1, registry);
	}
	
	/**
	 * ���һЩRegistry�����ǵ�λ�û�Ϊ������Registry֮�󣬵�����ȱʡ��SimpleRegistry֮ǰ��
	 */
	public void addRegistry(Registry... registries){
		for (Registry registry: registries){
			registryList.add(registryList.size()-1, registry);
		}
	}
	

	/* (non-Javadoc)
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

	/* (non-Javadoc)
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

	/* (non-Javadoc)
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
	 * ��������õ�ȱʡSimpleRegistry�����֮���������������ݡ�
	 * @return
	 */
	public SimpleRegistry getDefaultSimpleRegistry() {
		return defaultSimpleRegistry;
	}

}
