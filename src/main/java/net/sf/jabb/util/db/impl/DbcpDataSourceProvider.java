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

package net.sf.jabb.util.db.impl;

import java.io.IOException;
import java.util.InvalidPropertiesFormatException;
import java.util.Map;
import java.util.Properties;

import javax.sql.DataSource;

import org.apache.commons.dbcp.BasicDataSourceFactory;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import net.sf.jabb.util.db.DataSourceProvider;
import net.sf.jabb.util.prop.PropertiesLoader;

/**
 * DataSourceProvider for DBCP.
 * 
 * @author Zhengmao HU (James)
 *
 */
public class DbcpDataSourceProvider implements DataSourceProvider {
	private static final Log log = LogFactory.getLog(DbcpDataSourceProvider.class);
	protected static PropertiesLoader propLoader = new PropertiesLoader();

	public DataSource createDataSource(String source, String config) {
		String[] cfgs = config.split(PropertiesLoader.DELIMITERS, 2);
		if (cfgs.length != 2){
			log.warn("Wrong configuration format for '" + source + "' : " + config);
			return null;
		}

		DataSource ds = null;
		
		try {
			DirectDataSourceConfiguration lowerConfig = new DirectDataSourceConfiguration(cfgs[0]);
			Class.forName(lowerConfig.getDriverClassName());

			Properties props = propLoader.load(cfgs[1]);
			Properties connProps = lowerConfig.getConnectionProperties();
			props.put("username", connProps.get("user"));
			connProps.remove("user");
			props.put("password", connProps.get("password"));
			connProps.remove("password");
			props.put("url", lowerConfig.getUrl());
			props.put("driverClassName", lowerConfig.getDriverClassName());
			
			StringBuilder sb = new StringBuilder();
			String oldConnProp = props.getProperty("connectionProperties");
			if (oldConnProp != null){
				sb.append(oldConnProp.trim());
			}
			for (Map.Entry<Object, Object> p: connProps.entrySet()){
				if (sb.length() > 0 && sb.charAt(sb.length() - 1) != ';'){
					sb.append(';');
				}
				sb.append(p.getKey().toString());
				sb.append('=');
				sb.append(p.getValue().toString());
			}
			props.put("connectionProperties", sb.toString());
			
			ds = BasicDataSourceFactory.createDataSource(props);
			
		} catch (InvalidPropertiesFormatException e) {
			log.warn("Wrong configuration properties file format for '" + source + "' with configuration: " + config, e);
		} catch (IOException e) {
			log.warn("Error loading configuration file for '" + source + "' with configuration: " + config, e);
		} catch (ClassNotFoundException e) {
			log.warn("Driver class not found for '" + source + "' with configuration: " + config, e);
		} catch (Exception e) {
			log.warn("Error creating data source for '" + source + "' with configuration: " + config, e);
		}
        
		return ds;
	}

}
