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

package net.sf.jabb.util.text;

import java.util.concurrent.ConcurrentHashMap;
import net.sf.jabb.util.col.PutOnGetMap;
import net.sf.jabb.util.thread.Sequencer;


/**
 * This utility class can rename names by appending numbers to avoid name duplication, 
 * it is multi-thread safe.<br>
 * ������������ͨ�������ƺ���������ֵķ�ʽ��Ϊ�����������Զ����������Ƕ��̰߳�ȫ�ġ�
 * 
 * @author Zhengmao HU (James)
 *
 */
public class NameDeduplicator {
	protected PutOnGetMap<String, Sequencer> nameSequencers;
	protected String renamePattern;
	
	/**
	 * Constructs an instance with specified postfix pattern.<br>
	 * ����һ��ʵ����ʹ��ָ���ĺ�׺ģ�塣
	 * 
	 * @param postfixPattern	Patten for the postfix to be appended, 
	 * 							for example, <code>" (%d)"</code> or <code>"_%d"</code><br>
	 * 							��׺ģ�棬����"<code> (%d)</code>"��"<code>_%d</code>"��
	 */
	public NameDeduplicator(String postfixPattern){
		this.renamePattern = "%s" + postfixPattern;
		nameSequencers = new PutOnGetMap<String, Sequencer>(new ConcurrentHashMap<String, Sequencer>(), Sequencer.class);
	}
	
	/**
	 * Constructs an instance with <code>" (%d)"</code> as the postfix pattern.<br>
	 * ����һ��ʵ������"<code> (%d)</code>"��Ϊ��׺ģ�档
	 */
	public NameDeduplicator(){
		this(" (%d)");
	}
	
	/**
	 * Ensure a unique name, rename if needed.<br>
	 * ȷ�����Ʋ��ظ�������б�Ҫ�͸�����
	 * <p>
	 * This method is multi-threads safe.
	 * <p>
	 * ��������Ƕ��̰߳�ȫ�ġ�
	 * 
	 * @param name	The original name<br>ԭ����
	 * @return		Same as the original name if or renamed<br>��ԭ������ͬ�����Զ�����
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
	 * ȡ��ָ�����Ƶ���һ�����ظ�Idֵ��
	 * <p>
	 * For a specified name, the first time of invocation will return 0, 
	 * the second time return 1, the third time return 2, so on and so forth.
	 * This method is multi-threads safe.
	 * <p>
	 * ����ͬһ�����ƣ���һ�ε��÷��ص���0���ڶ�����1����������2���������ơ�
	 * ��������Ƕ��̰߳�ȫ�ġ�
	 * 
	 * @param name	The specified name<br>ָ��������
	 * @return		next sequential id<br>��һ�����ظ�Idֵ
	 */
	public long nextId(String name){
		return nameSequencers.get(name).next();
	}

}
