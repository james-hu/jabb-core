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
 * a bean with two properties: key and value
 */
public class KeyValueBean<K, V> extends DoubleValueBean<K, V> implements Serializable {
	private static final long serialVersionUID = 2731831951536130946L;

	public KeyValueBean(){
		super();
	}
	
	public KeyValueBean(K v1, V v2) {
		super(v1, v2);
	}

	public K getKey() {
		return getValue1();
	}

	public V getValue() {
		return getValue2();
	}

	public void setKey(K key) {
		setValue1(key);
	}

	public void setValue(V value) {
		setValue2(value);
	}
}