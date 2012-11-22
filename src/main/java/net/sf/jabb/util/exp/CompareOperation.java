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
 * 比较操作的类型常量定义。
 * <p>
 * Constants of the compare operations.
 * 
 * @author Zhengmao HU (James)
 */
public interface CompareOperation {
	static public final int GE = 1;
	static public final int GT = 2;
	static public final int LE = 3;
	static public final int LT = 4;
	static public final int EQ = 5;
	static public final int NE = 6;
}
