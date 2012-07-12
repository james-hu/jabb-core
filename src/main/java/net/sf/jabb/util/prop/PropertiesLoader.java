/*
Copyright 2010-2011 Zhengmao HU (James)

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

import java.io.IOException;
import java.io.InputStream;
import java.util.HashSet;
import java.util.InvalidPropertiesFormatException;
import java.util.Properties;
import java.util.Set;

/**
 * Utility to load properties from file; It supports both classic text properties file 
 * (.properties) format and new {@link Properties#loadFromXML(InputStream) xml} properties file (.xml) format; 
 * &quot;*&quot; is supported as widecard to match
 * the file name name of all of the two formats; Inclusion of files via &quot;.include=&quot; 
 * syntax is also supported.<br>
 * 载入Properties的工具类；它支持传统properties文件格式（.properties）和新的{@link Properties#loadFromXML(InputStream) xml}格式（.xml），
 * 也支持用“*”作为通配符同时匹配这两种格式的文件后缀，以及支持在文件中用“.include=”包含引用别的文件。
 * 
 * @author Zhengmao HU (James)
 *
 */
public class PropertiesLoader {
	/**
	 * The default inclusion keyword, which is &quot;.include&quot;.<br>
	 * 缺省的包含属性关键字。为&quot;.include&quot;
	 */
	public static final String DEFAULT_INCLUDE_PROPERTY_NAME	= ".include";
	/**
	 * If more than one file need to be included, the delimiters that can
	 * be used among file names, which is &quot;[ ,;\t]+include&quot;.<br>
	 * 如果需要包含多个文件，文件名之间所采用的分隔符，为&quot;[ ,;\t]+include&quot;。
	 */
	public static final String DELIMITERS 			= "[ ,;\t]+";

	protected Class<?> baseClass;
	
	protected boolean replacePlaceHolders;
	
	/**
	 * Create a new instance that locates properties files via relative path.<br>
	 * 创建一个新的实例，寻找properties文件的时候用相对位置。
	 * <p>
	 * baseClass.{@link Class#getResourceAsStream(String)} will be used to read properties files.<br>
	 * 读取properties文件的时候将使用baseClass.{@link Class#getResourceAsStream(String)}。
	 * 
	 * @param baseClass	the Class that its location will be used as the base when locating properties files.<br>
	 * 					这个类的位置将会作为基准位置来寻找properties文件。
	 * @param replacePlaceHolders  if true, place holders will be replaced by system properties.<br>
	 * 					如果为true，则其中的占位标志会被系统Properties所替代。
	 * If running inside JBoss, it replace any occurrence of ${p} with the System.getProperty(p) value. 
	 * If there is no such property p defined, then the ${p} reference will remain unchanged. 
	 * If the property reference is of the form ${p:v} and there is no such property p, then 
	 * the default value v will be returned. If the property reference is of the form ${p1,p2} 
	 * or ${p1,p2:v} then the primary and the secondary properties will be tried in turn, before 
	 * returning either the unchanged input, or the default value. The property ${/} is replaced 
	 * with System.getProperty("file.separator") value and the property ${:} is replaced with 
	 * System.getProperty("path.separator").
	 */
	public PropertiesLoader(Class<?> baseClass, boolean replacePlaceHolders){
		this.baseClass = baseClass;
		this.replacePlaceHolders = replacePlaceHolders;
	}
	
	/**
	 * Create a new instance that locates properties files via relative path.<br>
	 * 创建一个新的实例，寻找properties文件的时候用相对位置。
	 * <p>
	 * baseClass.{@link Class#getResourceAsStream(String)} will be used to read properties files.<br>
	 * 读取properties文件的时候将使用baseClass.{@link Class#getResourceAsStream(String)}。
	 * 
	 * @param baseClass	the Class that its location will be used as the base when locating properties files.<br>
	 * 					这个类的位置将会作为基准位置来寻找properties文件。
	 */
	public PropertiesLoader(Class<?> baseClass){
		this(baseClass, true);
	}
	
	/**
	 * Create a new instance that locates properties files via absolute path.<br>
	 * 创建一个新的实例，寻找properties文件的时候用绝对位置。
	 * <p>
	 * Thread Context ClassLoader will be used to read properties files.<br>
	 * 读取properties文件的时候将使用Thread Context ClassLoaderg。
	 * @param replacePlaceHolders  if true, place holders will be replaced by system properties.<br>
	 * 					如果为true，则其中的占位标志会被系统Properties所替代。
	 * If running inside JBoss, it replace any occurrence of ${p} with the System.getProperty(p) value. 
	 * If there is no such property p defined, then the ${p} reference will remain unchanged. 
	 * If the property reference is of the form ${p:v} and there is no such property p, then 
	 * the default value v will be returned. If the property reference is of the form ${p1,p2} 
	 * or ${p1,p2:v} then the primary and the secondary properties will be tried in turn, before 
	 * returning either the unchanged input, or the default value. The property ${/} is replaced 
	 * with System.getProperty("file.separator") value and the property ${:} is replaced with 
	 * System.getProperty("path.separator").
	 */
	public PropertiesLoader(boolean replacePlaceHolders){
		this(null, replacePlaceHolders);
	}
	
