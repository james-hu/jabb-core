/*
Copyright 2010-2012 Zhengmao HU (James)

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

package net.sf.jabb.util.db.impl;

import java.io.IOException;
import java.util.InvalidPropertiesFormatException;
import java.util.Properties;

import net.sf.jabb.util.prop.PropertiesLoader;

/**
 * 
 * @author Zhengmao HU (James)
 *
 */
public class DirectDataSourceConfiguration {
	protected static PropertiesLoader propLoader = new PropertiesLoader();
	protected Properties connectionProperties;
	protected String driverClassName;
	protected String url;
	
	protected void initialize(Properties props){
		connectionProperties = new Properties();
		connectionProperties.putAll(props);
		
		driverClassName = connectionProperties.getProperty("_driver");
		connectionProperties.remove("_driver");
		url = connectionProperties.getProperty("_url");
		connectionProperties.remove("_url");
	}
	
	public DirectDataSourceConfiguration(Properties configProperties){
		initialize(configProperties);
	}
	
	public DirectDataSourceConfiguration(String config) throws InvalidPropertiesFormatException, IOException{
		Properties props = propLoader.load(config);
		if (props == null){
			throw new IOException("Configuration resource not found: " + config);
		}
		initialize(props);
	}

	public Properties getConnectionProperties() {
		return connectionProperties;
	}

	public String getDriverClassName() {
		return driverClassName;
	}

	public String getUrl() {
		return url;
	}
	
}
