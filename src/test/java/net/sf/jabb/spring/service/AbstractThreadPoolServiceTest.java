/**
 * 
 */
package net.sf.jabb.spring.service;

import java.util.concurrent.ScheduledThreadPoolExecutor;

import org.junit.Test;

/**
 * @author James Hu (Zhengmao Hu)
 *
 */
public class AbstractThreadPoolServiceTest {

	@Test
	public void shouldNoExceptionThrownFromSetRemoveOnCancelPolicy(){
		ScheduledThreadPoolExecutor p = new ScheduledThreadPoolExecutor(10);
		AbstractThreadPoolService.setRemoveOnCancelPolicy(p, true);

	}
}
