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
 * a bean that contains three value properties.
 */
public class TripleValueBean<V1, V2, V3>  implements Serializable{
	private static final long serialVersionUID = -3680410437574200875L;

	private V1 value1;

	private V2 value2;
	
	private V3 value3;

	public TripleValueBean(){
	}

	public TripleValueBean(V1 v1, V2 v2, V3 v3) {
		this();
		setValue1(v1);
		setValue2(v2);
		setValue3(v3);
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

	public V3 getValue3() {
		return value3;
	}

	public void setValue3(V3 value3) {
		this.value3 = value3;
	}

	public String toString() {
		return "(" + (value1 == null ? "<null>" : value1.toString()) + ","
				+ (value2 == null ? "<null>" : value2.toString()) + ","
				+ (value3 == null ? "<null>" : value3.toString()) + ")";
	}

}
