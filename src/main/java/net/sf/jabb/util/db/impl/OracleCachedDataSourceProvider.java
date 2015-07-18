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
import java.util.Properties;

import javax.sql.DataSource;

import net.sf.jabb.util.db.DataSourceProvider;
import net.sf.jabb.util.prop.PropertiesLoader;
import oracle.jdbc.pool.OracleDataSource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * 
 * @author Zhengmao HU (James)
 *
 */
public class OracleCachedDataSourceProvider implements DataSourceProvider {
	private static final Log log = LogFactory.getLog(OracleCachedDataSourceProvider.class);
	protected static PropertiesLoader propLoader = new PropertiesLoader();

	@Override
	public DataSource createDataSource(String source, Properties configurationProperties, String config) {
		DataSource ds = null;
		Properties props = new Properties();
		props.putAll(configurationProperties);
		try {
			OracleDataSource ods = new OracleDataSource();
			
			// see http://download.oracle.com/docs/cd/B28359_01/java.111/b31224/urls.htm
			ods.setDriverType(props.getProperty("_driverType"));
			props.remove("_driverType");
			ods.setNetworkProtocol(props.getProperty("_networkProtocol"));
			props.remove("_networkProtocol");
			ods.setPortNumber(Integer.parseInt(props.getProperty("_portNumber")));
			props.remove("_portNumber");
			ods.setServerName(props.getProperty("_serverName"));
			props.remove("_serverName");
			ods.setDatabaseName(props.getProperty("_databaseName"));
			props.remove("_databaseName");
			
			ods.setUser(props.getProperty("user"));
			props.remove("user");
			ods.setPassword(props.getProperty("password"));
			props.remove("password");
			
			ods.setConnectionProperties(props);
			// see http://download.oracle.com/docs/cd/B14117_01/java.101/b10979/conncache.htm
			ods.setConnectionCachingEnabled(true);
			ods.setConnectionCacheName(source);
			 
			ds = ods;
		} catch (SQLException e) {
			log.error("Error creating Oracle cached data source for '" + source + "' with configuration: " + configurationProperties, e);
		} catch (Exception e) {
			log.error("Error creating data source for '" + source + "' with configuration: " + configurationProperties, e);
		}
		
		return ds;
	}

	public DataSource createDataSource(String source, String config) {
		DataSource ds = null;
		try {
			Properties props = propLoader.load(config);
			if (props == null){
				log.error("Cannot find configuration resource for '" + source + "' at location: " + config);
			}else{
				ds = createDataSource(source, props, null);
			}
		} catch (InvalidPropertiesFormatException e) {
			log.error("Wrong configuration properties file format for '" + source + "' with configuration: " + config, e);
		} catch (IOException e) {
			log.error("Error loading configuration file for '" + source + "' with configuration: " + config, e);
		}
		return ds;
	}

	@Override
	public boolean destroyDataSource(DataSource dataSource) {
		if (dataSource instanceof OracleDataSource){
			try {
				((OracleDataSource) dataSource).close();
				return true;
			} catch (Exception e) {
				log.warn("Error destroying oracle data source: " + dataSource, e);
			}
		}
		return false;
	}

}
