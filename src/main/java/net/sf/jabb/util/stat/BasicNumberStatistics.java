/*
Copyright 2010-2011, 2015 Zhengmao HU (James)

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

package net.sf.jabb.util.stat;



/**
 * 提供基本的统计信息，包括：
 * 最大值、最小值、平均值、总计、个数。
 * 它是多线程安全的。
 * @author Zhengmao HU (James)
 * @deprecated use LongStatistics instead
 *
 */
public class BasicNumberStatistics extends AtomicLongStatistics {
	private static final long serialVersionUID = 2323607693923649800L;
}
