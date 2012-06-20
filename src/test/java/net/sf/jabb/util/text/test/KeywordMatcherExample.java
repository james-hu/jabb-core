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
package net.sf.jabb.util.text.test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.mutable.MutableInt;

import net.sf.jabb.util.text.KeywordMatcher;

/**
 * @author Zhengmao HU (James)
 *
 */
public class KeywordMatcherExample {

	/**
	 * @param args
	 * @throws IOException 
	 * @throws ClassNotFoundException 
	 */
	public static void main(String[] args) throws IOException, ClassNotFoundException {
		System.out.println("==== һ�㹦��ʾ�� ====");
		KeywordMatcher m = showExample(null);
		
		System.out.println("==== ���л�ʾ�� ====");
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		ObjectOutputStream oos = new ObjectOutputStream(baos);
		oos.writeObject(m);
		byte[] binary = baos.toByteArray();
		ByteArrayInputStream bais = new ByteArrayInputStream(binary);
		ObjectInputStream ois = new ObjectInputStream(bais);
		KeywordMatcher m2 = (KeywordMatcher) ois.readObject();
		showExample(m2);
		
		System.out.println("==== ����ʾ�� ====");
		KeywordMatcher m3 = new KeywordMatcher(m);
		showExample(m3);
		
	}
	
	static public KeywordMatcher showExample(KeywordMatcher m){
		if (m == null){
			Map<String, Object> keywords = new HashMap<String, Object>();
			
			keywords.clear();
			keywords.put("�й�", "�й�");
			keywords.put("�й���", "�й���");
			keywords.put("�л����񹲺͹�", "�л����񹲺͹�");
			keywords.put("ë��", "ë��");
			keywords.put("������", "������");
			keywords.put("�찲��", "�찲��");
			keywords.put("��", "��");
			keywords.put("����", "����");
			keywords.put("�Ϻ�", "�Ϻ�");
			keywords.put("��", "����");

			System.out.println("*** �ؼ��ʱ� *******");
			for (String w: keywords.keySet()){
				System.out.format("\t %-15s ---> %s\n", w, keywords.get(w));
			}
			System.out.println();

			m = new KeywordMatcher(keywords);
			
		}
		String s ="1949��10��1�գ��ڱ����찲���ϣ�ë��ׯ��������\n"
			+ "�л����񹲺͹������ˣ��Ӵˣ��й�����վ�����ˡ�����ȫ�й�����Ľ��գ�\n"
			+ "�������Ϻ��ȵص����񻶺�ȸԾ��";
		System.out.println("*** �ı� *******");
		System.out.println(s);
		System.out.println();
		
		Map<Object, MutableInt> result = m.match(s);
		
		System.out.println("*** ��� *******");
		for (Object o: result.keySet()){
			System.out.format("\t %-15s ===> %d\n", o, result.get(o).intValue());
		}
		System.out.println();
		return m;
	}

}
