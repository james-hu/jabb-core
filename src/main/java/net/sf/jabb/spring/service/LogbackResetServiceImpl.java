/**
 * 
 */
package net.sf.jabb.spring.service;

import org.slf4j.ILoggerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.util.ContextInitializer;

import org.springframework.beans.factory.BeanNameAware;

/**
 * Implementation of LogbackResetService as a spring service.
 * When the setBeanName(...) method is called by Spring context, Logback context will be reset.
 * @author James Hu
 *
 */
public class LogbackResetServiceImpl implements LogbackResetService, BeanNameAware {
	static private final Logger logger = LoggerFactory.getLogger(LogbackResetServiceImpl.class);

	/* (non-Javadoc)
	 * @see net.sf.jabb.spring.service.LogbackResetService#resetLogback()
	 */
	@Override
	public void resetLogback() {
		logger.debug("Resetting Logback...");
		ILoggerFactory lcObject = LoggerFactory.getILoggerFactory();
		if(lcObject instanceof LoggerContext) {
			try {
				LoggerContext lc = (LoggerContext) lcObject;
				lc.reset();
				ContextInitializer ci = new ContextInitializer(lc);
				ci.autoConfig();
			} catch(Exception e) {
				logger.error("Failed to reset Logback", e);
			}	
		    logger.info("Logback has been reset.");
		}else{
			logger.error("Expected LOGBACK binding with SLF4J, but another log system has taken the place: " + lcObject.getClass().getSimpleName());
		}
	}

	@Override
	public void setBeanName(String name) {
		// setBeanName(...) method will called the earliest.
		resetLogback();
	}

}
