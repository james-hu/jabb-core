/*
Copyright 2010 Zhengmao HU (James)

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
import java.util.InvalidPropertiesFormatException;
import java.util.Properties;

/**
 * 载入Properties的工具类，它支持传统properties文件格式（.properties）和新的xml格式（.xml），
 * 也支持用“*”作为通配符匹配这两种格式的文件后缀，以及支持在文件中包含引用别的文件。
 * 
 * 
 * @author Zhengmao HU (James)
 *
 */
public class PropertiesLoader {
	public static final String DEFAULT_INCLUDE_PROPERTY_NAME	= ".include";
	public static final String DELIMITORS 			= "[ ,;\t]+";

	protected Class<?> baseClass;
	
	/**
	 * 使用相对于哪个类的位置作为资源文件的相对位置
	 * @param baseClass
	 */
	public PropertiesLoader(Class<?> baseClass){
		this.baseClass = baseClass;
	}
	
	/**
	 * 用绝对位置
	 */
	public PropertiesLoader(){
		this(null);
	}
	
	/**
	 * Load properties from resource, without handling of the inclusion. 
	 * @param name
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
			is = ClassLoader.getSystemResourceAsStream(name);
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
			return result;
		}
	}
	
	/**
	 * 载入，展开includePropertyName参数所指定的名称
	 * @param name
	 * @param includePropertyName
	 * @return
	 * @throws InvalidPropertiesFormatException
	 * @throws IOException
	 */
	public Properties load(String name, String includePropertyName) throws InvalidPropertiesFormatException, IOException{
		Properties result = loadWithoutInclude(name);
		if (result != null){
			String includedResources = result.getProperty(includePropertyName);
			if (includedResources != null){
				result.remove(includePropertyName);
				for (String includedName: includedResources.split(DELIMITORS)){
					Properties p = load(includedName, includePropertyName);
					if (p != null){
						result.putAll(p);
					}
				}
			}
		}
		return result;
	}
	
	/**
	 * 
	 * @param name
	 * @return
	 * @throws InvalidPropertiesFormatException
	 * @throws IOException
	 */
	public Properties load(String name) throws InvalidPropertiesFormatException, IOException{
		return load(name, DEFAULT_INCLUDE_PROPERTY_NAME);
	}

}
