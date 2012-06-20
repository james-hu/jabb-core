package net.sf.jabb.camel.test;

import java.nio.charset.Charset;

import net.sf.jabb.camel.CamelContextController;
import net.sf.jabb.camel.CombinedRegistry;
import net.sf.jabb.camel.RegistryUtility;

import org.apache.camel.CamelContext;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.impl.DefaultCamelContext;
import org.apache.camel.impl.SimpleRegistry;
import org.apache.camel.spi.Registry;
import org.jboss.netty.handler.codec.string.StringEncoder;

public class RegistryDemo {

	/**
	 * @param args
	 * @throws Exception 
	 */
	public static void main(String[] args) throws Exception {
		// You must use CombinedRegistry to construct CamelContext.
		Registry registry = new CombinedRegistry(new SimpleRegistry());
		CamelContext context = new DefaultCamelContext(registry);
		
		// Then, you can get the CombinedRegistry and put your own entry into it.
		CombinedRegistry reg = RegistryUtility.getCombinedRegistry(context);
		reg.getDefaultSimpleRegistry().put("my name", "my value");
		
		// And you can put Netty encoder into the Registry.
		StringEncoder stringEncoder = new StringEncoder(Charset.defaultCharset());
		RegistryUtility.addEncoder(context, "myStringEncoder", stringEncoder);
		context.addRoutes(new RouteBuilder(){
			@Override
			public void configure(){
				from("netty:tcp://localhost:8000?sync=true&encoders=#myStringEncoder")
				.to("seda:none");
			}
		});

	}

}
