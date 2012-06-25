package net.sf.jabb.util.text.test;

import java.util.Calendar;
import java.util.GregorianCalendar;

import net.sf.jabb.util.text.DurationFormatter;
import net.sf.jabb.util.text.NameDeduplicator;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class NameDeduplicatorDemo {
	NameDeduplicator nd;
	boolean stopNow = false;

	@Before
	public void setUp() throws Exception {
		nd = new NameDeduplicator();
		stopNow = false;
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void test() throws InterruptedException {
		long t0 = System.currentTimeMillis();
		
		for (int i = 0; i < 10; i ++){
			new Thread(new Runnable(){

				public void run() {
					while (!stopNow){
						System.out.println(nd.deduplicate("The Name"));
					}
					
				}
				
			}).start();
		}
		Thread.sleep(10*1000);
		stopNow = true;
		
		long t1 = System.currentTimeMillis();
		Thread.sleep(223);
		System.out.println(DurationFormatter.formatSince(t0));  // from t0 to current time

		System.out.println(DurationFormatter.format(t1-t0));	// from t0 to t1

		Calendar calendar = new GregorianCalendar();
		calendar.set(2060, 1, 31, 8, 0);  // Jan 31 2060 8AM
		System.out.println(DurationFormatter.formatSince(calendar.getTimeInMillis()));

	}
	

}
