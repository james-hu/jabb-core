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

package net.sf.jabb.util.exp;

/**
 * ���ڵõ��ȽϽ��
 * @author Zhengmao HU (James)
 *
 */
abstract public class CompareExpression extends BooleanExpression implements CompareOperation {
	protected Object leftOperand;
	protected Object rightOperand;
	protected int operation;
	
	public CompareExpression(Object leftOperand, int operation, Object rightOperand){
		super(OPERAND);
		this.leftOperand = leftOperand;
		this.rightOperand = rightOperand;
		this.operation = operation;
	}
	
	@Override
	public void addOperand(BooleanExpression operand){
		throw new UnsupportedOperationException("This method should not be used.");
	}

	@Override
	public void addOperand(BooleanExpression... operands){
		throw new UnsupportedOperationException("This method should not be used.");
	}
	
	@Override
	public boolean evaluate(Object context) {
		return compare(context);
	}
	
	/**
	 * ����Ӧ��ʵ���������������leftOperand, operation, rightOperand���Լ�context��
	 * ������ıȽ����㣬����ȷ�еĽ����
	 * @return
	 */
	abstract protected boolean  compare(Object context);

	@Override
	public String toString(){
		StringBuilder sb = new StringBuilder();
		sb.append('(');
		sb.append(leftOperand);
		switch(operation){
		case GE:
			sb.append(" >= ");
			break;
		case GT:
			sb.append(" > ");
			break;
		case LE:
			sb.append(" <= ");
			break;
		case LT:
			sb.append(" < ");
			break;
		case EQ:
			sb.append(" == ");
			break;
		case NE:
			sb.append(" != ");
			break;
		default:
			sb.append(" ? ");
			break;
		}
		sb.append(rightOperand);
		sb.append(')');
		return sb.toString();

	}

}
