package net.sf.jabb.util.exp.test;


import static org.junit.Assert.*;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import net.sf.jabb.util.exp.BooleanExpression;
import net.sf.jabb.util.text.KeywordMatcher;

import org.apache.commons.lang3.mutable.MutableInt;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class ExpressionTest {
	protected static KeywordMatcher kwMatcher;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
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

		kwMatcher = new KeywordMatcher(keywords);
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}
	
	@Test
	public void simpleTest(){
		Map<BooleanExpression, Boolean> exps = new HashMap<BooleanExpression, Boolean>();
		
		BooleanExpression be =
			BooleanExpression.AND(BooleanExpression.TRUE, BooleanExpression.FALSE);
		exps.put(be, Boolean.FALSE);
		
		be = BooleanExpression.AND(BooleanExpression.TRUE,
				BooleanExpression.AND(BooleanExpression.TRUE, BooleanExpression.FALSE),
				BooleanExpression.AND(BooleanExpression.TRUE, BooleanExpression.TRUE, BooleanExpression.FALSE)
				);
		exps.put(be, Boolean.FALSE);
		
		be = BooleanExpression.OR(BooleanExpression.TRUE,
				BooleanExpression.AND(BooleanExpression.TRUE, BooleanExpression.FALSE),
				BooleanExpression.AND(BooleanExpression.TRUE, BooleanExpression.TRUE, BooleanExpression.FALSE)
				);
		exps.put(be, Boolean.TRUE);
		
		be = BooleanExpression.NOT(be);
		exps.put(be, Boolean.FALSE);
		
		be =
			BooleanExpression.AND(BooleanExpression.TRUE, 
					BooleanExpression.NOT(BooleanExpression.FALSE));
		exps.put(be, Boolean.TRUE);
		
		for(Map.Entry<BooleanExpression, Boolean> exp: exps.entrySet()){
			boolean result = exp.getKey().evaluate(null);
			assertEquals(exp.getValue(), result);
			System.out.println(exp.getKey());
			System.out.println("  ==> " + result);
			System.out.println();
		}
	}
	
	@Test
	public void keywordMatchingTest(){
		BooleanExpression exp = 
			BooleanExpression.AND(BooleanExpression.HAS("毛泽东"),
					BooleanExpression.HAS("天安门"),
					BooleanExpression.OR(BooleanExpression.HAS("北京"),
							BooleanExpression.HAS("沈阳")),
					BooleanExpression.NOT(BooleanExpression.HAS("上海"))
					);
		
		System.out.println("-----------------");
		for (String[] text: new String[][]
		                             {
				{"1949年10月1日，在北京天安门上，毛泽东庄严宣布，\n"
				+ "中华人民共和国成立了，从此，中国人民站起来了。这是全中国人民的节日，\n"
				+ "北京、上海等地的人民欢呼雀跃。", "false"},
				{"毛泽东经常出现在北京天安门", "true"},
				{"毛泽东同志是一位伟大的领袖", "false"},
				{"任何游客都可以登上中国的天安门", "false"},
				{"任何游客都可以登上中国的天安门，而且可以参观毛泽东纪念堂", "false"},
				{"北京、沈阳、哈尔滨，都算是在北方", "false"},
				{"北京、沈阳、哈尔滨，都算是在北方，这句话不是毛泽东在天安门说的。", "true"},
				{"北京、沈阳、哈尔滨，都算是北方，毛泽东同志对北方非常重视", "false"},
				{"在北京的天安门城楼，毛泽东同志提到过他对哈尔滨非常重视", "true"},
				{"在北京的天安门城楼，毛泽东同志提到过他对上海非常重视", "false"},
		                             }
		){
			evaluateKeywordMatching(exp, text[0], text[1]);
		}
	}
	
	public void evaluateKeywordMatching(BooleanExpression exp, String text, String expectedResult){
		Map<Object, MutableInt> result = kwMatcher.match(text);
		System.out.println("----------------------");
		System.out.println(text);
		for (Object o: result.keySet()){
			System.out.format("\t %-15s ===> %d\n", o, result.get(o).intValue());
		}
		boolean actualResult = exp.evaluate(result.keySet());
		assertEquals(Boolean.valueOf(expectedResult), actualResult);
		System.out.println(exp);
		System.out.println("  ==> " + result + "   ==> " + actualResult);
		System.out.println();
		
	}

}
