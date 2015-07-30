/**
 * 
 */
package net.sf.jabb.util.parallel;

/**
 * A strategy used to decide how long to backoff before attempts.
 * It takes into consideration only the number of total attempts so far.
 * @author James Hu
 *
 */
public interface BackoffStrategy {
	
	/**
	 * Returns the duration in milliseconds, to wait before next attempt.
	 * @param numAttempted		number of total attempts that have already happened
	 * @return		number of milliseconds that needs to wait before next attempt
	 */
	long computeBackoffMilliseconds(int numAttempted);
}
