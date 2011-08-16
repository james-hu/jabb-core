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

package net.sf.jabb.util.text;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.TimeZone;

/**
 * An utility to format the length of time period to String, for example: 00:03:01.011 or 7d, 12:32:02:001.<br>
 * 一个可以用来格式化时间段信息的工具类，其输出结果比如: 00:03:01.011，或：7d, 12:32:02:001。
 * <p>
 * An example of usage:<br>
 * 典型用法是这样的：
 * <pre>
 *   long t0 = System.currentTimeMillis();
 *   ...
 *   long t1 = System.currentTimeMillis();
 *   ...
 *   System.out.println(DurationFormatter.formatSince(t0));  // from t0 to current time
 *   ...
 *   System.out.println(DurationFormatter.format(t1-t0));	// from t0 to t1
 *   ...
 *   Calendar calendar = new GregorianCalendar();
 *   calendar.set(2060, 1, 31, 8, 0);  // Jan 31 2060 8AM
 *   System.out.println(DurationFormatter.formatSince(calendar.getTimeInMillis());
 *   
 * </pre>
 * 
 * @author Zhengmao HU (James)
 *
 */
public class DurationFormatter {
	protected static final long ONE_DAY_IN_MILLI = 1000L*3600*24;

	/**
	 * Format the length of time period (in milliseconds) to String.<br>
	 * 格式化时间长度（以毫秒为单位）为字符串。
	 * 
	 * @param duration	Length of time period in milliseconds<br>以毫秒为单位的时间长度
	 * @return	The string presentation of the time period, 
	 * 			for example: 00:03:01.011 or 7d, 12:32:02:001.<br>
	 * 			字符串形式表示的时间长度，比如：00:03:01.011，或：7d, 12:32:02:001。
	 */
	public static String format(long duration){
		DateFormat df = new SimpleDateFormat("HH:mm:ss.SSS");
		df.setTimeZone(TimeZone.getTimeZone("GMT"));
		if (duration >= ONE_DAY_IN_MILLI){
			long days = duration/ONE_DAY_IN_MILLI;
			return String.format("%dd, %s", days, df.format(duration));
		}else{
			return df.format(duration);
		}
	}
	
	/**
	 * Format the length of time period between current system time and specified time to String.<br>
	 * 格式化指定时间同当前系统时间之间的时间差为字符串。
	 * <p>
	 * The time specified can be in the past or in the future, in either cases, meaningful result will be get.
	 * <p>
	 * 指定的用作同当前系统时间作比较的时间可以是在过去，也可以是在将来，这两种情况都能产生有意义的结果。
	 * 
	 * @param pastOrFutureTime	A time in the past or in the future<br>过去或未来的某个时间
	 * @return	The string presentation of the time period, 
	 * 			for example: 00:03:01.011 or 7d, 12:32:02:001.<br>
	 * 			字符串形式表示的时间长度，比如：00:03:01.011，或：7d, 12:32:02:001。
	 */
	public static String formatSince(long pastOrFutureTime){
		return format(Math.abs(System.currentTimeMillis() - pastOrFutureTime));
	}
}
