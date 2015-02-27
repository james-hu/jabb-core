/*
Copyright 2015 Zhengmao HU (James Hu)

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

import org.apache.commons.lang3.StringUtils;

/**
 * A period of time qualified by a quantity and a unit.
 * @author James Hu
 *
 */
public class TimePeriod {
	protected long quantity;
	protected TimePeriodUnit unit;
	
	public TimePeriod(){
	}
	
	public TimePeriod(TimePeriodUnit unit){
		this(1, unit);
	}

	
	public TimePeriod(long quantity, TimePeriodUnit unit){
		this();
		this.quantity = quantity;
		this.unit = unit;
	}
	
	/**
	 * Parse strings like '1 hour', '2 days', '3 Years', '12 minute' into TimePeriod.
	 * @param quantityAndUnit	the string to be parsed
	 * @return	Both quantity and unit
	 */
	static public TimePeriod of(String quantityAndUnit) {
		String[] durationAndUnit = StringUtils.split(quantityAndUnit);
		Long duration = Long.valueOf(durationAndUnit[0]);
		TimePeriodUnit unit = TimePeriodUnit.of(durationAndUnit[1]);
		return new TimePeriod(duration, unit);
	}


	
	public long toMilliseconds(){
		return quantity * unit.toMilliseconds();
	}
	
	public boolean isDivisorOf(TimePeriod that){
		switch(that.unit){
			case YEARS:
				switch(this.unit){
					case YEARS:
					case WEEKS:
					case DAYS:
						return (that.quantity % this.quantity) == 0;
					case MONTHS:
						return ((that.quantity * 12) % this.quantity) == 0;
					default:
						return this.isDivisorOf(new TimePeriod(that.quantity, TimePeriodUnit.DAYS));
				}
			case MONTHS:
				switch(this.unit){
					case YEARS:
						return (that.quantity % (this.quantity * 12)) == 0;
					case MONTHS:
					case WEEKS:
					case DAYS:
						return (that.quantity % this.quantity) == 0;
					default:
						return this.isDivisorOf(new TimePeriod(that.quantity, TimePeriodUnit.DAYS));
				}
			case WEEKS:
				switch(this.unit){
					case YEARS:
					case MONTHS:
					case WEEKS:
					case DAYS:
						return (that.quantity % this.quantity) == 0;
					default:
						return this.isDivisorOf(new TimePeriod(that.quantity, TimePeriodUnit.DAYS));
				}
			case DAYS:
				switch(this.unit){
					case WEEKS:
						return (that.quantity % (this.quantity * 7)) == 0;
					case YEARS:
					case MONTHS:
					case DAYS:
						return (that.quantity % this.quantity) == 0;
					default:
						return that.toMilliseconds() % this.toMilliseconds() == 0;
				}
			default:
				switch(this.unit){
					case YEARS:
					case MONTHS:
					case WEEKS:
						return (that.quantity % this.quantity) == 0;
					default:
						return that.toMilliseconds() % this.toMilliseconds() == 0;
				}
		}
	}
	
	@Override
	public String toString(){
		return String.valueOf(quantity) + " " + unit;
	}
	
	public long getQuantity() {
		return quantity;
	}
	public void setQuantity(long quantity) {
		this.quantity = quantity;
	}
	public TimePeriodUnit getUnit() {
		return unit;
	}
	public void setUnit(TimePeriodUnit unit) {
		this.unit = unit;
	}

}
