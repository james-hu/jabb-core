/**
 * 
 */
package net.sf.jabb.spring.env;

import java.util.Map;

import org.springframework.core.convert.ConversionException;
import org.springframework.core.env.AbstractPropertyResolver;
import org.springframework.util.ClassUtils;

/**
 * The PropertyResolver backed by a Properties/Map
 * @author James Hu
 *
 */
public class PropertiesPropertyResolver extends AbstractPropertyResolver {
	protected Map<? extends Object, ? extends Object> properties;
	
	public PropertiesPropertyResolver(Map<? extends Object, ? extends Object> properties){
		this.properties = properties;
	}

	@Override
	public boolean containsProperty(String key) {
		return properties.containsKey(key);
	}

	@Override
	public String getProperty(String key) {
		Object value = properties.get(key);
		return value == null ? null : value.toString();
	}

	@Override
	public <T> T getProperty(String key, Class<T> targetValueType) {
		Object value = properties.get(key);
		if (value == null){
			return null;
		}else{
			return conversionService.convert(value, targetValueType);
		}
	}

	@Override
	public <T> Class<T> getPropertyAsClass(String key, Class<T> targetValueType) {
		Object value = properties.get(key);
		if (value == null){
			return null;
		}else{
			Class<?> clazz;
			if (value instanceof String) {
				try {
					clazz = ClassUtils.forName((String)value, null);
				} catch (Exception ex) {
					throw new ClassConversionException((String)value, targetValueType, ex);
				}
			} else if (value instanceof Class) {
				clazz = (Class<?>)value;
			} else {
				clazz = value.getClass();
			}

			if (!targetValueType.isAssignableFrom(clazz)) {
				throw new ClassConversionException(clazz, targetValueType);
			}
			@SuppressWarnings("unchecked")
			Class<T> targetClass = (Class<T>)clazz;
			return targetClass;
		}

	}

	@SuppressWarnings("serial")
	static class ClassConversionException extends ConversionException {
		public ClassConversionException(Class<?> actual, Class<?> expected) {
			super(String.format("Actual type %s is not assignable to expected type %s", actual.getName(), expected.getName()));
		}

		public ClassConversionException(String actual, Class<?> expected, Exception ex) {
			super(String.format("Could not find/load class %s during attempt to convert to %s", actual, expected.getName()), ex);
		}
	}

}
