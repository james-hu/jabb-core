/*
Copyright 2014-2015 Zhengmao HU (James Hu)

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


import java.util.Calendar;
import java.util.concurrent.TimeUnit;

/**
 * Unit of time periods. This class bridges between TimeUnit and Calendar.
 *  For units smaller than hour, they are represented as TimeUnit;
 *  For units equals to or larger than hour, they are represented as Calendar fields.
 */
public enum TimePeriodUnit{
    MILLISECONDS(TimeUnit.MILLISECONDS, Calendar.MILLISECOND, 1L, 'I'), 
    SECONDS(TimeUnit.SECONDS, Calendar.SECOND, 1000L, 'S'), 
    MINUTES(TimeUnit.MINUTES, Calendar.MINUTE, 1000L * 60, 'N'), 
	HOURS(TimeUnit.HOURS, Calendar.HOUR_OF_DAY, 1000L * 3600, 'H'), 
	DAYS(TimeUnit.DAYS, Calendar.DAY_OF_MONTH, 1000L * 3600 * 24, 'D'),  // 1 means 1st day of a month
	WEEKS(null, Calendar.DAY_OF_WEEK, 1000L * 3600 * 24 * 7, 'W'),
	MONTHS(null, Calendar.MONTH, 2630000000L, 'M'),  // 0 means January
	YEARS(null, Calendar.YEAR, 31556900000L, 'Y');
    
    private TimeUnit timeUnit;
    private int calendarField;
    private long milliseconds;
    private char code;
    
    TimePeriodUnit(TimeUnit timeUnit, int calendarField, long milliseconds, char code){
    	this.timeUnit = timeUnit;
    	this.calendarField = calendarField;
    	this.milliseconds = milliseconds;
    	this.code = code;
    }
    
    /**
     * Get the corresponding TimePeriodUnit from TimeUnit. If no matching TimePeriodUnit
     * can be found, IllegalArgumentException will be thrown.
     * @param timeUnit	the time unit, can be null
     * @return	the TimePeriodUnit, can be null if the input is null.
     */
    static public TimePeriodUnit of(TimeUnit timeUnit){
    	if (timeUnit == null){
    		return null;
    	}
    	switch(timeUnit){
	    	case MILLISECONDS:
	    		return MILLISECONDS;
	    	case SECONDS:
	    		return SECONDS;
	    	case MINUTES:
	    		return MINUTES;
	    	case HOURS:
	    		return HOURS;
	    	case DAYS:
	    		return DAYS;
	    	default:
	    		throw new IllegalArgumentException("Unsupported TimeUnit: " + timeUnit);
    	}
    }
    
    /**
     * Get the TimePeriodUnit that the one-character code represents
     * @param code	the one-character code
     * @return	the corresponding TimePeriodUnit
     */
    static public TimePeriodUnit from (char code){
    	switch(code){
	    	case 'I':
	    		return MILLISECONDS;
	    	case 'S':
	    		return SECONDS;
	    	case 'N':
	    		return MINUTES;
	    	case 'H':
	    		return HOURS;
	    	case 'D':
	    		return DAYS;
	    	case 'W':
	    		return WEEKS;
	    	case 'M':
	    		return MONTHS;
	    	case 'Y':
	    		return YEARS;
    		default:
	    		throw new IllegalArgumentException("Unknown: " + code);
    	}
    }
    
    /**
     * Get the one-character code for this unit
     * @return	the one-character code
     */
    public char toShortCode(){
    	return code;
    }
	
    /**
     * Get the corresponding TimeUnit.
     * @return corresponding TimeUnit or null for WEEKS, MONTHS and YEARS
     */
    public TimeUnit toTimeUnit(){
        return timeUnit;
    }
    
    public int toCalendarField(){
        return calendarField;
    }

	/**
	 * Number of milliseconds this unit represents. However for MONTHS and YEARS the value
	 * returned are just average numbers.
	 * @return number of milliseconds
	 */
	public long toMilliseconds(){
		return milliseconds;
	}
	
	public boolean isDivisorOf(TimePeriodUnit that){
		if (this == that){
			return true;
		}
		switch(that){
			case YEARS:
				return this != WEEKS;
			case MONTHS:
				return this != WEEKS && this != MONTHS;
			default:
				return this.isShorterThan(that); // && (that.milliseconds % this.milliseconds) == 0;
		}
	}

	public boolean isShorterThan(TimePeriodUnit another) {
		return this.toMilliseconds() < another.milliseconds;
	}

	public boolean isLongerThan(TimePeriodUnit another) {
		return this.toMilliseconds() > another.milliseconds;
	}
	
	/**
	 * Parse a string to get the TimePeriodUnit it represents.
	 * @param unitString the string to be parsed, it should just be the name of an TimePeriodUnit enum, 
	 *  but can contain leading and trailing blank spaces, 
	 * 	can have mixed upper and lower cases, and can have the last letter 's' missing. 
	 * 	For example, 'hour', 'Hour', 'HOUR', 'hours', and 'hOUrs' are all valid.
	 * @return	the TimePeriodUnit represented by the input string
	 */
	static public TimePeriodUnit from(String unitString) {
		unitString = unitString.trim().toUpperCase();
		if(unitString.charAt(unitString.length() - 1) != 'S') {
			unitString = unitString + "S";
		}
		return TimePeriodUnit.valueOf(unitString);
	}

	/**
	 * @deprecated use from(...) instead
	 * @param unitString	unitString the string to be parsed
	 * @return	the TimePeriodUnit represented by the input string
	 */
	static public TimePeriodUnit of(String unitString) {
		return from(unitString);
	}
	
}