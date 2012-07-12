/*
Copyright 2012 James Hu

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
*/
package net.sf.jabb.util.text;

import java.lang.reflect.InvocationTargetException;
import java.util.Collection;

import org.apache.commons.beanutils.PropertyUtils;

/**
 * Handles the formatting of collections.
 * 
 * @author James Hu
 *
 */
public class CollectionFormatter {
	/**
	 * Format a collection's elements' properties to delimiter separated string.<br>
	 * Usage examples: <br>
	 * <code>
	 * 		CollectionFormatter.format(myCollection, null, ",");
	 *		CollectionFormatter.format(myCollection, "personInCharge.name.firstName", ", "); 		
	 *		CollectionFormatter.format(myCollection, "relatedPeople(InCharge).name", ", "); 		
	 *		CollectionFormatter.format(myCollection, "subordinate[3].address(home).city", " | "); 		
	 * </code>
	 * @param collection	The collection that will be formatted
	 * @param property		The property of the collection's element that will be put into the result string.
	 * 						Please see PropertyUtils.getProperty() of commons-beanutils for detail.
	 * 						Use null if the element itself needs to be put into the result string.
	 * @param separator		Used in the result string to separate each element.
	 * @param trim			true if the property need to be trimmed, false if not.
	 * @return	A string containing separated properties of the collection's elements
	 */
	static public String format(Collection<?> collection, String property, String separator, boolean trim) {
		StringBuilder sb = new StringBuilder();
		if (collection != null){
			for (Object o: collection){
				Object p = null;
				if (property == null){
					p = o;
				}else{
					try {
						p = PropertyUtils.getProperty(o, property);
					} catch (Exception e) {
						p = "ACCESS_ERROR:" + property;
					}
				}
				sb.append(p == null? "null" : (trim? p.toString().trim() : p.toString()));
				sb.append(separator);
			}
		}
		if (sb.length() > 0){
			sb.setLength(sb.length() - separator.length());
		}
		return sb.toString();
	}
	
	/**
	 * Format a collection's elements' properties to delimiter separated string, with trimming on the elements' properties<br>
	 * @param collection	The collection that will be formatted
	 * @param property		The property of the collection's element that will be put into the result string.
	 * 						Please see PropertyUtils.getProperty() of commons-beanutils for detail.
	 * @param separator		Used in the result string to separate each element.
	 * @return	A string containing separated properties of the collection's elements
	 */
	static public String format(Collection<?> collection, String property, String separator) {
		return format(collection, property, separator, true);
	}
	
	/**
	 * Format a collection's elements to ',' separated string with trimming on the elements' toString().<br>
	 * @param collection	The collection that will be formatted
	 * @return	A string containing separated properties of the collection's elements
	 */
	static public String format(Collection<?> collection){
		return format(collection, null, ",", true);
	}
	
	/**
	 * Format a collection's elements to ',' separated string.<br>
	 * @param collection	The collection that will be formatted
	 * @param trim			true if the elements' toString() need to be trimmed, false if not.
	 * @return	A string containing separated properties of the collection's elements
	 */
	static public String format(Collection<?> collection, boolean trim){
		return format(collection, null, ",", trim);
	}
	
	/**
	 * Format a collection's elements' properties to ',' separated string, with trimming on the elements' properties<br>
	 * @param collection	The collection that will be formatted
	 * @param property		The property of the collection's element that will be put into the result string.
	 * 						Please see PropertyUtils.getProperty() of commons-beanutils for detail.
	 * @return	A string containing separated properties of the collection's elements
	 */
	static public String format(Collection<?> collection, String property){
		return format(collection, property, ",", true);
	}
	
	/**
	 * Format a collection's elements' properties to ',' separated string, with trimming on the elements' properties<br>
	 * @param collection	The collection that will be formatted
	 * @param property		The property of the collection's element that will be put into the result string.
	 * 						Please see PropertyUtils.getProperty() of commons-beanutils for detail.
	 * @param trim			true if the elements' toString() need to be trimmed, false if not.
	 * @return	A string containing separated properties of the collection's elements
	 */
	static public String format(Collection<?> collection, String property, boolean trim){
		return format(collection, property, ",", trim);
	}
}
