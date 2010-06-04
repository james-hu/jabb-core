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
 * 把多个long类型的值封装在这一个对象里，适合用来作为Map的key。
 * 
 * @author Zhengmao HU (James)
 *
 */
public class LongArray implements Comparable<Object>, Serializable{
	private static final long serialVersionUID = -8516762858218377073L;
	
	protected long[] values;
	
	/**
	 * 创建一个包含这些长整型值的实例
	 * @param values	一批长整型值
	 */
	public LongArray(long... values){
		this.values = values;
	}
	
	/**
	 * 获得所有值
	 * @return
	 */
	public long[] getValues(){
		return values;
	}
	
	/**
	 * 获得指定位置的值
	 * @param index
	 * @return
	 */
	public long getValue(int index){
		return values[index];
	}
	
	/**
	 * 获得指定位置的值
	 * @param index
	 * @return
	 */
	public long getLongValue(int index){
		return values[index];
	}
	
	/**
	 * 获得指定位置的值，以整型返回
	 * @param index
	 * @return
	 */
	public int getIntValue(int index){
		return (int)values[index];
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
		if (! (obj instanceof LongArray)){
			throw new IllegalArgumentException("Only comparing to MultipleLong is supported.");
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
