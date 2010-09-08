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

package net.sf.jabb.util.col;

import java.io.Serializable;

/**
 * 把多个int类型的值封装在这一个对象里，适合用来作为Map的key。
 * 支持hashCode(), toString(), equals(), compareTo()方法。
 * <p>
 * Encapsulates multiple int type values into one object, which
 * is very suitable to be used as key object of Map. 
 * It supports hashCode(), toString(), equals(), compareTo() methods.
 * 
 * @author Zhengmao HU (James)
 *
 */
public class IntegerArray implements Comparable<Object>, Serializable{
	private static final long serialVersionUID = -8135093635897238532L;

	protected int[] values;
	
	/**
	 * 创建一个包含这些整型值的实例。
	 * <p>
	 * Constructs an IntegerArray that encapsulates specified int values.
	 * 
	 * @param values	一批整型值<br>int values that will be encapsulated
	 */
	public IntegerArray(int... values){
		this.values = values;
	}
	
	/**
	 * 获得所有值。
	 * <p>
	 * Gets all the values encapsulated in this object.
	 * 
	 * @return	array of values
	 */
	public int[] getValues(){
		return values;
	}
	
	/**
	 * 获得指定位置的值。
	 * <p>
	 * Gets the value in specified position.
	 * 
	 * @param index	position (position of the first one is 0)
	 * @return	the value
	 */
	public int getValue(int index){
		return values[index];
	}
	
	/**
	 * 获得指定位置的值。
	 * <p>
	 * Gets the value in specified position.
	 * 
	 * @param index	position (position of the first one is 0)
	 * @return	the value as long
	 */
	public long getLongValue(int index){
		return values[index];
	}
	
	/**
	 * 获得指定位置的值。
	 * <p>
	 * Gets the value in specified position.
	 * 
	 * @param index	position (position of the first one is 0)
	 * @return	the value as int
	 */
	public int getIntValue(int index){
		return values[index];
	}
	
	/**
	 * 获得计算得到的hash值。
	 * <p>
	 * Gets the calculated hash code.
	 */
	@Override
	public int hashCode(){
		long result = 0;
		for (long l: values){
			result += l << 32;
			result ^= l * 31;
		}
		return (int) result;
	}

	/**
	 * 比较。
	 * <p>
	 * Compare
	 * 
	 * @param obj	
	 * @return	-1 if little than obj, 0 if equals, 1 if greater.
	 */
	@Override
	public int compareTo(Object obj) {
		if (! (obj instanceof IntegerArray)){
			throw new IllegalArgumentException("Only comparing to IntArray is supported.");
		}
		IntegerArray to = (IntegerArray) obj;
		
		if (this.values.length < to.values.length){
			return -1;
		}else if (this.values.length > to.values.length){
			return 1;
		}
		
		int result = 0;
		int i = 0;
		while (result == 0 && i < this.values.length){
			if (this.values[i] < to.values[i]){
				result = -1;
			}else if (this.values[i] > to.values[i]){
				result = 1;
			}
			i ++;
		}
		return result;
	}
	
	@Override
	public boolean equals(Object obj){
		//check for self-comparison
	    if ( this == obj ) 
	    	return true;
	    
	    //use instanceof instead of getClass here for two reasons
	    //1. if need be, it can match any supertype, and not just one class;
	    //2. it renders an explict check for "that == null" redundant, since
	    //it does the check for null already - "null instanceof [type]" always
	    //returns false. (See Effective Java by Joshua Bloch.)

	    if ( !(obj instanceof IntegerArray) ) 
	    	return false;

		return compareTo(obj) == 0;
	}
	
	@Override
	public String toString(){
		StringBuilder sb = new StringBuilder();
		sb.append('(');
		boolean isFirst = true;
		for (int l: values){
			if (isFirst){
				isFirst = false;
			}else{
				sb.append(", ");
			}
			sb.append(l);
		}
		sb.append(')');
		return sb.toString();
	}

}
