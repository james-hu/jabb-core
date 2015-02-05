/*
Copyright 2014 Zhengmao HU (James Hu)

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


import java.util.concurrent.TimeUnit;
import java.util.Calendar;

import org.apache.commons.lang3.StringUtils;

import net.sf.jabb.util.bean.DoubleValueBean;

/**
 *  For units smaller than hour, they are represented as TimeUnit;
 *  For units equals to or larger then hour, they are represented as Calendar fields.
 */
public enum AggregationPeriodUnit{
    MILLISECONDS(TimeUnit.MILLISECONDS, Calendar.MILLISECOND, true, 1L), 
    SECONDS(TimeUnit.SECONDS, Calendar.SECOND, true, 1000L), 
    MINUTES(TimeUnit.MINUTES, Calendar.MINUTE, true, 1000L * 60), 
	HOURS(TimeUnit.HOURS, Calendar.HOUR_OF_DAY, false, 1000L * 60 * 60), 
	DAYS(TimeUnit.DAYS, Calendar.DAY_OF_MONTH, false, 1000L * 3600 * 24),  // 1 means 1st day of a month
	MONTHS(null, Calendar.MONTH, false, 2630000000L),  // 0 means January
	YEARS(null, Calendar.YEAR, false, 31556900000L);
    
    private TimeUnit timeUnit;
    private int calendarField;
    private boolean smallerThanHour;
    private long milliseconds;
    
    AggregationPeriodUnit(TimeUnit timeUnit, int calendarField, boolean smallerThanHour, long milliseconds){
    	this.timeUnit = timeUnit;
    	this.calendarField = calendarField;
    	this.smallerThanHour = smallerThanHour;
    	this.milliseconds = milliseconds;
    }
	
    public TimeUnit toTimeUnit(){
        return timeUnit;
    }
    
    public int toCalendarField(){
        return calendarField;
    }

	public boolean isSmallerThanHour() {
		return smallerThanHour;
	}
	
	public long toMilliseconds(){
		return milliseconds;
	}
	
	public long toMilliseconds(int duration){
		return milliseconds * duration;
	}
	
	/**
	 * Parse a string to get the AggregationPeriodUnit
	 * @param unitString the string to be parsed, it should just be the name of an AggregationPeriodUnit enum, 
	 *  but can contain leading and trailing blank spaces, 
	 * 	can have mixed upper and lower cases, and can have the last letter 's' missing. 
	 * 	For example, 'hour', 'Hour', 'HOUR', 'hours', and 'hOUrs' are all valid.
	 * @return	the AggregationPeriodUnit represented by the input string
	 */
	static public AggregationPeriodUnit parse(String unitString) {
		unitString = unitString.trim().toUpperCase();
		if(unitString.charAt(unitString.length() - 1) != 'S') {
			unitString = unitString + "S";
		}
		return AggregationPeriodUnit.valueOf(unitString);
	}

	/**
	 * Parse strings like '1 hour', '2 days', '3 Years', '12 minute'
	 * @param quantityAndUnit	the string to be parsed
	 * @return	Both quantity and unit
	 */
	static public DoubleValueBean<Integer, AggregationPeriodUnit> parseWithQuantity(String quantityAndUnit) {
		String[] durationAndUnit = StringUtils.split(quantityAndUnit);
		Integer duration = Integer.valueOf(durationAndUnit[0]);
		AggregationPeriodUnit unit = parse(durationAndUnit[1]);
		return new DoubleValueBean<Integer, AggregationPeriodUnit>(duration, unit);
	}

	
}