package net.sf.jabb.util.exp.test;


import static org.junit.Assert.*;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import net.sf.jabb.util.exp.BooleanExpression;
import net.sf.jabb.util.text.KeywordMatcher;

import org.apache.commons.lang.mutable.MutableInt;
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
			BooleanExpression.AND(BooleanExpression.HAS("ë��"),
					BooleanExpression.HAS("�찲��"),
					BooleanExpression.OR(BooleanExpression.HAS("����"),
							BooleanExpression.HAS("����")),
					BooleanExpression.NOT(BooleanExpression.HAS("�Ϻ�"))
					);
		
		System.out.println("-----------------");
		for (String[] text: new String[][]
		                             {
				{"1949��10��1�գ��ڱ����찲���ϣ�ë��ׯ��������\n"
				+ "�л����񹲺͹������ˣ��Ӵˣ��й�����վ�����ˡ�����ȫ�й�����Ľ��գ�\n"
				+ "�������Ϻ��ȵص����񻶺�ȸԾ��", "false"},
				{"ë�󶫾��������ڱ����찲��", "true"},
				{"ë��ͬ־��һλΰ�������", "false"},
				{"�κ��οͶ����Ե����й����찲��", "false"},
				{"�κ��οͶ����Ե����й����찲�ţ����ҿ��Բι�ë�󶫼�����", "false"},
				{"���������������������������ڱ���", "false"},
				{"���������������������������ڱ�������仰����ë�����찲��˵�ġ�", "true"},
				{"�������������������������Ǳ�����ë��ͬ־�Ա����ǳ�����", "false"},
				{"�ڱ������찲�ų�¥��ë��ͬ־�ᵽ�����Թ������ǳ�����", "true"},
				{"�ڱ������찲�ų�¥��ë��ͬ־�ᵽ�������Ϻ��ǳ�����", "false"},
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
