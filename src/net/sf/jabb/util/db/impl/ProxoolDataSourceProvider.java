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
import org.logicalcobwebs.proxool.ProxoolException;
import org.logicalcobwebs.proxool.ProxoolFacade;

import net.sf.jabb.util.db.DataSourceProvider;
import net.sf.jabb.util.prop.PropertiesLoader;

/**
 * 
 * @author Zhengmao HU (James)
 *
 */
public class ProxoolDataSourceProvider implements DataSourceProvider {
	private static Log log = LogFactory.getLog(ProxoolDataSourceProvider.class);
	protected static PropertiesLoader propLoader = new PropertiesLoader();
	

	@Override
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
			props.put("proxool.url", lowerConfig.getUrl());
			props.put("proxool.driver", lowerConfig.getDriverClassName());
			props.putAll(lowerConfig.getConnectionProperties());

			String url = "proxool." + source;
			ProxoolFacade.registerConnectionPool(url, props);
			ds = new DriverManagerDataSource(url);
		} catch (InvalidPropertiesFormatException e) {
			log.error("Wrong configuration properties file format for '" + source + "' with configuration: " + config, e);
		} catch (IOException e) {
			log.error("Error loading configuration file for '" + source + "' with configuration: " + config, e);
		} catch (ProxoolException e) {
			log.error("Error creating Proxool connection pool for '" + source + "' with configuration: " + config, e);
		} catch (Exception e) {
			log.error("Error creating data source for '" + source + "' with configuration: " + config, e);
		}
		return ds;
	}

}
