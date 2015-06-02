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

import javax.sql.DataSource;

/**
 * 
 * @author Zhengmao HU (James)
 */
public interface DataSourceProvider {
	public DataSource createDataSource(String source, String config);
	public boolean destroyDataSource(DataSource dataSource);
}
