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
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import net.sf.jabb.util.text.MatchingDefinition;
import net.sf.jabb.util.text.StartWithMatcher;

/**
 * @author Zhengmao HU (James)
 */
public class StartWithMatcherExample {

	/**
	 * 注意，如果匹配条件互相之间是匹配的，则结果可能会不准。
	 * 所以在需要知道具体匹配结果的时候，要避免用正则表达式来作为匹配条件，尽量用纯字符串。
	 * @param args
	 * @throws IOException 
	 * @throws ClassNotFoundException 
	 * @throws CloneNotSupportedException 
	 */
	public static void main(String[] args) throws IOException, ClassNotFoundException, CloneNotSupportedException{
		System.out.println("==== 一般功能示范 ====");
		StartWithMatcher m = showExample(null);
		
		System.out.println("==== 序列化示范 ====");
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		ObjectOutputStream oos = new ObjectOutputStream(baos);
		oos.writeObject(m);
		byte[] binary = baos.toByteArray();
		ByteArrayInputStream bais = new ByteArrayInputStream(binary);
		ObjectInputStream ois = new ObjectInputStream(bais);
		StartWithMatcher m2 = (StartWithMatcher) ois.readObject();
		showExample(m2);
		
		System.out.println("==== 复制示范 ====");
		StartWithMatcher m3 = new StartWithMatcher(m);
		showExample(m3);
		
	}
	
	static public StartWithMatcher showExample(StartWithMatcher m){
		if (m == null){
			MatchingDefinition c1 = new MatchingDefinition();
			c1.setRegularExpression(".*\\.sina.com/images/.*\\.jpg");
			List<String> l1 = new LinkedList<String>();
			l1.add("www.sina.com/images/a.jpg");
			l1.add("www2.sina.com/images/a.jpg");
			l1.add("img.sina.com/images/a.jpg");
			l1.add("img.sina.com/images/b.jpg");
			l1.add("news.sina.com/images/a.jpg");
			c1.setExactMatchExamples(l1);
			c1.setAttachment("c1");
			
			MatchingDefinition c2 = new MatchingDefinition();
			c2.setRegularExpression("news.sina.com/images/.*\\.jpg");
			List<String> l2 = new LinkedList<String>();
			l2.add("news.sina.com/images/a.jpg");
			l2.add("news.sina.com/images/b.jpg");
			c2.setExactMatchExamples(l2);
			c2.setAttachment("c2");
			
			MatchingDefinition c3 = new MatchingDefinition();
			c3.setRegularExpression("news.sina.com/images/");
			List<String> l3 = new LinkedList<String>();
			l3.add("news.sina.com/images/adfad/adsfk/a.jpg");
			l3.add("news.sina.com/images/wrtj/lkjjt/b.jpg");
			c3.setExactMatchExamples(l3);
			c3.setAttachment("c3");
			
			List<MatchingDefinition> ml = new ArrayList<MatchingDefinition>(10);
			ml.add(c1);
			ml.add(c2);
			ml.add(c3);
			
			System.out.println(ml);
			m = new StartWithMatcher(ml);
		}
		
		System.out.println();
		showMatching(m, "news.sina.com/images/test.jpg");
		showMatching(m, "news.sina.com/images/x.jpg");
		showMatching(m, "news.sina.com/images/x.doc");
		showMatching(m, "images.sina.com/images/test.jpg");
		System.out.println();

		return m;
	}
	
	static public void showMatching(StartWithMatcher m, String s){
		System.out.println(s + "\t ===> " + m.match(s));
	}
	
}
