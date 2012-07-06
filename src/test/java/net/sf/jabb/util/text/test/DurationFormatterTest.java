package net.sf.jabb.util.text.test;

import static org.junit.Assert.*;

import java.util.concurrent.TimeUnit;

import net.sf.jabb.util.text.DurationFormatter;

import org.junit.Test;

public class DurationFormatterTest {
	@Test
	public void test() {
		assertEquals("02:00:00.000", DurationFormatter.format(TimeUnit.HOURS.toMillis(2)));
		assertEquals("09:53:02.901", DurationFormatter.format(
				TimeUnit.HOURS.toMillis(9)
				+ TimeUnit.MINUTES.toMillis(53)
				+ TimeUnit.SECONDS.toMillis(2)
				+ 901
				));
		assertEquals("1:30", DurationFormatter.format(
				TimeUnit.HOURS.toMillis(1)
				+ TimeUnit.MINUTES.toMillis(30)
				, TimeUnit.MINUTES, true
				));
		assertEquals("1:30:00", DurationFormatter.format(
				TimeUnit.HOURS.toMillis(1)
				+ TimeUnit.MINUTES.toMillis(30)
				, TimeUnit.SECONDS, true
				));
		assertEquals("3:00", DurationFormatter.format(
				TimeUnit.MINUTES.toMillis(3)
				, TimeUnit.SECONDS, true
				));
	}

}
