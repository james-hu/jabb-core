package net.sf.jabb.camel.test;

import java.nio.charset.Charset;

import net.sf.jabb.camel.CombinedRegistry;
import net.sf.jabb.camel.XmlSocketUtility;

import org.apache.camel.CamelContext;
import org.apache.camel.impl.DefaultCamelContext;
import org.apache.camel.impl.SimpleRegistry;
import org.apache.camel.spi.Registry;

public class XmlSocketDemo {

	/**
	 * @param args
	 * @throws Exception 
	 */
	public static void main(String[] args) throws Exception {
		// You must use CombinedRegistry to construct CamelContext.
		Registry registry = new CombinedRegistry(new SimpleRegistry());
		CamelContext camelContext = new DefaultCamelContext(registry);

		XmlSocketUtility.addServer(
			camelContext, 				// The CamelContext
			"tcp://localhost:9000", 	// The listening port
			false, "seda:input",		// Destination of the messages received.
					// The combination of false and seda: can be used 
					// for multi-threaded processing.
					// If single-threaded processing is desired, then use
					// the combination of true and direct: instead.
			"env:Envelope", 			// The top level tag of the XML messages
			Charset.forName("GB2312"), 	// Encoding of the XML message
			5000);						// Possible maximum length of the messages
	}

}
