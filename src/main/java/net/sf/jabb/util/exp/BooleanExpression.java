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

package net.sf.jabb.util.exp;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

/**
 * Logical expression that results in true or false.<br>
 * 产生true或false结果的逻辑表达式。
 * 
 * @author Zhengmao HU (James)
 *
 */
public class BooleanExpression {
	protected static final int OPERATOR_AND 	= 1;
	protected static final int OPERATOR_OR  	= 2;
	protected static final int OPERATOR_NOT 	= 3;
	protected static final int OPERAND			= 4;
	
	public static final BooleanExpression TRUE;
	public static final BooleanExpression FALSE;
	
	
	protected int operatorType;
	protected List<BooleanExpression> children;
	
	static{
		TRUE = new BooleanExpression(OPERAND) {
			@Override
			public boolean evaluate(Object context){
				return true;
			}
			
			@Override
			public String toString(){
				return "true";
			}
		};
		
		FALSE = new BooleanExpression(OPERAND) {
			@Override
			public boolean evaluate(Object context){
				return false;
			}
			
			@Override
			public String toString(){
				return "false";
			}
		};
	}
	
	/**
	 * 创建用AND连接的表达式
	 * @param operands
	 * @return	用AND连接之后的表达式
	 */
	static public BooleanExpression AND(BooleanExpression... operands){
		BooleanExpression result = new BooleanExpression(OPERATOR_AND);
		result.addOperand(operands);
		return result;
	}

	/**
	 * 创建用AND连接的表达式
	 * @param operands
	 * @return	用AND连接之后的表达式
	 */
	static public BooleanExpression AND(Collection<? extends BooleanExpression> operands){
		BooleanExpression result = new BooleanExpression(OPERATOR_AND);
		result.addOperand(operands);
		return result;
	}

	/**
	 * 创建用OR连接的表达式
	 * @param operands
	 * @return	用OR连接之后的表达式
	 */
	static public BooleanExpression OR(BooleanExpression... operands){
		BooleanExpression result = new BooleanExpression(OPERATOR_OR);
		result.addOperand(operands);
		return result;
	}

	/**
	 * 创建用OR连接的表达式
	 * @param operands
	 * @return	用OR连接之后的表达式
	 */
	static public BooleanExpression OR(Collection<? extends BooleanExpression> operands){
		BooleanExpression result = new BooleanExpression(OPERATOR_OR);
		result.addOperand(operands);
		return result;
	}
	
	/**
	 * 创建用NOT修饰的表达式
	 * @param operand
	 * @return	用NOT修饰之后的表达式
	 */
	static public BooleanExpression NOT(BooleanExpression operand){
		BooleanExpression result = new BooleanExpression(OPERATOR_NOT);
		result.addOperand(operand);
		return result;
	}
	
	static public BooleanExpression HAS(final Object obj){
		return new BooleanExpression(OPERAND){
			protected Object key = obj;
			
			@Override
			public boolean evaluate(Object context){
				if (context instanceof Collection<?>){
					return ((Collection<?>)context).contains(key);
				}else{
					throw new IllegalArgumentException("The context object for evaluation must be instanceof Collection.");
				}
			}
			
			@Override
			public String toString(){
				return key.toString();
			}
		};
	}
	
	/**
	 * 创建一个实例
	 * @param operatorType
	 */
	protected BooleanExpression(int operatorType){
		this.operatorType = operatorType;
		if (this.operatorType != OPERAND){
			this.children = new LinkedList<BooleanExpression>();
		}
	}
	
	/**
	 * 给表达式增加运算数
	 * @param operand
	 */
	public void addOperand(BooleanExpression operand){
		children.add(operand);
	}

	/**
	 * 给表达式增加运算数
	 * @param operands
	 */
	public void addOperand(BooleanExpression... operands){
		for (BooleanExpression operand: operands){
			children.add(operand);
		}
	}

	/**
	 * 给表达式增加运算数
	 * @param operands
	 */
	public void addOperand(Collection<? extends BooleanExpression> operands){
		children.addAll(operands);
	}

	/**
	 * 获得表达式的结果。子类可以重载这个方法。
	 * @param context
	 * @return result
	 */
	public boolean evaluate(Object context){
		switch (operatorType){
		case OPERATOR_AND:
			for (BooleanExpression e: children){
				if (e.evaluate(context) == false){
					return false;
				}
			}
			return true;
		case OPERATOR_OR:
			for (BooleanExpression e: children){
				if (e.evaluate(context) == true){
					return true;
				}
			}
			return false;
		case OPERATOR_NOT:
			if (children.size() != 1){
				throw new UnsupportedOperationException("One and only one operand is allowed for NOT operator.");
			}
			BooleanExpression e = children.get(0);
			return !e.evaluate(context);
		default:
			throw new UnsupportedOperationException("Operand should implement its own evaluation method.");
		}
	}
	
	/**
	 * 用null作为context获得表达式的结果。子类不必重载这个方法。
	 * @return	evaluate(null)
	 */
	public boolean evaluate(){
		return evaluate(null);
	}
	
	/**
	 * 转成字符串表示。子类可以重载这个方法。
	 */
	public String toString(){
		StringBuilder sb = new StringBuilder();
		sb.append('(');
		if (operatorType == OPERATOR_NOT){
			sb.append("NOT ");
			sb.append(children.get(0));
		}else{
			boolean isFirst = true;
			for (BooleanExpression e: children){
				if (isFirst){
					isFirst = false;
				}else{
					switch(operatorType){
					case OPERATOR_AND:
						sb.append(" AND ");
						break;
					case OPERATOR_OR:
						sb.append(" OR ");
						break;
					default:
						sb.append(" ? ");
						break;
					}
				}
				sb.append(e);
			}
		}
		sb.append(')');
		return sb.toString();
	}
	
}
