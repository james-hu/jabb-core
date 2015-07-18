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

import java.util.Properties;

import javax.sql.DataSource;

import net.sf.jabb.util.db.ConnectionUtility;
import net.sf.jabb.util.db.DataSourceProvider;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * 一个一个尝试。
 * @author Zhengmao HU (James)
 *
 */
public class TryDataSourceProvider implements DataSourceProvider {
	private static final Log log = LogFactory.getLog(TryDataSourceProvider.class);

	@Override
	public DataSource createDataSource(String source, Properties configurationProperties, String config) {
		log.warn("Properties argument ignored for: " + source);
		return createDataSource(source, config);
	}

	@Override
	public DataSource createDataSource(String source, String config) {
		for (String subSource: config.split(ConnectionUtility.DELIMITORS)){
			DataSource ds = ConnectionUtility.getDataSource(subSource);
			if (ds != null){
				log.debug("Data source '" + subSource + "' will be used for data source '" + source + "'.");
				return ds;
			}
		}
		log.error("No usable data source found for '" + source + "'.");
		return null;
	}

	@Override
	public boolean destroyDataSource(DataSource dataSource) {
		return false;
	}

}
