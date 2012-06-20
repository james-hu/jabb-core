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
import java.util.List;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;

import net.sf.jabb.util.text.StringStartWithMatcher;

/**
 * 
 * @author Zhengmao HU (James)
 *
 */
public class StringStartWithMatcherExample {

	/**
	 * 这是一个用法示例。
	 * @param args
	 * @throws IOException 
	 * @throws ClassNotFoundException 
	 */
	public static void main(String[] args) throws IOException, ClassNotFoundException{
		System.out.println("==== 一般功能示范 ====");
		StringStartWithMatcher m = showExample(null);
		
		System.out.println("==== 序列化示范 ====");
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		ObjectOutputStream oos = new ObjectOutputStream(baos);
		oos.writeObject(m);
		byte[] binary = baos.toByteArray();
		ByteArrayInputStream bais = new ByteArrayInputStream(binary);
		ObjectInputStream ois = new ObjectInputStream(bais);
		StringStartWithMatcher m2 = (StringStartWithMatcher) ois.readObject();
		showExample(m2);
		
		System.out.println("==== 复制示范 ====");
		StringStartWithMatcher m3 = new StringStartWithMatcher(m);
		showExample(m3);

	}
	
	static public StringStartWithMatcher showExample(StringStartWithMatcher m){
		if (m == null){
			Map<String, Object> heads = new HashMap<String, Object>();
			
			////////////  号段匹配  ///////////////
			heads.clear();
			heads.put("134", "134号段");
			heads.put("135", "135号段");
			heads.put("136", "136号段");
			heads.put("1361", "1361号段");
			heads.put("1362", "1362号段");
			heads.put("137", "137号段");
			heads.put("138", "138号段");
			heads.put("13817", "13817号段");
			heads.put("13817726996", "我的号码");
			heads.put("138177269", "很接近我的号码");
			heads.put("1381772", "有些接近我的号码");
			heads.put("中华人民共和国", "中华人民共和国");
			heads.put("中华人民", "中华人民");
			heads.put("中华", "中华");
			// 号段展开
			StringStartWithMatcher.expandNumberMatchingRange(heads, "1335000", "1335999", "1335000~1335999");
			StringStartWithMatcher.expandNumberMatchingRange(heads, "1375010", "1375039", "1375010~1375039");
			StringStartWithMatcher.expandNumberMatchingRange(heads, "13750632", "13750641", "13750632~13750641");
			StringStartWithMatcher.expandNumberMatchingRange(heads, "130120", "130139", "130120~130139");
			StringStartWithMatcher.expandNumberMatchingRange(heads, "130125", "130129", "130125~130129");
			StringStartWithMatcher.expandNumberMatchingRange(heads, "1891", "189299", "1891~189299");
			StringStartWithMatcher.expandNumberMatchingRange(heads, "1881991", "1882", "1881991~1882");
			
			System.out.println("\t*** 展开后的匹配对应表 *******");
			SortedSet<String> ss = new TreeSet<String>(heads.keySet());
			for (String pattern: ss){
				System.out.format("\t %-15s ---> %s\n", pattern, heads.get(pattern));
			}
			System.out.println();

			m = new StringStartWithMatcher(heads);
			
		}
		System.out.println("  **** 匹配结果 *******");
		for (String s: new String[] {
				"1376726637", "13717726996", "1340898394",
				"18", "138", "1385", "13817", "13817899633", "1381772", "13817726", 
				"13817726997", "13817726996",
				"138177269977", "138177269967", "138177269", "1381772699",
				"13817726997744", "138177269967343",
				"133500166", "1335010", "133501",
				"1891234", "18923434", "1892",
				"1301213243", "130138090", "13009090", "13012689892",
				"中间位置", "中国人民", "中华大地", "中华民国", "中华人民共有的财产", "中华人民共和国", "中华人民共和国成立了",
				"1360172", "13610238", "1362834", "2137138139", "134136137138139"
				}){
			Object o = m.match(s);
			List<Object> lo = m.matchAll(s);
			System.out.format("\t %-15s ===> %s  |%s\n", s, (o!=null ? o.toString() : "null"), 
					(lo!=null ? lo.toString() : "null"));
		}
		System.out.println();

		return m;
	}
	
}
