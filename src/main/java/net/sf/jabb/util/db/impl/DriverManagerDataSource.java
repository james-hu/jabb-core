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

import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.util.Properties;
import java.util.logging.Logger;

import javax.sql.DataSource;

/**
 * 
 * @author Zhengmao HU (James)
 *
 */
public class DriverManagerDataSource implements DataSource {
	protected String url;
	protected Properties info;
	
	public DriverManagerDataSource(String driverClassName, String url, Properties info) throws ClassNotFoundException{
		Class.forName(driverClassName);
		this.url = url;
		this.info = info;
	}

	public DriverManagerDataSource(String url, Properties info){
		this.url = url;
		this.info = info;
	}

	public DriverManagerDataSource(String url){
		this.url = url;
		this.info = null;
	}

	public PrintWriter getLogWriter() throws SQLException {
		return DriverManager.getLogWriter();
	}

	public void setLogWriter(PrintWriter out) throws SQLException {
		DriverManager.setLogWriter(out);
	}

	public void setLoginTimeout(int seconds) throws SQLException {
		DriverManager.setLoginTimeout(seconds);
	}

	public int getLoginTimeout() throws SQLException {
		return DriverManager.getLoginTimeout();
	}

	public <T> T unwrap(Class<T> iface) throws SQLException {
		throw new SQLException("Not supported: unwrap()");
	}

	public boolean isWrapperFor(Class<?> iface) throws SQLException {
		return false;
	}

	public Connection getConnection() throws SQLException {
		if (info != null){
			return DriverManager.getConnection(url, info);
		}else{
			return DriverManager.getConnection(url);
		}
	}

	public Connection getConnection(String username, String password)
			throws SQLException {
		if (info != null){
			Properties p = new Properties();
			p.putAll(info);
			p.put("user", username);
			p.put("password", password);
			return DriverManager.getConnection(url, info);
		}else{
			return DriverManager.getConnection(url, username, password);
		}
	}

	//@Override
	public Logger getParentLogger() throws SQLFeatureNotSupportedException {
		throw new SQLFeatureNotSupportedException("Not supported.");
	}

	public String getUrl() {
		return url;
	}

}
