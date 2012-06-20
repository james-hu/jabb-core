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

import net.sf.jabb.util.text.UrlStartWithMatcher;

/**
 * 
 * @author Zhengmao HU (James)
 * 
 * 
 */
public class UrlStartWithMatcherExample {

	/**
	 * 这是一个用法示例。
	 * @param args
	 * @throws IOException 
	 * @throws ClassNotFoundException 
	 */
	public static void main(String[] args) throws IOException, ClassNotFoundException {
		System.out.println("==== 一般功能示范 ====");
		UrlStartWithMatcher m = showExample(null);
		
		System.out.println("==== 序列化示范 ====");
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		ObjectOutputStream oos = new ObjectOutputStream(baos);
		oos.writeObject(m);
		byte[] binary = baos.toByteArray();
		ByteArrayInputStream bais = new ByteArrayInputStream(binary);
		ObjectInputStream ois = new ObjectInputStream(bais);
		UrlStartWithMatcher m2 = (UrlStartWithMatcher) ois.readObject();
		showExample(m2);
		
		System.out.println("==== 复制示范 ====");
		UrlStartWithMatcher m3 = new UrlStartWithMatcher(m);
		showExample(m3);

		

	}
	static public UrlStartWithMatcher showExample(UrlStartWithMatcher m){
		if (m == null){
			Map<String, Object> heads = new HashMap<String, Object>();

			////////////  URL匹配  ///////////////
			heads.clear();
			for (String s: new String[] {
					"news.sina.com.cn",
					"*.sina.com.cn",
					"*.sina.com.cn/z",
					"*.sina.com.cn/images",
					"*.baidu.cn",
					"*.baidu.com",
					"*.baidu.com/se",
					"*.baidu.com/set",
					"www.baidu.com/search/",
					"www.baidu.com/",
					"*.sina.com.cn/news/daily/a.jpg"
				}){
				heads.put(s, s);
			}
			System.out.println("   ------- 匹配对应表 -------");
			for (String pattern: heads.keySet()){
				System.out.format("   %-35s ---> %s\n", pattern, heads.get(pattern));
			}
			System.out.println();
			
			m = new UrlStartWithMatcher(heads);
		}
		System.out.println("   ------- 匹配结果 -------");
		for (String s: new String[] {
				"http://new-s.sina.com.cn",
				"http://news.sina.com.cn",
				"https://news_sina3com4cn",
				"https://news.sina.com.cn/z/2010chunyun/index.shtml",
				"http://h3.news.sina.com.cn/z/2010chunyun/index.shtml",
				"ent.sina.com.cn/entertainment/x/3/a.html",
				"news.sina.com.cn",
				"news_sina3com4cn",
				"news.sina.com.cn/z/2010chunyun/index.shtml",
				"ent.sina.com.cn/entertainment/x/3/a.html",
				"hollywood.sina.com.cn/entertainment/news/today.html",
				"www.baidu.com/search?key=3&a=b", 
				"mp3.baidu.com/download/song=1",
				"video.baidu.com/screen/video=2",
				"www.baidu.com/news/a.html",
				"www.baidu.com/setup/",
				"www.baidu.com/search/abcd.jpg",
				"www.baidu.com/search/mp3/test.jpg",
				"www.baidu.com/search/video/flash/new.jpg",
				"www.baidu.com/news/daily/headline.jpg",
				"www.baidu.com/news/daily/common/headline.jpg"
				}){
			Object o = m.match(s);
			System.out.format("   %-54s ===> %s\n", s, (o!=null ? o.toString() : "null"));
		}
		return m;
	}

}
