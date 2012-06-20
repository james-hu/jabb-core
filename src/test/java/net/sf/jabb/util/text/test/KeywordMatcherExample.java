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
		System.out.println("==== 一般功能示范 ====");
		KeywordMatcher m = showExample(null);
		
		System.out.println("==== 序列化示范 ====");
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		ObjectOutputStream oos = new ObjectOutputStream(baos);
		oos.writeObject(m);
		byte[] binary = baos.toByteArray();
		ByteArrayInputStream bais = new ByteArrayInputStream(binary);
		ObjectInputStream ois = new ObjectInputStream(bais);
		KeywordMatcher m2 = (KeywordMatcher) ois.readObject();
		showExample(m2);
		
		System.out.println("==== 拷贝示范 ====");
		KeywordMatcher m3 = new KeywordMatcher(m);
		showExample(m3);
		
	}
	
	static public KeywordMatcher showExample(KeywordMatcher m){
		if (m == null){
			Map<String, Object> keywords = new HashMap<String, Object>();
			
			keywords.clear();
			keywords.put("中国", "中国");
			keywords.put("中国人", "中国人");
			keywords.put("中华人民共和国", "中华人民共和国");
			keywords.put("毛泽东", "毛泽东");
			keywords.put("江泽民", "江泽民");
			keywords.put("天安门", "天安门");
			keywords.put("年", "年");
			keywords.put("北京", "北京");
			keywords.put("上海", "上海");
			keywords.put("，", "逗号");

			System.out.println("*** 关键词表 *******");
			for (String w: keywords.keySet()){
				System.out.format("\t %-15s ---> %s\n", w, keywords.get(w));
			}
			System.out.println();

			m = new KeywordMatcher(keywords);
			
		}
		String s ="1949年10月1日，在北京天安门上，毛泽东庄严宣布，\n"
			+ "中华人民共和国成立了，从此，中国人民站起来了。这是全中国人民的节日，\n"
			+ "北京、上海等地的人民欢呼雀跃。";
		System.out.println("*** 文本 *******");
		System.out.println(s);
		System.out.println();
		
		Map<Object, MutableInt> result = m.match(s);
		
		System.out.println("*** 结果 *******");
		for (Object o: result.keySet()){
			System.out.format("\t %-15s ===> %d\n", o, result.get(o).intValue());
		}
		System.out.println();
		return m;
	}

}
