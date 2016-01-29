/*
Copyright 2015 Zhengmao HU (James)

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

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.BigInteger;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

/**
 * Holder of the minimum and maximum BigInteger values. It is thread-safe.
 * @author James Hu
 *
 */
public class ConcurrentBigIntegerMinMaxHolder implements Serializable, MinMaxHolder{
	private static final long serialVersionUID = 8080025480251400931L;

	protected AtomicBigInteger minRef;
	protected AtomicBigInteger maxRef;
	
	public ConcurrentBigIntegerMinMaxHolder(){
	}
	
	public ConcurrentBigIntegerMinMaxHolder(Number min, Number max){
		reset(min, max);
	}
	
	@Override
	public int hashCode(){
		return new HashCodeBuilder()
				.append(minRef)
				.append(maxRef)
				.toHashCode();
	}

	@Override
	public boolean equals(Object o){
		if (o == this){
			return true;
		}
		if (!(o instanceof ConcurrentBigIntegerMinMaxHolder)){
			return false;
		}
		ConcurrentBigIntegerMinMaxHolder that = (ConcurrentBigIntegerMinMaxHolder)o;
		return new EqualsBuilder()
				.append(this.minRef, that.minRef)
				.append(this.maxRef, that.maxRef)
				.isEquals();
	}
	
	synchronized protected void initializeRefs(BigInteger x){
		if (minRef == null){
			minRef = new AtomicBigInteger(x);
		}
		if (maxRef == null){
			maxRef = new AtomicBigInteger(x);
		}
	}

	@Override
	public void evaluate(BigInteger x){
		if (minRef == null || maxRef == null){
			initializeRefs(x);
			return;
		}

		BigInteger min = minRef.get();
		int c = min.compareTo(x);
		if (c < 0){
			BigInteger max;
			do {
				max = maxRef.get();
			} while (max.compareTo(x) < 0 && !maxRef.compareAndSet(max, x));
		}else if (c > 0){ // c > 0
			while (c > 0 && !minRef.compareAndSet(min, x)){
				min = minRef.get();
				c = min.compareTo(x);
			}
		}
		// if c == 0 do nothing
	}
	
	@Override
	public void reset(){
		minRef = null;
		maxRef = null;
	}
	
	/**
	 * Convert a number to a big integer and try the best to preserve precision
	 * @param number	the number
	 * @return	the big integer, can be null if the number is null
	 */
	protected BigInteger toBigInteger(Number number){
		if (number == null){
			return null;
		}
		Class<?> claz = number.getClass();
		if (claz == BigInteger.class){
			return (BigInteger)number;
		}else if (claz == BigDecimal.class){
			return ((BigDecimal)number).toBigInteger();
		}else if (claz == Double.class){
			return new BigDecimal((Double)number).toBigInteger();
		}else if (claz == Float.class){
			return new BigDecimal((Float)number).toBigInteger();
		}else{
			return BigInteger.valueOf(number.longValue());
		}
	}
	
	@Override
	public void reset(Number minNumber, Number maxNumber){
		BigInteger min = toBigInteger(minNumber);
		BigInteger max = toBigInteger(maxNumber);
		
		if (min.compareTo(max) > 0){
			throw new IllegalArgumentException("min value must not be greater than max value");
		}
		minRef = new AtomicBigInteger(min);
		maxRef = new AtomicBigInteger(max);
	}
	

	/**
	 * Merge the min/max value from another instance into this one.
	 * @param another   another instance of MinMaxHolder
	 */
	@Override
	public void merge(MinMaxHolder another){
		BigInteger anotherMin = toBigInteger(another.getMin());
		if (anotherMin != null){
			evaluate(anotherMin);
		}
		BigInteger anotherMax = toBigInteger(another.getMax());
		if (anotherMax != null){
			evaluate(anotherMax);
		}
	}
	
	
	public BigInteger getMin(){
		return minRef == null ? null : minRef.get();
	}
	
	public BigInteger getMax(){
		return maxRef == null ? null : maxRef.get();
	}
	
	@Override
	public String toString(){
		return "(" + getMin() + ", " + getMax() + ")";
	}

	@Override
	public void evaluate(long x) {
		evaluate(BigInteger.valueOf(x));
	}

	@Override
	public void evaluate(Long x) {
		evaluate(BigInteger.valueOf(x));
	}

	@Override
	public void evaluate(float x) {
		evaluate(new BigDecimal(x).toBigInteger());
	}

	@Override
	public void evaluate(Float x) {
		evaluate(new BigDecimal(x).toBigInteger());
	}

	@Override
	public void evaluate(double x) {
		evaluate(new BigDecimal(x).toBigInteger());
	}

	@Override
	public void evaluate(Double x) {
		evaluate(new BigDecimal(x).toBigInteger());
	}

	@Override
	public void evaluate(BigDecimal x) {
		evaluate(x.toBigInteger());
	}

	@Override
	public void evaluate(Number x) {
		evaluate(toBigInteger(x));
	}

	@Override
	public Long getMinAsLong() {
		return minRef == null ? null : minRef.get().longValue();
	}

	@Override
	public Long getMaxAsLong() {
		return maxRef == null ? null : maxRef.get().longValue();
	}

	@Override
	public Float getMinAsFloat() {
		return minRef == null ? null : new BigDecimal(minRef.get()).floatValue();
	}

	@Override
	public Float getMaxAsFloat() {
		return maxRef == null ? null : new BigDecimal(maxRef.get()).floatValue();
	}

	@Override
	public Double getMinAsDouble() {
		return minRef == null ? null : new BigDecimal(minRef.get()).doubleValue();
	}

	@Override
	public Double getMaxAsDouble() {
		return maxRef == null ? null : new BigDecimal(maxRef.get()).doubleValue();
	}

	@Override
	public BigInteger getMinAsBigInteger() {
		return minRef == null ? null : minRef.get();
	}

	@Override
	public BigInteger getMaxAsBigInteger() {
		return maxRef == null ? null : maxRef.get();
	}

	@Override
	public BigDecimal getMinAsBigDecimal() {
		return minRef == null ? null : new BigDecimal(minRef.get());
	}

	@Override
	public BigDecimal getMaxAsBigDecimal() {
		return maxRef == null ? null : new BigDecimal(maxRef.get());
	}


}
