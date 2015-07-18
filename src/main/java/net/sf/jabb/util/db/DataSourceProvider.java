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

package net.sf.jabb.util.db;

import java.util.Properties;

import javax.sql.DataSource;

/**
 * 
 * @author Zhengmao HU (James)
 */
public interface DataSourceProvider {
	/**
	 * Create data source
	 * @param source	name of the data source (will be used in logging)
	 * @param config	the configuration (defined and interpreted by the provider)
	 * @return	the data source
	 */
	public DataSource createDataSource(String source, String config);
	
	/**
	 * Create data source
	 * @param source	name of the data source (will be used in logging)
	 * @param configurationProperties  overriding configuration properties. 
	 * 				Usage of this argument is totally depend on the provider,
	 *  			for example the provider may decide to ignore this argument.
	 * @param config	the configuration (defined and interpreted by the provider)
	 * @return	the data source
	 */
	public DataSource createDataSource(String source, Properties configurationProperties, String config);
	
	/**
	 * Destroy the data source
	 * @param dataSource the data source which must be previously created by the same provider
	 * @return	true if successfully destroyed, false otherwise
	 */
	public boolean destroyDataSource(DataSource dataSource);
}
