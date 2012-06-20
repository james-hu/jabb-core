package net.sf.jabb.camel.test;

import net.sf.jabb.camel.CamelContextController;

import org.apache.camel.CamelContext;
import org.apache.camel.impl.DefaultCamelContext;
import org.apache.camel.impl.SimpleRegistry;
import org.apache.camel.spi.Registry;

public class ControllerDemo {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		Registry registry = new SimpleRegistry();
		CamelContext context = new DefaultCamelContext(registry);
		CamelContextController controller;
		try {
			controller = new CamelContextController(
					context, "tcp://localhost:9001", false);
		} catch (Exception e) {
			e.printStackTrace();
			return;
		}
		controller.start();
	}

}
