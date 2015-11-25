/**
 * 
 */
package net.sf.jabb.util.jms;

import javax.jms.Connection;
import javax.jms.JMSException;
import javax.jms.MessageConsumer;
import javax.jms.MessageProducer;
import javax.jms.QueueBrowser;
import javax.jms.Session;

/**
 * Utility methods for JMS
 * @author James Hu
 *
 */
public class JmsUtility {
	
	/**
	 * Generate a single line String consisted of the summary of a JMSException
	 * @param e		the exception
	 * @return		summary of the exception in one line
	 */
	static public String exceptionSummary(JMSException e){
		Throwable cause = e.getCause();
		Exception linked = e.getLinkedException();
		return "JMSException: " + e.getMessage() 
				+ ", error code: " + e.getErrorCode()
				+ ", linked exception: " 
				+ (linked == null ? null : (linked.getClass().getName() + " - " + linked.getMessage()))
				+ ", cause: " 
				+ (cause == null ? null : (cause.getClass().getName() + " - " + cause.getMessage()));
	}
	

	static public void closeSilently(MessageProducer sender, MessageConsumer consumer, Session session){
		if (sender != null){
			try{
				sender.close();
			}catch(Exception e){
				// ignore
			}
		}
		if (consumer != null){
			try{
				consumer.close();
			}catch(Exception e){
				// ignore
			}
		}
		if (session != null){
			try{
				session.close();
			}catch(Exception e){
				// ignore
			}
		}
	}

	static public void closeSilently(MessageProducer sender, Session session){
		if (sender != null){
			try{
				sender.close();
			}catch(Exception e){
				// ignore
			}
		}
		if (session != null){
			try{
				session.close();
			}catch(Exception e){
				// ignore
			}
		}
	}

	static public void closeSilently(MessageConsumer consumer, Session session){
		if (consumer != null){
			try{
				consumer.close();
			}catch(Exception e){
				// ignore
			}
		}
		if (session != null){
			try{
				session.close();
			}catch(Exception e){
				// ignore
			}
		}
	}

	static public void closeSilently(QueueBrowser browser, Session session){
		if (browser != null){
			try{
				browser.close();
			}catch(Exception e){
				// ignore
			}
		}
		if (session != null){
			try{
				session.close();
			}catch(Exception e){
				// ignore
			}
		}
	}
	
	static public void closeSilently(Session session, Connection conn){
		if (session != null){
			try{
				session.close();
			}catch(Exception e){
				// ignore
			}
		}
		if (conn != null){
			try{
				conn.close();
			}catch(Exception e){
				// ignore
			}
		}
	}
	
	static public void closeSilently(Connection conn){
		if (conn != null){
			try{
				conn.close();
			}catch(Exception e){
				// ignore
			}
		}
	}


}
