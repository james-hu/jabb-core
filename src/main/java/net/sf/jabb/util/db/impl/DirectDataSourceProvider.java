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
import java.util.Properties;

import javax.sql.DataSource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import net.sf.jabb.util.db.DataSourceProvider;
import net.sf.jabb.util.prop.PropertiesLoader;

/**
 * 
 * @author Zhengmao HU (James)
 *
 */
public class DirectDataSourceProvider implements DataSourceProvider {
	private static final Log log = LogFactory.getLog(DirectDataSourceProvider.class);
	//protected static PropertiesLoader propLoader = new PropertiesLoader();

	@Override
	public DataSource createDataSource(String source, Properties configurationProperties, String config) {
		DataSource ds = null;
		try {
			DirectDataSourceConfiguration cfg = new DirectDataSourceConfiguration(configurationProperties);
			ds = new DriverManagerDataSource(cfg.getDriverClassName(), cfg.getUrl(), cfg.getConnectionProperties()); 
		} catch (ClassNotFoundException e) {
			log.error("Driver class not found for '" + source + "' with configuration: " + configurationProperties, e);
		} catch (Exception e) {
			log.error("Error creating data source for '" + source + "' with configuration: " + configurationProperties, e);
		}
		return ds;
	}

	public DataSource createDataSource(String source, String config) {
		DataSource ds = null;
		try {
			DirectDataSourceConfiguration cfg = new DirectDataSourceConfiguration(config);
			ds = new DriverManagerDataSource(cfg.getDriverClassName(), cfg.getUrl(), cfg.getConnectionProperties()); 
		} catch (InvalidPropertiesFormatException e) {
			log.error("Wrong configuration properties file format for '" + source + "' with configuration: " + config, e);
		} catch (ClassNotFoundException e) {
			log.error("Driver class not found for '" + source + "' with configuration: " + config, e);
		} catch (IOException e) {
			log.error("Error loading configuration file for '" + source + "' with configuration: " + config, e);
		} catch (Exception e) {
			log.error("Error creating data source for '" + source + "' with configuration: " + config, e);
		}
		return ds;
	}

	@Override
	public boolean destroyDataSource(DataSource dataSource) {
		return false;
	}

}
