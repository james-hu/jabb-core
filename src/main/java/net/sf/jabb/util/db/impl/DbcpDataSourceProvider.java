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
import java.sql.SQLException;
import java.util.InvalidPropertiesFormatException;
import java.util.Map;
import java.util.Properties;

import javax.sql.DataSource;

import org.apache.commons.dbcp.BasicDataSource;
import org.apache.commons.dbcp.BasicDataSourceFactory;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.mchange.v2.c3p0.DataSources;

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

	@Override
	public DataSource createDataSource(String source, Properties configurationProperties, String config) {
		if (StringUtils.isBlank(config)){
			log.error("Blank configuration string for '" + source);
			return null;
		}
		
		Properties props1 = configurationProperties;
		
		DataSource ds = null;
		try {
			DirectDataSourceConfiguration lowerConfig = new DirectDataSourceConfiguration(props1);
			Class.forName(lowerConfig.getDriverClassName());

			Properties props2 = propLoader.load(config);
			if (props2 == null){
				log.error("Cannot find configuration resource for '" + source + "' at location: " + config);
			}else{
				Properties connProps = lowerConfig.getConnectionProperties();
				props2.put("username", connProps.get("user"));
				connProps.remove("user");
				props2.put("password", connProps.get("password"));
				connProps.remove("password");
				props2.put("url", lowerConfig.getUrl());
				props2.put("driverClassName", lowerConfig.getDriverClassName());
				
				StringBuilder sb = new StringBuilder();
				String oldConnProp = props2.getProperty("connectionProperties");
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
				props2.put("connectionProperties", sb.toString());
				
				ds = BasicDataSourceFactory.createDataSource(props2);
			}
		} catch (InvalidPropertiesFormatException e) {
			log.error("Wrong configuration properties file format for '" + source + "' with configuration: " + config, e);
		} catch (IOException e) {
			log.error("Error loading configuration file for '" + source + "' with configuration: " + config, e);
		} catch (ClassNotFoundException e) {
			log.error("Driver class not found for '" + source + "' with configuration: " + config, e);
		} catch (Exception e) {
			log.error("Error creating data source for '" + source + "' with configuration: " + config, e);
		}
        
		return ds;
	}

	@Override
	public DataSource createDataSource(String source, String config) {
		String[] cfgs = config.split(PropertiesLoader.DELIMITERS, 2);
		if (cfgs.length != 2){
			log.error("Wrong configuration format for '" + source + "' : " + config);
			return null;
		}

		DataSource ds = null;
		try {
			Properties props1 = propLoader.load(cfgs[0]);
			if (props1 == null){
				log.error("Cannot find configuration resource for '" + source + "' at location: " + cfgs[0]);
			}else{
				ds = createDataSource(source, props1, cfgs[1]);
			}
		} catch (InvalidPropertiesFormatException e) {
			log.error("Wrong configuration properties file format for '" + source + "' with configuration: " + config, e);
		} catch (IOException e) {
			log.error("Error loading configuration file for '" + source + "' with configuration: " + config, e);
		} catch (Exception e) {
			log.error("Error creating data source for '" + source + "' with configuration: " + config, e);
		}
        
		return ds;
	}

	@Override
	public boolean destroyDataSource(DataSource dataSource) {
		if (dataSource instanceof BasicDataSource){
			try {
				((BasicDataSource) dataSource).close();
				return true;
			} catch (Exception e) {
				log.warn("Error destroying dbcp data source: " + dataSource, e);
			}
		}
		return false;
	}

}
