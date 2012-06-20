package net.sf.jabb.util.prop.test;

import java.io.IOException;
import java.util.InvalidPropertiesFormatException;

import net.sf.jabb.util.prop.PropertiesLoader;

public class PropertiesLoaderDemo {

	/**
	 * @param args
	 * @throws IOException 
	 * @throws InvalidPropertiesFormatException 
	 */
	public static void main(String[] args) throws InvalidPropertiesFormatException, IOException {
		PropertiesLoader l = new PropertiesLoader(PropertiesLoaderDemo.class);
		System.out.println(l.load("simple.properties"));
		System.out.println(l.load("xml.xml"));
		System.out.println(l.load("inclusion.properties"));
		System.out.println(l.load("widecard.*"));
		System.out.println(l.load("multilayer.*"));
		System.out.println(l.load("loop.*"));	
	}
}
