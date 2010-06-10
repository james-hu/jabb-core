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

package net.sf.jabb.util.text;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.TimeZone;

/**
 * 格式化时间段信息，其结果比如: 00:03:01.011，或：7d, 12:32:02:001。
 * 典型用法是这样的：
 * <pre>
 *   long t0 = System.currentTimeMillis();
 *   ...
 *   long t1 = System.currentTimeMillis();
 *   ...
 *   System.out.println(DurationFormatter.formatSince(t0));
 *   System.out.println(DurationFormatter.format(t1-t0));
 * </pre>
 * @author Zhengmao HU (James)
 *
 */
public class DurationFormatter {
	protected static DateFormat df;
	protected static final long ONE_DAY = 1000L*3600*24;

	static{
		df = new SimpleDateFormat("HH:mm:ss.SSS");
		df.setTimeZone(TimeZone.getTimeZone("GMT"));
	}
	
	/**
	 * 格式化时间长度
	 * @param duration	以毫秒为单位的时间长度
	 * @return
	 */
	public static String format(long duration){
		if (duration >= ONE_DAY){
			long days = duration/ONE_DAY;
			return String.format("%dd, %s", days, df.format(duration));
		}else{
			return df.format(duration);
		}
	}
	
	/**
	 * 格式化指定时间同当前系统时间之间的时间差
	 * @param previousTime	指定以哪个时间同当前系统时间来比较
	 * @return
	 */
	public static String formatSince(long previousTime){
		return format(System.currentTimeMillis() - previousTime);
	}
}
