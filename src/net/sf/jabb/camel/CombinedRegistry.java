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
 * 它使得你可以把好几个Registry合并在一起，当查找东西的时候，它会逐个尝试这些个Registry。而且，
 * 它内置一个SimpleRegistry，作为最后一个尝试的Registry。你还可以通过内置的SimpleRegistry来利用
 * 程序添加自己额外的内容。
 * 
 * @author Zhengmao HU (James)
 *
 */
public class CombinedRegistry implements Registry {
	protected SimpleRegistry defaultSimpleRegistry;
	protected List<Registry> registryList;
	
	/**
	 * 创建一个实例，它仅含有内置的SimpleRegistry。
	 */
	public CombinedRegistry(){
		defaultSimpleRegistry = new SimpleRegistry();
		registryList = new LinkedList<Registry>();
		registryList.add(defaultSimpleRegistry);
	}
	
	/**
	 * 创建一个除了内置的SimpleRegistry之外，还包含指定Registry的实例。
	 * @param registry
	 */
	public CombinedRegistry(Registry registry){
		this();
		addRegistry(registry);
	}
	
	/**
	 * 创建一个除了内置的SimpleRegistry之外，还包含指定的一些Registry的实例。
	 * @param registries
	 */
	public CombinedRegistry(Registry... registries){
		this();
		addRegistry(registries);
	}
	
	/**
	 * 添加一个Registry，它的位置会为与其他Registry之后，但是在缺省的SimpleRegistry之前。
	 */
	public void addRegistry(Registry registry){
		registryList.add(registryList.size()-1, registry);
	}
	
	/**
	 * 添加一些Registry，它们的位置会为与其他Registry之后，但是在缺省的SimpleRegistry之前。
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
	 * 获得其内置的缺省SimpleRegistry。获得之后可以向它添加内容。
	 * @return
	 */
	public SimpleRegistry getDefaultSimpleRegistry() {
		return defaultSimpleRegistry;
	}

}
