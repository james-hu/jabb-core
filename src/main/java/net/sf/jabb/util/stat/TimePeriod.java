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
import org.apache.commons.lang3.builder.EqualsBuilder;

/**
 * A period of time qualified by a quantity and a unit.
 * @author James Hu
 *
 */
public class TimePeriod implements Comparable<TimePeriod>{
	protected long amount;
	protected TimePeriodUnit unit;
	
	public TimePeriod(){
	}
	
	public TimePeriod(TimePeriodUnit unit){
		this(1, unit);
	}

	
	public TimePeriod(long quantity, TimePeriodUnit unit){
		this();
		this.amount = quantity;
		this.unit = unit;
	}
	
	@Override
	public boolean equals(Object o){
		if (o == this){
			return true;
		}
		
		if (o != null && o instanceof TimePeriod){
			TimePeriod that = (TimePeriod)o;
			return new EqualsBuilder()
				.append(this.amount, that.amount)
				.append(this.unit, that.unit)
				.isEquals();
		}else{
			return false;
		}
	}
	
	@Override
	public int hashCode(){
		return (int) (amount * amount) + unit.hashCode() * 41;
	}
	
	@Override
	public int compareTo(TimePeriod that) {
		if (that == null || that.unit == null){
			return 1;
		}
		if (this.unit == null){
			return -1;
		}
		long diff = this.toMilliseconds() - that.toMilliseconds();
		if (diff > 0){
			return 1;
		}else if (diff < 0){
			return -1;
		}else{
			return 0;
		}
	}

	
	/**
	 * Parse strings like '1 hour', '2 days', '3 Years', '12 minute' into TimePeriod.
	 * @param quantityAndUnit	the string to be parsed
	 * @return	Both quantity and unit
	 * @deprecated use from(...) instead
	 */
	static public TimePeriod of(String quantityAndUnit) {
		return from(quantityAndUnit);
	}
	/**
	 * Parse strings like '1 hour', '2 days', '3 Years', '12 minute' into TimePeriod.
	 * Short formats like '1H', '2 D', '3y' are also supported.
	 * @param quantityAndUnit	the string to be parsed
	 * @return	Both quantity and unit
	 */
	static public TimePeriod from(String quantityAndUnit) {
		String trimed = quantityAndUnit.trim();
		String allExceptLast = trimed.substring(0, trimed.length() - 1);
		if (StringUtils.isNumericSpace(allExceptLast)){ // short format
			long quantity = Long.parseLong(allExceptLast.trim());
			TimePeriodUnit unit = TimePeriodUnit.from(Character.toUpperCase(trimed.charAt(trimed.length() - 1)));
			return new TimePeriod(quantity, unit);
		}else{
			String[] durationAndUnit = StringUtils.split(trimed);
			Long duration = Long.valueOf(durationAndUnit[0]);
			TimePeriodUnit unit = TimePeriodUnit.from(durationAndUnit[1]);
			return new TimePeriod(duration, unit);
		}
	}


	
	public long toMilliseconds(){
		return amount * unit.toMilliseconds();
	}
	
	public boolean isDivisorOf(TimePeriod that){
		switch(that.unit){
			case YEARS:
				switch(this.unit){
					case YEARS:
					case WEEKS:
					case DAYS:
						return (that.amount % this.amount) == 0;
					case MONTHS:
						return ((that.amount * 12) % this.amount) == 0;
					default:
						return this.isDivisorOf(new TimePeriod(that.amount, TimePeriodUnit.DAYS));
				}
			case MONTHS:
				switch(this.unit){
					case YEARS:
						return (that.amount % (this.amount * 12)) == 0;
					case MONTHS:
					case WEEKS:
					case DAYS:
						return (that.amount % this.amount) == 0;
					default:
						return this.isDivisorOf(new TimePeriod(that.amount, TimePeriodUnit.DAYS));
				}
			case WEEKS:
				switch(this.unit){
					case YEARS:
					case MONTHS:
					case WEEKS:
					case DAYS:
						return (that.amount % this.amount) == 0;
					default:
						return this.isDivisorOf(new TimePeriod(that.amount, TimePeriodUnit.DAYS));
				}
			case DAYS:
				switch(this.unit){
					case WEEKS:
						return (that.amount % (this.amount * 7)) == 0;
					case YEARS:
					case MONTHS:
					case DAYS:
						return (that.amount % this.amount) == 0;
					default:
						return that.toMilliseconds() % this.toMilliseconds() == 0;
				}
			default:
				switch(this.unit){
					case YEARS:
					case MONTHS:
					case WEEKS:
						return (that.amount % this.amount) == 0;
					default:
						return that.toMilliseconds() % this.toMilliseconds() == 0;
				}
		}
	}
	
	@Override
	public String toString(){
		return String.valueOf(amount) + " " + unit;
	}

	public String toShortString(){
		return String.format("%d%c", amount, unit.toShortCode());
	}

	/**
	 * @deprecated use getAmount() instead
	 * @return the amount
	 */
	public long getQuantity() {
		return amount;
	}
	/**
	 * @deprecated use setAmount(...) instead
	 * @param quantity the amount
	 */
	public void setQuantity(long quantity) {
		this.amount = quantity;
	}
	public long getAmount() {
		return amount;
	}
	public void setAmount(long amount) {
		this.amount = amount;
	}
	public TimePeriodUnit getUnit() {
		return unit;
	}
	public void setUnit(TimePeriodUnit unit) {
		this.unit = unit;
	}

}
