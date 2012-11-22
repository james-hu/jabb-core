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

package net.sf.jabb.util.text;

import java.util.concurrent.ConcurrentHashMap;

import net.sf.jabb.util.col.PutIfAbsentMap;
import net.sf.jabb.util.col.PutOnGetMap;
import net.sf.jabb.util.thread.Sequencer;


/**
 * This utility class can rename names by appending numbers to avoid name duplication, 
 * it is multi-thread safe.<br>
 * 这个工具类可以通过给名称后面添加数字的方式来为避免重名而自动改名，它是多线程安全的。
 * 
 * @author Zhengmao HU (James)
 *
 */
public class NameDeduplicator {
	protected PutIfAbsentMap<String, Sequencer> nameSequencers;
	protected String renamePattern;
	
	/**
	 * Constructs an instance with specified postfix pattern.<br>
	 * 创建一个实例，使用指定的后缀模板。
	 * 
	 * @param postfixPattern	Patten for the postfix to be appended, 
	 * 							for example, <code>" (%d)"</code> or <code>"_%d"</code><br>
	 * 							后缀模版，比如"<code> (%d)</code>"或"<code>_%d</code>"。
	 */
	public NameDeduplicator(String postfixPattern){
		this.renamePattern = "%s" + postfixPattern;
		nameSequencers = new PutIfAbsentMap<String, Sequencer>(new ConcurrentHashMap<String, Sequencer>(), Sequencer.class);
	}
	
	/**
	 * Constructs an instance with <code>" (%d)"</code> as the postfix pattern.<br>
	 * 创建一个实例，以"<code> (%d)</code>"作为后缀模版。
	 */
	public NameDeduplicator(){
		this(" (%d)");
	}
	
	/**
	 * Ensure a unique name, rename if needed.<br>
	 * 确保名称不重复，如果有必要就改名。
	 * <p>
	 * This method is multi-threads safe.
	 * <p>
	 * 这个方法是多线程安全的。
	 * 
	 * @param name	The original name<br>原名称
	 * @return		Same as the original name if or renamed<br>与原名称相同，或被自动改名
	 */
	public String deduplicate(String name){
		long id = nextId(name);
		if (id == 0){	// the first one
			return name;
		}else{
			return String.format(renamePattern, name, id);
		}
	}
	
	/**
	 * Get the next sequential id for specified name.<br>
	 * 取得指定名称的下一个不重复Id值。
	 * <p>
	 * For a specified name, the first time of invocation will return 0, 
	 * the second time return 1, the third time return 2, so on and so forth.
	 * This method is multi-threads safe.
	 * <p>
	 * 对于同一个名称，第一次调用返回的是0，第二次是1，第三次是2，依此类推。
	 * 这个方法是多线程安全的。
	 * 
	 * @param name	The specified name<br>指定的名称
	 * @return		next sequential id<br>下一个不重复Id值
	 */
	public long nextId(String name){
		return nameSequencers.get(name).next();
	}

}
