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
			calendarField = Calendar.DAY;
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