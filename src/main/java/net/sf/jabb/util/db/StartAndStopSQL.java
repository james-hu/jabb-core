/*
Copyright 2012 James Hu

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
import java.sql.Statement;

import javax.sql.DataSource;

import org.springframework.context.Lifecycle;

/**
 * Helper class to execute SQL when Spring context starts and stops.
 * It should be used as singleton.
 * 
 * @author James Hu
 *
 */
public class StartAndStopSQL implements Lifecycle {
	protected boolean isRunning;
	
	protected DataSource dataSource;
	protected String startSQL;
	protected String stopSQL;
	
	/**
	 * Execute one SQL statement. RuntimeException will be thrown if SQLException was caught.
	 * @param sql the statement to be executed
	 */
	protected void executeSQL(String sql){
		Connection conn = null;
		Statement stmt = null;
		try {
			conn = dataSource.getConnection();
			stmt = conn.createStatement();
			stmt.execute(sql);
		}catch(SQLException sqle){
			throw new RuntimeException("Failed to execute SQL: " + sql, sqle);
		}finally{
			ConnectionUtility.closeConnection(conn, stmt);
		}
	}

	/* (non-Javadoc)
	 * @see org.springframework.context.Lifecycle#isRunning()
	 */
	@Override
	public boolean isRunning() {
		return isRunning;
	}

	/* (non-Javadoc)
	 * @see org.springframework.context.Lifecycle#start()
	 */
	@Override
	public void start() {
		if (startSQL != null && startSQL.length() > 0){
			executeSQL(startSQL);
		}
		isRunning = true;
	}

	/* (non-Javadoc)
	 * @see org.springframework.context.Lifecycle#stop()
	 */
	@Override
	public void stop() {
		if (stopSQL != null && stopSQL.length() > 0){
			executeSQL(stopSQL);
		}
		isRunning = false;
	}

	public DataSource getDataSource() {
		return dataSource;
	}

	public void setDataSource(DataSource dataSource) {
		this.dataSource = dataSource;
	}

	public String getStartSQL() {
		return startSQL;
	}

	public void setStartSQL(String startSQL) {
		this.startSQL = startSQL;
	}

	public String getStopSQL() {
		return stopSQL;
	}

	public void setStopSQL(String shutdownSQL) {
		this.stopSQL = shutdownSQL;
	}

}
