package net.sf.jabb.quartz;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.quartz.spi.TriggerFiredBundle;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.scheduling.quartz.SpringBeanJobFactory;

/**
 * Enhanced SpringBeanJobFactory with autowiring capability.
 * <br>To use it, configure this for Quartz: 
 * org.quartz.scheduler.jobFactory.class = net.sf.jabb.quartz.AutowiringSpringBeanJobFactory
 * <br> And, configure this for Spring: &lt;bean id=... class="net.sf.jabb.quartz.AutowiringSpringBeanJobFactory"/&gt;
 * 
 * <br>If there are multiple ApplicationContext instances and you need to choose one from them, then you need
 * to extend this class and override setter and setter methods of beanFactory.
 * 
 * <br>Inspired by: https://gist.github.com/jelies/5085593
 * 
 * @author james.hu
 *
 */
public class AutowiringSpringBeanJobFactory extends SpringBeanJobFactory implements ApplicationContextAware{
	private static final Log log = LogFactory.getLog(AutowiringSpringBeanJobFactory.class);

	static private AutowireCapableBeanFactory beanFactory;
	
	@Override
	public void setApplicationContext(ApplicationContext appContext)
			throws BeansException {
		setBeanFacotry(appContext.getAutowireCapableBeanFactory());
	}
	
	@Override
	protected Object createJobInstance(TriggerFiredBundle bundle) throws Exception  {
		final Object job = super.createJobInstance(bundle);
		AutowireCapableBeanFactory f = null;
		f = getBeanFactory();	
		for (int i = 0; i < 60 && f == null; i ++){		// what if setApplicationContext() has not been invoked yet
			try{
				Thread.sleep(1000);
			}catch(Exception e){
				// ignore
			}
			f = getBeanFactory();
		}
		if (f != null){
			f.autowireBean(job);
		}else{
			log.error("Didn't do autowiring because ApplicationContext has not been injected.");
		}
		return job;
	}
	
	/**
	 * Subclass should override this method to set beanFactory to somewhere globally.
	 * @param beanFactory
	 */
	protected void setBeanFacotry(AutowireCapableBeanFactory beanFactory){
		AutowiringSpringBeanJobFactory.beanFactory = beanFactory;
	}
	
	/**
	 * Subclass should override this method to set beanFactory to somewhere globally.
	 * @return
	 */
	protected AutowireCapableBeanFactory getBeanFactory(){
		return AutowiringSpringBeanJobFactory.beanFactory;
	}
	
}
