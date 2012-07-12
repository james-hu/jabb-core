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
package net.sf.jabb.util.prop;

import java.util.Properties;

import org.jboss.util.StringPropertyReplacer;

/**
 * It can replace the place holders in string with system or user-defined properties.
 * Its implementation depends on jboss-common-core.jar which is part of JBoss runtime.
 * 
 * @author James Hu
 *
 */
public class PlaceHolderReplacer {
	static private final int NO_IMPL = 0;
	static private final int JBOSS_IMPL = 1;
	
	static private int IMPL_TYPE = NO_IMPL;
	
	static{
		try {
			Class.forName("org.jboss.util.StringPropertyReplacer");
			IMPL_TYPE = JBOSS_IMPL;
		} catch (ClassNotFoundException e) {
			// ignore
		}
	}
	
	/**
	 * If running inside JBoss, it replace any occurrence of ${p} with the System.getProperty(p) value. 
	 * If there is no such property p defined, then the ${p} reference will remain unchanged. 
	 * If the property reference is of the form ${p:v} and there is no such property p, then 
	 * the default value v will be returned. If the property reference is of the form ${p1,p2} 
	 * or ${p1,p2:v} then the primary and the secondary properties will be tried in turn, before 
	 * returning either the unchanged input, or the default value. The property ${/} is replaced 
	 * with System.getProperty("file.separator") value and the property ${:} is replaced with 
	 * System.getProperty("path.separator").
	 * 
	 * @param str		the input string that substitution will be performed upon.
	 * @return
	 */
	static public String replaceWithProperties(final String str){
		return replaceWithProperties(str, null);
	}
	
	/**
	 * If running inside JBoss, it replace any occurrence of ${p} with the System.getProperty(p) value. 
	 * If there is no such property p defined, then the ${p} reference will remain unchanged. 
	 * If the property reference is of the form ${p:v} and there is no such property p, then 
	 * the default value v will be returned. If the property reference is of the form ${p1,p2} 
	 * or ${p1,p2:v} then the primary and the secondary properties will be tried in turn, before 
	 * returning either the unchanged input, or the default value. The property ${/} is replaced 
	 * with System.getProperty("file.separator") value and the property ${:} is replaced with 
	 * System.getProperty("path.separator").
	 * 
	 * @param str		the input string that substitution will be performed upon.
	 * @param props		the properties to be used instead of System.getProerty()
	 * @return
	 */
	static public String replaceWithProperties(final String str, final Properties props){
		String result = null;
		switch (IMPL_TYPE){
			case JBOSS_IMPL:
				result = StringPropertyReplacer.replaceProperties(str, props);
				break;
			default:
				result = str;
		}
		return result;
	}

}
