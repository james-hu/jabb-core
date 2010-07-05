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
 * �Ѷ��long���͵�ֵ��װ����һ��������ʺ�������ΪMap��key��
 * ֧��hashCode(), toString(), equals(), compareTo()������
 * <p>
 * Encapsulates multiple int type values into one object, which
 * is very suitable to be used as key object of Map. 
 * It supports hashCode(), toString(), equals(), compareTo() methods.
 * 
 * @author Zhengmao HU (James)
 *
 */
public class LongArray implements Comparable<Object>, Serializable{
	private static final long serialVersionUID = -8516762858218377073L;
	
	protected long[] values;
	
	/**
	 * ����һ��������Щ������ֵ��ʵ��
	 * <p>
	 * Constructs an LongArray that encapsulates specified long values.
	 * 
	 * @param values	һ��������ֵ<br>long values that will be encapsulated
	 */
	public LongArray(long... values){
		this.values = values;
	}
	
	/**
	 * �������ֵ��
	 * <p>
	 * Gets all the values encapsulated in this object.
	 * 
	 * @return	array of values
	 */
	public long[] getValues(){
		return values;
	}
	
	/**
	 * ���ָ��λ�õ�ֵ��
	 * <p>
	 * Gets the value in specified position.
	 * 
	 * @param index	position (position of the first one is 0)
	 * @return	the value
	 */
	public long getValue(int index){
		return values[index];
	}
	
	/**
	 * ���ָ��λ�õ�ֵ��
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
	 * ���ָ��λ�õ�ֵ��
	 * <p>
	 * Gets the value in specified position.
	 * 
	 * @param index	position (position of the first one is 0)
	 * @return	the value as int
	 */
	public int getIntValue(int index){
		return (int)values[index];
	}
	
	/**
	 * ��ü���õ���hashֵ��
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
	 * �Ƚϡ�
	 * <p>
	 * Compare
	 * 
	 * @param obj	
	 * @return	-1 if little than obj, 0 if equals, 1 if greater.
	 */
	@Override
	public int compareTo(Object obj) {
		if (! (obj instanceof LongArray)){
			throw new IllegalArgumentException("Only comparing to LongArray is supported.");
		}
		LongArray to = (LongArray) obj;
		
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
		if (obj == null){
			throw new NullPointerException(".equals(null) happened.");
		}
		return compareTo(obj) == 0;
	}
	
	@Override
	public String toString(){
		StringBuilder sb = new StringBuilder();
		sb.append('(');
		boolean isFirst = true;
		for (long l: values){
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
