/*
Copyright 2010-2011, 2015 Zhengmao HU (James)

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
import java.util.concurrent.atomic.AtomicLong;


/**
 * Holder of the minimum and maximum Long values. It is thread-safe.
 * 
 * @author Zhengmao HU (James)
 *
 */
public class ConcurrentLongMinMaxHolder  implements Serializable, MinMaxHolder{
	private static final long serialVersionUID = -2426326997756055169L;
	
	protected AtomicLong minRef;
	protected AtomicLong maxRef;
	
	public ConcurrentLongMinMaxHolder(){
	}
	
	public ConcurrentLongMinMaxHolder(Number min, Number max){
		reset(min, max);
	}
	
	
	synchronized protected void initializeRefs(long x){
		if (minRef == null){
			minRef = new AtomicLong(x);
		}
		if (maxRef == null){
			maxRef = new AtomicLong(x);
		}
	}

	
	@Override
	public void evaluate(long x){
		if (minRef == null || maxRef == null){
			initializeRefs(x);
			return;
		}

		long min = minRef.get();
		if (min < x){
			long max;
			do {
				max = maxRef.get();
			} while (max < x && !maxRef.compareAndSet(max, x));
		}else if (min > x){ // min > x
			while (min > x && !minRef.compareAndSet(min, x)){
				min = minRef.get();
			}
		}
		// if min == x do nothing
	}
	
	@Override
	public void evaluate(Long x) {
		if (minRef == null){
			initializeRefs(x);
			return;
		}

		long min = minRef.get();
		if (min < x){
			long max;
			do {
				max = maxRef.get();
			} while (max < x && !maxRef.compareAndSet(max, x));
		}else if (min > x){ // min > x
			while (min > x && !minRef.compareAndSet(min, x)){
				min = minRef.get();
			}
		}
		// if min == x do nothing
	}

	@Override
	public void reset(){
		minRef = null;
		maxRef = null;
	}
	
	@Override
	public void reset(Number minNumber, Number maxNumber){
		long min = minNumber.longValue();
		long max = maxNumber.longValue();
		if (min > max){
			throw new IllegalArgumentException("min value must not be greater than max value");
		}
		minRef = new AtomicLong(min);
		maxRef = new AtomicLong(max);
	}

	
	/**
	 * Merge the min/max value from another instance into this one.
	 * @param another   another instance of MinMaxHolder
	 */
	@Override
	public void merge(MinMaxHolder another){
		Long anotherMin = another.getMaxAsLong();
		if (anotherMin != null){
			evaluate(anotherMin);
		}
		Long anotherMax = another.getMaxAsLong();
		if (anotherMax != null){
			evaluate(anotherMax);
		}
	}
	
	@Override
	public Long getMinAsLong(){
		return minRef == null ? null : minRef.get();
	}
	
	@Override
	public Long getMaxAsLong(){
		return maxRef == null ? null : maxRef.get();
	}
	
	@Override
	public String toString(){
		return "(" + getMin() + ", " + getMax() + ")";
	}

	@Override
	public void evaluate(float x) {
		evaluate((long)x);
	}

	@Override
	public void evaluate(Float x) {
		evaluate(x.longValue());
	}

	@Override
	public void evaluate(double x) {
		evaluate((long)x);
	}

	@Override
	public void evaluate(Double x) {
		evaluate(x.longValue());
	}

	@Override
	public void evaluate(BigInteger x) {
		evaluate(x.longValue());
	}

	@Override
	public void evaluate(BigDecimal x) {
		evaluate(x.longValue());
	}

	@Override
	public Float getMinAsFloat() {
		return minRef == null ? null : Float.valueOf(minRef.get());
	}

	@Override
	public Float getMaxAsFloat() {
		return maxRef == null ? null : Float.valueOf(maxRef.get());
	}

	@Override
	public Double getMinAsDouble() {
		return minRef == null ? null : Double.valueOf(minRef.get());
	}

	@Override
	public Double getMaxAsDouble() {
		return maxRef == null ? null : Double.valueOf(maxRef.get());
	}

	@Override
	public BigInteger getMinAsBigInteger() {
		return minRef == null ? null : BigInteger.valueOf(minRef.get());
	}

	@Override
	public BigInteger getMaxAsBigInteger() {
		return maxRef == null ? null : BigInteger.valueOf(maxRef.get());
	}

	@Override
	public BigDecimal getMinAsBigDecimal() {
		return minRef == null ? null : BigDecimal.valueOf(minRef.get());
	}

	@Override
	public BigDecimal getMaxAsBigDecimal() {
		return maxRef == null ? null : BigDecimal.valueOf(maxRef.get());
	}

	@Override
	public void evaluate(Number x) {
		evaluate(x.longValue());
	}

	@Override
	public Number getMin() {
		return minRef == null ? null : minRef.get();
	}

	@Override
	public Number getMax() {
		return maxRef == null ? null : maxRef.get();
	}

	

}
