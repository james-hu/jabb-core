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

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import net.sf.jabb.util.db.DataSourceProvider;

/**
 * 
 * @author Zhengmao HU (James)
 *
 */
public class JndiDataSourceProvider implements DataSourceProvider {
	private static final Log log = LogFactory.getLog(JndiDataSourceProvider.class);

	public DataSource createDataSource(String source, String config) {
		DataSource ds = null;
		try {
			ds = (DataSource) new InitialContext().lookup(config);
		} catch (NamingException e) {
			log.warn("Cannot get data source for '" + source + "' from JNDI name '" + config + "'.", e);
		} catch (Exception e) {
			log.warn("Error getting data source for '" + source + "' from JNDI name '" + config + "'.", e);
		}
		return ds;
	}

	@Override
	public boolean destroyDataSource(DataSource dataSource) {
		return false;
	}

}
