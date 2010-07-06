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
 * 对数字（Number的子类，包括AtomicLong, AtomicInteger, Long, Integer, Double等）进行比较。
 * 比较常见的用法是其中一个操作数用AtomicLong或AtomicInteger，另一个用Long或Integer。
 * <p>
 * Compares Numbers(including AtomicLong, AtomicInteger, Long, Integer, Double, etc)
 * Typical usage is to use AtomicLong or AtomicInteger as one operand and
 * use Long or Integer as another.
 * 
 * @author Zhengmao HU (James)
 *
 */
public class NumberCompareExpression extends CompareExpression {

	/**
	 * 直接把待比较的数字带进去。真正的比较会发生在evaluate的时候。
	 * 调用evaluate()方法的时候传递的context参数不会被用到。
	 * 
	 * @param leftOperand
	 * @param operation
	 * @param rightOperand
	 */
	public NumberCompareExpression(Number leftOperand, int operation,
			Number rightOperand) {
		super(leftOperand, operation, rightOperand);
	}

	/**
	 * @param context this argument is useless, you can pass null directly
	 * @see net.sf.jabb.util.exp.CompareExpression#compare(java.lang.Object)
	 */
	@Override
	protected boolean compare(Object context) {
		double left = ((Number)leftOperand).doubleValue();
		double right = ((Number)rightOperand).doubleValue();
		switch (operation){
		case CompareOperation.EQ:
			return left == right;
		case CompareOperation.GE:
			return left >= right;
		case CompareOperation.GT:
			return left > right;
		case CompareOperation.LE:
			return left <= right;
		case CompareOperation.LT:
			return left < right;
		case CompareOperation.NE:
			return left != right;
		default:
			throw new IllegalArgumentException("No such CompareOperation: " + operation);
		}
	}

}
