/**
 * 
 */
package net.sf.jabb.spring.service;

/**
 * The service to reset logback when it is initialized
 * @author James Hu
 *
 */
public interface LogbackResetService {

	/**
	 * Reset logback - Logback will re-read the configurations.
	 */
	public void resetLogback();
}
