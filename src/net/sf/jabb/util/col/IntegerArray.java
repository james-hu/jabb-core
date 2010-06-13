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
 * �Ѷ��int���͵�ֵ��װ����һ��������ʺ�������ΪMap��key��
 * 
 * @author Zhengmao HU (James)
 *
 */
public class IntegerArray implements Comparable<Object>, Serializable{
	private static final long serialVersionUID = -8135093635897238532L;

	protected int[] values;
	
	/**
	 * ����һ��������Щ����ֵ��ʵ��
	 * @param values	һ������ֵ
	 */
	public IntegerArray(int... values){
		this.values = values;
	}
	
	/**
	 * �������ֵ
	 * @return
	 */
	public int[] getValues(){
		return values;
	}
	
	/**
	 * ���ָ��λ�õ�ֵ
	 * @param index
	 * @return
	 */
	public int getValue(int index){
		return values[index];
	}
	
	/**
	 * ���ָ��λ�õ�ֵ
	 * @param index
	 * @return
	 */
	public long getLongValue(int index){
		return values[index];
	}
	
	/**
	 * ���ָ��λ�õ�ֵ�������ͷ���
	 * @param index
	 * @return
	 */
	public int getIntValue(int index){
		return values[index];
	}
	
	public int hashCode(){
		long result = 0;
		for (long l: values){
			result += l << 32;
			result ^= l * 31;
		}
		return (int) result;
	}

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
		if (obj == null){
			throw new NullPointerException(".equals(null) happened.");
		}
		return compareTo(obj) == 0;
	}
	
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
