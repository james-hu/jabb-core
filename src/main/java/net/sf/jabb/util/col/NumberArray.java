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

package net.sf.jabb.util.col;

import java.io.Serializable;

/**
 * Encapsulates multiple objects of Number(such as Integer, Long, Double) into one object,
 * which is suitable to be used as the key object of Map.<br>
 * 把多个Number（比如Integer, Long, Double）类型的值封装在这一个对象里，适合用来作为Map的key。
 * <p>
 * It supports hashCode(), toString(), equals(), compareTo() methods.
 * <p>
 * 它支持hashCode(), toString(), equals(), compareTo()方法。
 * 
 * @author Zhengmao HU (James)
 *
 * @param <T extends Number>	Number类型
 */
public class NumberArray<T extends Number> implements Comparable<Object>, Serializable{
	private static final long serialVersionUID = 3101324164832289477L;

	protected T[] values;
	
	/**
	 * Constructs a NumberArray with specified value objects.<br>
	 * 创建一个包含这些数值对象的实例。
	 * 
	 * @param values	value objects of Number type that will be encapsulated.<br>
	 * 					一批将被封装的数值对象。
	 */
	public NumberArray(T... values){
		this.values = values;
	}
	
	/**
	 * Gets all the values encapsulated in this object.<br>
	 * 获得所有值。
	 * 
	 * @return array of value objects
	 */
	public T[] getValues(){
		return values;
	}
	
	/**
	 * Gets the value in specified position.<br>
	 * 获得指定位置的值。
	 * 
	 * @param index	position (position of the first one is 0)
	 * @return	the value
	 */
	public T getValue(int index){
		return values[index];
	}

	/**
	 * Gets the value in specified position.<br>
	 * 获得指定位置的值。
	 * 
	 * @param index	position (position of the first one is 0)
	 * @return	the value as int
	 */
	public int getIntValue(int index){
		return values[index].intValue();
	}
	
	/**
	 * Gets the value in specified position.<br>
	 * 获得指定位置的值。
	 * 
	 * @param index	position (position of the first one is 0)
	 * @return	the value as long
	 */
	public long getLongValue(int index){
		return values[index].longValue();
	}
	
	/**
	 * Gets the value in specified position.<br>
	 * 获得指定位置的值。
	 * 
	 * @param index	position (position of the first one is 0)
	 * @return	the value as double
	 */
	public double getDoubleValue(int index){
		return values[index].doubleValue();
	}

	/**
	 * Gets the value in specified position.<br>
	 * 获得指定位置的值。
	 * 
	 * @param index	position (position of the first one is 0)
	 * @return	the value as float
	 */
	public float getFloatValue(int index){
		return values[index].floatValue();
	}

	/**
	 * Gets the value in specified position.<br>
	 * 获得指定位置的值。
	 * 
	 * @param index	position (position of the first one is 0)
	 * @return	the value as short
	 */
	public short getShortValue(int index){
		return values[index].shortValue();
	}

	/**
	 * Gets the value in specified position.<br>
	 * 获得指定位置的值。
	 * 
	 * @param index	position (position of the first one is 0)
	 * @return	the value as byte
	 */
	public byte getByteValue(int index){
		return values[index].byteValue();
	}

	/**
	 * Gets the calculated hash code.<br>
	 * 获得计算得到的hash值。
	 */
	@Override
	public int hashCode(){
		long result = 0;
		for (T l: values){
			result += l.hashCode();
			result ^= l.hashCode() * 31;
		}
		return (int) result;
	}


	/**
	 * Compare.<br>
	 * 比较。
	 * 
	 * @param obj	The object to be compared with
	 * @return	-1 if little than obj, 0 if equals, 1 if greater than.
	 */
	public int compareTo(Object obj) {
		if (! (obj instanceof NumberArray<?>)){
			throw new IllegalArgumentException("Only comparing to MultipleNumbers is supported.");
		}
		NumberArray<?> to = (NumberArray<?>) obj;
		
		if (this.values.length < to.values.length){
			return -1;
		}else if (this.values.length > to.values.length){
			return 1;
		}
		
		int result = 0;
		int i = 0;
		while (result == 0 && i < this.values.length){
			if (this.values[i].doubleValue() < to.values[i].doubleValue()){
				result = -1;
			}else if (this.values[i].doubleValue() > to.values[i].doubleValue()){
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
	    if ( !(obj instanceof NumberArray) ) 
	    	return false;

		return compareTo(obj) == 0;
	}
	
	@Override
	public String toString(){
		StringBuilder sb = new StringBuilder();
		sb.append('(');
		boolean isFirst = true;
		for (T l: values){
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
