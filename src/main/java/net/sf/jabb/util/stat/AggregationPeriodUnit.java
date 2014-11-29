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

/**
 *  For units smaller than hour, they are represented as TimeUnit;
 *  For units equals to or larger then hour, they are represented as Calendar fields.
 */
public enum AggregationPeriodUnit{
    MILLISECONDS(TimeUnit.MILLISECONDS, Calendar.MILLISECOND, true), 
    SECONDS(TimeUnit.SECONDS, Calendar.SECOND, true), 
    MINUTES(TimeUnit.MINUTES, Calendar.MINUTE, true), 
	HOURS(TimeUnit.HOURS, Calendar.HOUR_OF_DAY, false), 
	DAYS(TimeUnit.DAYS, Calendar.DAY_OF_MONTH, false),  // 1 means 1st day of a month
	MONTHS(null, Calendar.MONTH, false),  // 0 means January
	YEARS(null, Calendar.YEAR, false);
    
    private TimeUnit timeUnit;
    private int calendarField;
    private boolean smallerThanHour;
    
    AggregationPeriodUnit(TimeUnit timeUnit, int calendarField, boolean smallerThanHour){
    	this.timeUnit = timeUnit;
    	this.calendarField = calendarField;
    	this.smallerThanHour = smallerThanHour;
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
	
}