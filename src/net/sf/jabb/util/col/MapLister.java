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

import java.util.Map;
import java.util.TreeMap;

/**
 * 把Map里的内容格式化输出的工具。
 * <p>
 * The utility to list the content of Map in a formatted manner. 
 * 
 * @author Zhengmao HU (James)
 *
 */
public class MapLister {
	/**
	 * 把Map里的内容列在String里，Map里的每个entry占一行，按key排序，每行的格式为“key\t= value\n”。
	 * <p>
	 * List the content of Map to a newly created String, entries are sorted by key,  formatted as "key\t= value\n".
	 * 
	 * @param map	需要列出内容的Map对象
	 * 				<br>The Map object for which the content need to be listed
	 * @return 含有格式化过的Map内容的String
	 * 			<br>A String that holds formated content of the Map
	 */
	public static String listToString(Map<?, ?> map){
		TreeMap<?, ?> tm = new TreeMap<Object, Object>(map);
		StringBuilder sb = new StringBuilder();
		for (Map.Entry<?, ?> item: tm.entrySet()){
			Object k = item.getKey();
			Object v = item.getValue();
			sb.append(k == null ? "null" : k.toString());
			sb.append("\t= ");
			sb.append(v == null ? "null" : v.toString());
			sb.append('\n');
		}
		return sb.toString();
	}
}
