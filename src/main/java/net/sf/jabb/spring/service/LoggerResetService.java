/**
 * 
 */
package net.sf.jabb.spring.service;

/**
 * The service to reset logger when it is initialized
 * @author James Hu
 *
 */
public interface LoggerResetService {

	/**
	 * Reset logger - the logger will re-read the configurations.
	 */
	public void resetLogger();
}
