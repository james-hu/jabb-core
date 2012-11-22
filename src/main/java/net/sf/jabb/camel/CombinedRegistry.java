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
 * 它使得你可以把好几个Registry合并在一起。
 * <p>
 * When looking up something in it, it will try those encapsulated Registry(s) one by one. 
 * And it has an internal SimpleRegistry, as the last one to try. You can also write code to
 * add entries to the internal SimpleRegistry.
 * <p>
 * 当查找东西的时候，它会逐个尝试这些个Registry。而且，它内置一个SimpleRegistry，
 * 作为最后一个尝试的Registry。你还可以通过内置的SimpleRegistry来利用
 * 程序添加自己额外的内容。
 * 
 * @author Zhengmao HU (James)
 *
 */
public class CombinedRegistry implements Registry {
	protected SimpleRegistry defaultSimpleRegistry;
	protected List<Registry> registryList;
	
	/**
	 * Constructs an instance that contains only an internal SimpleRegistry.<br>
	 * 创建一个实例，它仅含有内置的SimpleRegistry。
	 */
	public CombinedRegistry(){
		defaultSimpleRegistry = new SimpleRegistry();
		registryList = new LinkedList<Registry>();
		registryList.add(defaultSimpleRegistry);
	}
	
	/**
	 * Constructs an instance that contains not only an internal SimpleRegistry, 
	 * but also the Registry specified.<br>
	 * 创建一个除了内置的SimpleRegistry之外，还包含指定Registry的实例。
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
	 * 创建一个除了内置的SimpleRegistry之外，还包含指定的一些Registry的实例。
	 * 
	 * @param registries	The Registry(s) that will be encapsulated.
	 */
	public CombinedRegistry(Registry... registries){
		this();
		addRegistry(registries);
	}
	
	/**
	 * Adds a Registry which will be put after all others but just before the internal SimpleRegistry.<br>
	 * 添加一个Registry，它的位置会位于其他Registry之后，但是在缺省的SimpleRegistry之前。
	 */
	public void addRegistry(Registry registry){
		registryList.add(registryList.size()-1, registry);
	}
	
	/**
	 * Adds several Registry(s) which will be put after all others but just before the internal SimpleRegistry.<br>
	 * 添加一些Registry，它们的位置会位于其他Registry之后，但是在缺省的SimpleRegistry之前。
	 */
	public void addRegistry(Registry... registries){
		for (Registry registry: registries){
			registryList.add(registryList.size()-1, registry);
		}
	}
	

	/** 
	 * Looks up from all encapsulated Registry(s) one by one, and returns the first result found.<br>
	 * 按次序从所封装的Registry中查找，返回第一个找到的结果。
	 * <p>
	 * If no result can be found, null will be returned.
	 * <p>
	 * 如果全都找不到，则返回null。
	 * 
	 * @see org.apache.camel.spi.Registry#lookup(java.lang.String)
	 */
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
	 * 按次序从所封装的Registry中查找，返回第一个找到的结果。
	 * <p>
	 * If no result can be found, null will be returned.
	 * <p>
	 * 如果全都找不到，则返回null。
	 * 
	 * @see org.apache.camel.spi.Registry#lookup(java.lang.String, java.lang.Class)
	 */
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
	 * Looks up from all encapsulated Registry(s) one by one, and returns all the result found.<br>
	 * 按次序从所封装的Registry中查找，并返回所有能找到的结果。
	 * <p>
	 * If no result can be found, an empty Map will be returned.
	 * <p>
	 * 如果全都找不到，则返回一个空的Map。
	 * 
	 * @see org.apache.camel.spi.Registry#lookupByType(java.lang.Class)
	 */
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
	 * 获得其内置的缺省SimpleRegistry，其后可以向它进行增删改查操作。
	 * 
	 * @return	The internal SimpleRegistry.
	 */
	public SimpleRegistry getDefaultSimpleRegistry() {
		return defaultSimpleRegistry;
	}

}
