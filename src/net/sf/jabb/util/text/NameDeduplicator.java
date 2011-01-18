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

package net.sf.jabb.util.text;

import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.lang.mutable.MutableLong;

/**
 * Ϊ�����������Զ�����
 * 
 * @author Zhengmao HU (James)
 *
 */
public class NameDeduplicator {
	protected ConcurrentHashMap<String, MutableLong> names;
	protected String renamePattern;
	
	/**
	 * ����һ��ʵ��
	 * @param postfixPattern	��׺ģ�棬����" (%d)"��"_%d"
	 */
	public NameDeduplicator(String postfixPattern){
		this.renamePattern = "%s" + postfixPattern;
		names = new ConcurrentHashMap<String, MutableLong>();
	}
	
	/**
	 * ����һ��ʵ������" (%d)"��Ϊ��׺ģ�档
	 */
	public NameDeduplicator(){
		this(" (%d)");
	}
	
	public String deduplicate(String name){
		String newName;
		MutableLong id = names.get(name);
		if (id != null){
			id.increment();
			newName = String.format(renamePattern, name, id.longValue());
		}else{
			id = new MutableLong(1);
			names.put(name, id);
			newName = name;
		}
		return newName;
	}

}
