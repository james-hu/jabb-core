/**
 * 
 */
package net.sf.jabb.util.stat;

import java.io.Serializable;
import java.math.BigInteger;
import java.util.concurrent.atomic.AtomicReference;

/**
 * A {@code BigInteger} that may be updated atomically.  See the
 * {@link java.util.concurrent.atomic} package specification for
 * description of the properties of atomic variables. An
 * {@code AtomicBigInteger} is used in applications such as atomically
 * incremented sequence numbers, and cannot be used as a replacement
 * for a {@link java.math.BigInteger}. However, this class does extend
 * {@code Number} to allow uniform access by tools and utilities that
 * deal with numerically-based classes.

 * @author James Hu
 *
 */
public class AtomicBigInteger extends Number implements Serializable {
	private static final long serialVersionUID = 5705945430578766306L;
	
	private final AtomicReference<BigInteger> valueHolder = new AtomicReference<BigInteger>();

    public AtomicBigInteger(BigInteger bigInteger) {
        valueHolder.set(bigInteger);
    }
    
    public AtomicBigInteger(){
    	this(BigInteger.ZERO);
    }
    
	@Override
	public int hashCode(){
		BigInteger value = valueHolder.get();
		return value == null ? (int)serialVersionUID : value.hashCode();
	}

	@Override
	public boolean equals(Object o){
		if (o == this){
			return true;
		}
		
		if (o instanceof AtomicBigInteger){
			BigInteger x = ((AtomicBigInteger)o).get();
			if (x == null){
				return valueHolder.get() == null;
			}else{
				return x.equals(valueHolder.get());
			}
		/*}else if (o instanceof BigInteger){
			return ((BigInteger)o).equals(valueHolder.get()); */
		}else{
			return false;
		}
	}
    
    /**
     * Gets the current value.
     *
     * @return the current value
     */
    public BigInteger get() {
        return valueHolder.get();
    }

    /**
     * Sets to the given value.
     *
     * @param newValue the new value
     */
    public void set(BigInteger newValue) {
    	 valueHolder.set(newValue);
    }

    /**
     * Eventually sets to the given value.
     *
     * @param newValue the new value
      */
    public void lazySet(BigInteger newValue) {
    	valueHolder.lazySet(newValue);
    }

    /**
     * Atomically sets to the given value and returns the old value.
     *
     * @param newValue the new value
     * @return the previous value
     */
    public BigInteger getAndSet(BigInteger newValue) {
        while (true) {
        	BigInteger current = get();
            if (valueHolder.compareAndSet(current, newValue))
                return current;
        }
    }

    /**
     * Atomically sets the value to the given updated value
     * if the current value {@code ==} the expected value.
     *
     * @param expect the expected value
     * @param update the new value
     * @return true if successful. False return indicates that
     * the actual value was not equal to the expected value.
     */
    public boolean compareAndSet(BigInteger expect, BigInteger update) {
    	return valueHolder.compareAndSet(expect, update);
    }

    /**
     * Atomically sets the value to the given updated value
     * if the current value {@code ==} the expected value.
     *
     * <p>May <a href="package-summary.html#Spurious">fail spuriously</a>
     * and does not provide ordering guarantees, so is only rarely an
     * appropriate alternative to {@code compareAndSet}.
     *
     * @param expect the expected value
     * @param update the new value
     * @return true if successful.
     */
    public boolean weakCompareAndSet(BigInteger expect, BigInteger update) {
    	return valueHolder.weakCompareAndSet(expect, update);
    }

    /**
     * Atomically increments by one the current value.
     *
     * @return the previous value
     */
    public BigInteger getAndIncrement() {
        while (true) {
        	BigInteger current = get();
        	BigInteger next = current.add(BigInteger.ONE);
            if (compareAndSet(current, next))
                return current;
        }
    }

    /**
     * Atomically decrements by one the current value.
     *
     * @return the previous value
     */
    public BigInteger getAndDecrement() {
        while (true) {
        	BigInteger current = get();
        	BigInteger next = current.subtract(BigInteger.ONE);
            if (compareAndSet(current, next))
                return current;
        }
    }

    /**
     * Atomically adds the given value to the current value.
     *
     * @param delta the value to add
     * @return the previous value
     */
    public BigInteger getAndAdd(BigInteger delta) {
        while (true) {
        	BigInteger current = get();
        	BigInteger next = current.add(delta);
            if (compareAndSet(current, next))
                return current;
        }
    }

    /**
     * Atomically increments by one the current value.
     *
     * @return the updated value
     */
    public BigInteger incrementAndGet() {
        for (;;) {
        	BigInteger current = get();
        	BigInteger next = current.add(BigInteger.ONE);
            if (compareAndSet(current, next))
                return next;
        }
    }

    /**
     * Atomically decrements by one the current value.
     *
     * @return the updated value
     */
    public BigInteger decrementAndGet() {
        for (;;) {
        	BigInteger current = get();
        	BigInteger next = current.subtract(BigInteger.ONE);
            if (compareAndSet(current, next))
                return next;
        }
    }

    /**
     * Atomically adds the given value to the current value.
     *
     * @param delta the value to add
     * @return the updated value
     */
    public BigInteger addAndGet(BigInteger delta) {
        for (;;) {
        	BigInteger current = valueHolder.get();
        	BigInteger next = current.add(delta);
            if (valueHolder.compareAndSet(current, next))
                return next;
        }
    }

    public void add(BigInteger delta) {
        for (;;) {
        	BigInteger current = valueHolder.get();
        	BigInteger next = current.add(delta);
            if (valueHolder.compareAndSet(current, next))
                return;
        }
    }

    /**
     * Returns the String representation of the current value.
     * @return the String representation of the current value.
     */
    public String toString() {
        return get().toString();
    }


	/* (non-Javadoc)
	 * @see java.lang.Number#intValue()
	 */
	@Override
	public int intValue() {
		return get().intValue();
	}

	/* (non-Javadoc)
	 * @see java.lang.Number#longValue()
	 */
	@Override
	public long longValue() {
		return get().longValue();
	}

	/* (non-Javadoc)
	 * @see java.lang.Number#floatValue()
	 */
	@Override
	public float floatValue() {
		return get().floatValue();
	}

	/* (non-Javadoc)
	 * @see java.lang.Number#doubleValue()
	 */
	@Override
	public double doubleValue() {
		return get().doubleValue();
	}

}
