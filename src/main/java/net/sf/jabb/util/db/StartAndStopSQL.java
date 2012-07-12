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
import java.util.concurrent.atomic.AtomicInteger;

import javax.sql.DataSource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.Lifecycle;

/**
 * Helper class to execute SQL when Spring context starts and stops.
 * It should be used as singleton.
 * 
 * @author James Hu
 *
 */
public class StartAndStopSQL implements Lifecycle, InitializingBean, DisposableBean {
	private static final Log log = LogFactory.getLog(StartAndStopSQL.class);
	
	static final protected int UNKNOWN=0;
	static final protected int STARTING=1;
	static final protected int RUNNING=2;
	static final protected int STOPPING=3;
	
	static final protected String[] stateNames = new String[] {
		"UNKOWN",
		"STARTING",
		"RUNNING",
		"STOPPING"
	};
	
	protected AtomicInteger state = new AtomicInteger(UNKNOWN);
	
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
			log.info("SQL executed: " + sql);
		}catch(SQLException sqle){
			throw new RuntimeException("Failed to execute SQL: " + sql, sqle);
		}finally{
			ConnectionUtility.closeConnection(conn, stmt);
		}
	}

	/* (non-Javadoc)
	 * @see org.springframework.context.Lifecycle#isRunning()
	 */
	public boolean isRunning() {
		return state.get() == RUNNING;
	}

	/* (non-Javadoc)
	 * @see org.springframework.context.Lifecycle#start()
	 */
	public void start() {
		if (state.compareAndSet(UNKNOWN, STARTING)){
			log.debug("Starting...");
			if (startSQL != null && startSQL.length() > 0){
				executeSQL(startSQL);
			}
			state.set(RUNNING);
		}else{
			log.warn("Start request ignored. Current state is: " + stateNames[state.get()]);
		}
	}

	/* (non-Javadoc)
	 * @see org.springframework.context.Lifecycle#stop()
	 */
	public void stop() {
		if (state.compareAndSet(RUNNING, STOPPING)){
			log.debug("Stopping...");
			if (stopSQL != null && stopSQL.length() > 0){
				executeSQL(stopSQL);
			}
			state.set(UNKNOWN);
		}else{
			log.warn("Stop request ignored. Current state is: " + stateNames[state.get()]);
		}
	}
	
	@Override
	public void afterPropertiesSet() throws Exception {
		start();
	}

	@Override
	public void destroy() throws Exception {
		stop();
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
