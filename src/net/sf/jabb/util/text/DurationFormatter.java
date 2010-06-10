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
 * ��ʽ��ʱ�����Ϣ����������: 00:03:01.011����7d, 12:32:02:001��
 * �����÷��������ģ�
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
	 * ��ʽ��ʱ�䳤��
	 * @param duration	�Ժ���Ϊ��λ��ʱ�䳤��
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
	 * ��ʽ��ָ��ʱ��ͬ��ǰϵͳʱ��֮���ʱ���
	 * @param previousTime	ָ�����ĸ�ʱ��ͬ��ǰϵͳʱ�����Ƚ�
	 * @return
	 */
	public static String formatSince(long previousTime){
		return format(System.currentTimeMillis() - previousTime);
	}
}
