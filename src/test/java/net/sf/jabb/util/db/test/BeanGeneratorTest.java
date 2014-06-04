package net.sf.jabb.util.db.test;

import static org.junit.Assert.*;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import net.sf.cglib.beans.BeanGenerator;
import net.sf.cglib.core.NamingPolicy;
import net.sf.cglib.core.Predicate;

import org.junit.Test;

public class BeanGeneratorTest {

	@Test
	public void test() {
	    final Map<String, Class<?>> properties =
	            new HashMap<String, Class<?>>();
	        properties.put("foo", Integer.class);
	        properties.put("bar", String.class);
	        properties.put("baz", int[].class);

	        final Class<?> beanClass =
	            createBeanClass("some.ClassName", properties);
	        System.out.println(beanClass);
	        for(final Method method : beanClass.getDeclaredMethods()){
	            System.out.println(method);
	        }

	}
	
	protected static Class<?> createBeanClass(final String className, Map<String, Class<?>> properties){

		    final BeanGenerator beanGenerator = new BeanGenerator();

		    /* use our own hard coded class name instead of a real naming policy */
		    beanGenerator.setNamingPolicy(new NamingPolicy(){
				@Override
				public String getClassName(String prefix, String source,
						Object key, Predicate names) {
					return className;
				}});
		    //BeanGenerator.addProperties(beanGenerator, properties);
		    beanGenerator.addProperty("newName", String.class);
		    return (Class<?>) beanGenerator.createClass();
		}

}
