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

package net.sf.jabb.util.col;

import java.util.Map;
import java.util.TreeMap;

/**
 * An utility to list the content of the Map in a formatted manner.<br>
 * ��Map������ݸ�ʽ������Ĺ��ߡ�
 * 
 * @author Zhengmao HU (James)
 *
 */
public class MapLister {
	/**
	 * Print the content of the Map in a newly created String, 
	 * entries are sorted by key,  formatted as "key\t= value\n".<br>
	 * ��Map�����������String�Map���ÿ��entryռһ�У���key����
	 * ÿ�еĸ�ʽΪ��key\t= value\n����
	 * 
	 * @param map	The Map object for which the content need to be listed.<br>
	 * 				��Ҫ�г����ݵ�Map����
	 * @return 		A String that holds formated content of the Map.<br>
	 * 				���и�ʽ������Map���ݵ�String
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
