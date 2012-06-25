/*
Copyright 2010,2012 James Hu

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

import java.sql.Connection;
import java.sql.SQLException;

/**
 * 
 * @author Zhengmao HU (James)
 */
public class HibernateConnectionProvider implements
	org.hibernate.service.jdbc.connections.spi.ConnectionProvider {

	private static final long serialVersionUID = 2089779204517906906L;

	public boolean isUnwrappableAs(Class unwrapType) {
		// TODO Auto-generated method stub
		return false;
	}

	public <T> T unwrap(Class<T> unwrapType) {
		// TODO Auto-generated method stub
		return null;
	}

	public Connection getConnection() throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	public void closeConnection(Connection conn) throws SQLException {
		// TODO Auto-generated method stub
		
	}

	public boolean supportsAggressiveRelease() {
		// TODO Auto-generated method stub
		return false;
	}



}