	/**
	 * Create a new instance that locates properties files via absolute path.<br>
	 * 创建一个新的实例，寻找properties文件的时候用绝对位置。
	 * <p>
	 * ClassLoader.{@link ClassLoader#getSystemResourceAsStream(String)} will be used to read properties files.<br>
	 * 读取properties文件的时候将使用ClassLoader.{@link ClassLoader#getSystemResourceAsStream(String)}。
	 */
	public PropertiesLoader(){
		this(null, true);
	}
	
	/**
	 * Replace place holders in both keys and values with values defined in system properties.
	 * @param props
	 * @return
	 */
	protected Properties replacePlaceHolders(Properties props){
		if (replacePlaceHolders){
			Properties result = new Properties();
			for (Object keyObj: props.keySet()){
				String key = (String) keyObj;
				String value = props.getProperty(key);
				key = PlaceHolderReplacer.replaceWithProperties(key);
				value = PlaceHolderReplacer.replaceWithProperties(value);
				result.put(key, value);
			}
			return result;
		}else{
			return props;
		}
	}
	
	/**
	 * Load properties from resource, without handling of the inclusion.<br>
	 * 从指定的资源中载入properties，不处理包含关系。
	 * @param name	location of the properties file, which can ends with &quot;.*&quot;.<br>
	 * 				properties文件的位置，可以以&quot;.*&quot;结尾。
	 * @return	null if not found, otherwise return the loaded properties
	 * @throws IOException resource found, but error when reading
	 * @throws InvalidPropertiesFormatException resource found, but with wrong format
	 */
	public Properties loadWithoutInclude(String name) throws InvalidPropertiesFormatException, IOException{
		if (name.endsWith(".*")){
			String baseName = name.substring(0, name.length() - 2);
			Properties p1 = loadWithoutInclude(baseName + ".xml");
			Properties p2 = loadWithoutInclude(baseName + ".properties");
			if (p1 == null & p2 == null){	// all not found
				return null;
			}else if (p1 != null & p2 != null){	// all found
				p1.putAll(p2);
				return p1;
			}else{								// found only one
				return p1 != null ? p1 : p2;
			}
		}
		
		InputStream is = null;
		if (baseClass != null){
			 is = baseClass.getResourceAsStream(name);
		}else{
			//is = ClassLoader.getSystemResourceAsStream(name);
			is = Thread.currentThread().getContextClassLoader().getResourceAsStream(name);
		}
		if (is == null){
			return null;
		}else{
			Properties result = new Properties();
			try{
				if (name.endsWith(".xml")){
					result.loadFromXML(is);
				}else{
					result.load(is);
				}
			}finally{
				is.close();
			}
			return replacePlaceHolders(result);
		}
	}
	
	/**
	 * (Internal usage only) Load properties from resource, with handling of the inclusion.<br>
	 * （内部使用）从指定的资源中载入properties，处理包含关系。
	 * @param name	location of the properties file, which can ends with &quot;.*&quot;.<br>
	 * 				properties文件的位置，可以以&quot;.*&quot;结尾。
	 * @param includePropertyName	To override the default inclusion keyword.<br>	
	 * 								如果不想使用缺省的包含属性关键字，可以在这里指定。
	 * @param history		names of the files that have been loaded before.
	 * @return	null if not found, otherwise return the loaded properties
	 * @throws IOException resource found, but error when reading
	 * @throws InvalidPropertiesFormatException resource found, but with wrong format
	 */
	protected Properties load(String name, String includePropertyName, Set<String> history) throws InvalidPropertiesFormatException, IOException{
		if (history != null && history.contains(name)){	// to prevent looping
			throw new IllegalArgumentException("Loop found when loading properties file: " + name);
		}
		Properties result = loadWithoutInclude(name);
		if (result != null){
			String includedResources = result.getProperty(includePropertyName);
			if (includedResources != null){
				result.remove(includePropertyName);
				if (history == null){
					history = new HashSet<String>();
				}
				history.add(name);
				for (String includedName: includedResources.split(DELIMITERS)){
					Properties p = load(includedName, includePropertyName, history);
					if (p != null){
						result.putAll(p);
					}
				}
			}
		}
		return result;
	}
	
	/**
	 * Load properties from resource, with handling of the inclusion.<br>
	 * 从指定的资源中载入properties，处理包含关系。
	 * @param name	location of the properties file, which can ends with &quot;.*&quot;.<br>
	 * 				properties文件的位置，可以以&quot;.*&quot;结尾。
	 * @param includePropertyName	To override the default inclusion keyword.<br>	
	 * 								如果不想使用缺省的包含属性关键字，可以在这里指定。
	 * @return	null if not found, otherwise return the loaded properties
	 * @throws IOException resource found, but error when reading
	 * @throws InvalidPropertiesFormatException resource found, but with wrong format
	 */
	public Properties load(String name, String includePropertyName) throws InvalidPropertiesFormatException, IOException{
		return load(name, includePropertyName, null);
	}
	
	/**
	 * Load properties from resource, with handling of the inclusion.<br>
	 * 从指定的资源中载入properties，处理包含关系。
	 * @param name	location of the properties file, which can ends with &quot;.*&quot;.<br>
	 * 				properties文件的位置，可以以&quot;.*&quot;结尾。
	 * @return	null if not found, otherwise return the loaded properties
	 * @throws IOException resource found, but error when reading
	 * @throws InvalidPropertiesFormatException resource found, but with wrong format
	 */
	public Properties load(String name) throws InvalidPropertiesFormatException, IOException{
		return load(name, DEFAULT_INCLUDE_PROPERTY_NAME, null);
	}

}
