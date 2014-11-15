/*
Copyright 2014 Zhengmao HU (James)

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
    NANOSECONDS, MICROSECONDS, MILLISECONDS, SECONDS, MINUTES, 
	HOURS, DAYS, MONTHS, YEARS;
    
    private TimeUnit timeUnit;
    private int calendarField;
    
    AggregationPeriodUnit(){
        String name = this.name();
		
        if ("YEARS".equals(name)){
            calendarField = Calendar.YEAR;
        }else if ("MONTHS".equals(name)){
			calendarField = Calendar.MONTH;
		}else if ("DAYS".equals(name)){
			calendarField = Calendar.DAY_OF_MONTH;
		}else if ("HOURS".equals(name)){
			calendarField = Calendar.HOUR_OF_DAY;
		}else{
			timeUnit = TimeUnit.valueOf(name);
		}
    }
	
	public boolean isSmallerThanHour(){
		return timeUnit != null;
	}
    
    public TimeUnit toTimeUnit(){
        return timeUnit;
    }
    
    public int toCalendarField(){
        return calendarField;
    }
}