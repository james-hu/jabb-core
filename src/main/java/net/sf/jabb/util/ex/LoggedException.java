/*
Copyright 2012 James Hu

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
package net.sf.jabb.util.ex;

import org.apache.commons.logging.Log;

/**
 * The exception that had been logged by commons-logging.
 * @author James Hu
 *
 */
public class LoggedException extends Exception {
	private static final long serialVersionUID = -1106808263965004727L;

	static public final int TRACE = 0;
	static public final int DEBUG = 1;
	static public final int WARN = 2;
	static public final int ERROR = 3;
	static public final int FATAL = 4;
	static public final int INFO =5;

	/**
	 * Create a new instance, and at the same time, ensure the original exception is logged.
	 * 
	 * @param log		the log utility
	 * @param level		level of the log
	 * @param message	description
	 * @param cause		the original exception. If it is of type LoggedException, 
	 * 					then the newly created instance is a clone of itself.
	 */
	public LoggedException(Log log, int level, String message, Throwable cause) {
		super(cause instanceof LoggedException ? cause.getMessage() : message,
				cause instanceof LoggedException ? cause.getCause() : cause);
		if (! (cause instanceof LoggedException)){
			switch (level){
			case TRACE:
				if (log.isTraceEnabled()){
					log.trace(message, cause);
				}
				break;
			case DEBUG:
				if (log.isDebugEnabled()){
					log.debug(message, cause);
				}
				break;
			case WARN:
				if (log.isWarnEnabled()){
					log.warn(message, cause);
				}
				break;
			case FATAL:
				log.fatal(message, cause);
				break;
			case INFO:
				log.info(message, cause);
				break;
			default:
				log.error(message, cause);
			}
		}
	}
	
	/**
	 * Create a new instance, and at the same time, ensure the original exception is logged as error.
	 * 
	 * @param log		the log utility
	 * @param message	description
	 * @param cause		the original exception. If it is of type LoggedException, 
	 * 					then the newly created instance is a clone of itself.
	 */
	public LoggedException(Log log, String message, Throwable cause) {
		this(log, ERROR, message, cause);
	}
}
