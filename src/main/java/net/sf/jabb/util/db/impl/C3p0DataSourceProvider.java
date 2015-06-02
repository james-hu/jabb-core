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

import javax.sql.DataSource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.mchange.v2.c3p0.DataSources;
import com.mchange.v2.c3p0.PooledDataSource;

import net.sf.jabb.util.db.DataSourceProvider;
import net.sf.jabb.util.prop.PropertiesLoader;

/**
 * 
 * @author Zhengmao HU (James)
 */
public class C3p0DataSourceProvider implements DataSourceProvider {
	private static final Log log = LogFactory.getLog(C3p0DataSourceProvider.class);
	protected static PropertiesLoader propLoader = new PropertiesLoader();

	public DataSource createDataSource(String source, String config) {
		String[] cfgs = config.split(PropertiesLoader.DELIMITERS, 2);
		if (cfgs.length < 1 || cfgs.length > 2){
			log.warn("Wrong configuration format for '" + source + "' : " + config);
			return null;
		}
		DataSource ds = null;
		try {
			DirectDataSourceConfiguration lowerConfig = new DirectDataSourceConfiguration(cfgs[0]);
			Class.forName(lowerConfig.getDriverClassName());
			DataSource unpooled = DataSources.unpooledDataSource(lowerConfig.getUrl(),lowerConfig.getConnectionProperties());
			if (cfgs.length == 2){
				ds = DataSources.pooledDataSource(unpooled, propLoader.load(cfgs[1]));
			}else{
				ds = unpooled;
			}
		} catch (InvalidPropertiesFormatException e) {
			log.warn("Wrong configuration properties file format for '" + source + "' with configuration: " + config, e);
		} catch (IOException e) {
			log.warn("Error loading configuration file for '" + source + "' with configuration: " + config, e);
		} catch (ClassNotFoundException e) {
			log.warn("Driver class not found for '" + source + "' with configuration: " + config, e);
		} catch (SQLException e) {
			log.warn("Error getting c3p0 data source for '" + source + "' with configuration: " + config, e);
		} catch (Exception e) {
			log.warn("Error creating data source for '" + source + "' with configuration: " + config, e);
		}
		return ds;
	}

	@Override
	public boolean destroyDataSource(DataSource dataSource) {
		if (dataSource instanceof PooledDataSource){
			try {
				DataSources.destroy(dataSource);
				return true;
			} catch (Exception e) {
				log.warn("Error destroying c3p0 data source: " + dataSource, e);
			}
		}
		return false;
	}

}
