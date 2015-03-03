package net.sf.jabb.util.stat;

import java.math.BigDecimal;
import java.math.BigInteger;

/**
 * Holder of statistics about numbers
 * @author James Hu
 *
 * @param <T>
 */
public interface NumberStatistics<T extends Number> {

	/**
	 * Merge the statistics from another object into this one
	 * @param other
	 */
	public void merge(NumberStatistics<? extends Number> other);

	/**
	 * 把一个值提供给统计
	 * @param value		需要提供给统计的值
	 */
	public void evaluate(int value);

	/**
	 * 把一个值提供给统计
	 * @param value		需要提供给统计的值
	 */
	public void evaluate(long value);
	
	/**
	 * 把一个值提供给统计
	 * @param value		需要提供给统计的值
	 */
	public void evaluate(BigInteger value);

	/**
	 * 取得平均值。如果此前并没有提供任何值给统计用，则return null。
	 * @return	平均值
	 */
	public Double getAvg();
	
	/**
	 * 取得平均值。如果此前并没有提供任何值给统计用，则return null。
	 * @return	平均值
	 */
	public BigDecimal getAvg(int scale);

	/**
	 * 取得最小值。如果此前并没有提供任何值给统计用，则return null。
	 * @return	最小值
	 */
	public T getMin();

	/**
	 * 取得最大值。如果此前并没有提供任何值给统计用，则return null。
	 * @return	最大值
	 */
	public T getMax();

	/**
	 * 取得加总值。如果此前并没有提供任何值给统计用，则返回0。
	 * @return	累加值
	 */
	public T getSum();

	/**
	 * 取得已经提供给统计的值的个数。如果此前并没有提供任何值给统计用，则返回0。
	 * @return	个数
	 */
	public long getCount();

	/**
	 * Reset to initial status.<br>
	 * 回复到初始状态。
	 */
	public void reset();

	/**
	 * Reset to initial status.<br>
	 * 回复到初始状态。
	 */
	public void reset(T newCount, T newSum, T newMin, T newMax);

}