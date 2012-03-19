/*
Copyright 2003,2012 Zhengmao HU (James)

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
package net.sf.jabb.util.bean;

import java.io.Serializable;

/**
 * @author Zhengmao Hu
 * 
 * a bean that contains two value properties.
 */
public class DoubleValueBean<V1, V2>  implements Serializable{
	private static final long serialVersionUID = -6737961503575937877L;

	private V1 value1;

	private V2 value2;
	
	public DoubleValueBean(){
	}

	public DoubleValueBean(V1 v1, V2 v2) {
		this();
		setValue1(v1);
		setValue2(v2);
	}

	public void setValue1(V1 value1) {
		this.value1 = value1;
	}

	public V1 getValue1() {
		return value1;
	}

	public void setValue2(V2 value2) {
		this.value2 = value2;
	}

	public V2 getValue2() {
		return value2;
	}

	public String toString() {
		return "(" + (value1 == null ? "<null>" : value1.toString()) + ","
				+ (value2 == null ? "<null>" : value2.toString()) + ")";
	}

}