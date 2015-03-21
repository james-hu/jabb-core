/*
Copyright 2012, 2015 James Hu

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
import java.util.Map;

/**
 * Get value from Maps according to predefined key list.
 * @author James Hu
 *
 */
public class MapGetter<V> {
	protected Object[] keys;
	
	public MapGetter(Object... keys){
		this.keys = keys;
	}
	
	public MapGetter(Collection<? extends Object> keyList){
		keys = keyList.toArray();
	}
	
	/**
	 * Get the first matching value in the map.
	 * @param map
	 * @return		null if nothing found
	 */
	public V get(Map<?, V> map){
		for (Object k : keys){
			V v = map.get(k);
			if (v != null){
				return v;
			}
		}
		return null;
	}

}
