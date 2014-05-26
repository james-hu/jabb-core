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

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.concurrent.atomic.AtomicInteger;

import javax.sql.DataSource;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.listener.CommonsLoggingListener;
import org.apache.tools.ant.taskdefs.SQLExec;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.Lifecycle;
import org.springframework.core.io.Resource;

/**
 * Helper class to execute SQL when Spring context starts and stops.
 * It should be used as singleton.
 * 
 * @author James Hu
 *
 */
public class StartAndStopSQL implements Lifecycle, InitializingBean, DisposableBean, ApplicationContextAware {
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
	protected String startSqlResource;
	protected String stopSqlResource;
	protected boolean useAnt = false;
	protected String startSqlCondition;
	protected String stopSqlCondition;
	
	protected ApplicationContext context;
	
	/**
	 * Execute one SQL statement. RuntimeException will be thrown if SQLException was caught.
	 * @param sql the statement to be executed
	 */
	protected void executeSQL(String sql){
		Connection conn = null;
		Statement stmt = null;
		
		if (useAnt){
			try{
				AntSqlExec sqlExec = new AntSqlExec(dataSource, sql);
				sqlExec.execute();
				log.info("SQL executed with Ant: " + sql);
			}catch(BuildException be){
				throw new RuntimeException("Failed to execute SQL with Ant (" + be.getMessage() + "): " + sql, be);
			}
		}else{
			try {
				conn = dataSource.getConnection();
				stmt = conn.createStatement();
				stmt.execute(sql);
				log.info("SQL executed: " + sql);
			}catch(SQLException sqle){
				throw new RuntimeException("Failed to execute SQL (" + sqle.getMessage() + "): " + sql, sqle);
			}finally{
				ConnectionUtility.closeConnection(conn, stmt);
			}
		}
	}
	
	/**
	 * Execute SQL statements stored in a resource. RuntimeException will be thrown if SQLException was caught.
	 * @param resource the resource where SQL statements are stored
	 */
	protected void executeSqlResource(String resource){
		Resource sqlResource = context.getResource(resource);
		InputStream in = null;
		String sql = null;
		try{
			in = sqlResource.getInputStream();
			sql = IOUtils.toString(in);
		} catch(IOException ioe){
			throw new RuntimeException("Failed to get SQL (" + ioe.getMessage() + ") from: " + resource, ioe);
		}finally{
			IOUtils.closeQuietly(in);
		}
		if (sql != null && sql.length() > 0){
			executeSQL(sql);
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
			if (StringUtils.isBlank(startSqlCondition) || isInCondition(startSqlCondition)){
				if (StringUtils.isNotBlank(startSQL)){
					executeSQL(startSQL);
				} else if (StringUtils.isNotBlank(startSqlResource)){
					executeSqlResource(startSqlResource);
				}
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
			if (StringUtils.isBlank(stopSqlCondition) || isInCondition(stopSqlCondition)){
				if (StringUtils.isNotBlank(stopSQL)){
					executeSQL(stopSQL);
				} else if (StringUtils.isNotBlank(stopSqlResource)){
					executeSqlResource(stopSqlResource);
				}
			}
			state.set(UNKNOWN);
		}else{
			log.warn("Stop request ignored. Current state is: " + stateNames[state.get()]);
		}
	}
	
	/**
	 * Check if the database is in a specific condition by checking the result of a SQL statement
	 * @param sql the SQL statement that would return a number
	 * @return true if the returned number is greater than 0
	 * @throws SQLException 
	 */
	protected boolean isInCondition(String sql){
		Connection conn = null;
		Statement stmt = null;
		ResultSet rs = null;
		try{
			conn = dataSource.getConnection();
			stmt = conn.createStatement();
			rs = stmt.executeQuery(sql);
			rs.next();
			long result = rs.getInt(1);
			log.debug("Result from the condition checking SQL is " + result + " : " + sql);
			return result > 0;
		} catch (SQLException sqle) {
			throw new RuntimeException("Unable to check condition (" + sqle.getMessage() + ") for: " + sql, sqle);
		}finally{
			ConnectionUtility.closeConnection(conn, stmt, rs);
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

	@Override
	public void setApplicationContext(ApplicationContext appContext)
			throws BeansException {
		context = appContext;
	}
	
	static class AntSqlExec extends SQLExec{
		private DataSource dataSource;
		private Connection conn;
		
		public AntSqlExec(DataSource dataSource, String sql){
			super();
			Project project = new Project();
            project.init();
            project.addBuildListener(new CommonsLoggingListener());
            setProject(project);
            setTaskType("sql");
            setTaskName("sql");
            
            this.dataSource = dataSource;
            this.setAutocommit(true);
            this.addText(sql);
		}
		
		@Override
		protected Connection getConnection() throws BuildException{
			try {
		        if (conn == null) {
		            conn = dataSource.getConnection();
		            conn.setAutoCommit(isAutocommit());
		            if (!isValidRdbms(conn)) {
		                conn = null;
		            }
		        }
		        return conn;
			} catch (SQLException e) {
				throw new BuildException("Unable to get database connection (" + e.getMessage() + ")", e);
			}
		}
		
		@Override
		public void execute() throws BuildException{
			getProject().fireBuildStarted();
			super.execute();
			getProject().fireBuildFinished(null);
		}
		
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

	public String getStartSqlResource() {
		return startSqlResource;
	}

	public String getStopSqlResource() {
		return stopSqlResource;
	}

	public void setStartSqlResource(String startSqlResource) {
		this.startSqlResource = startSqlResource;
	}

	public void setStopSqlResource(String stopSqlResource) {
		this.stopSqlResource = stopSqlResource;
	}

	public boolean isUseAnt() {
		return useAnt;
	}

	public void setUseAnt(boolean useAnt) {
		this.useAnt = useAnt;
	}

	public String getStartSqlCondition() {
		return startSqlCondition;
	}

	public void setStartSqlCondition(String startSqlCondition) {
		this.startSqlCondition = startSqlCondition;
	}

	public String getStopSqlCondition() {
		return stopSqlCondition;
	}

	public void setStopSqlCondition(String stopSqlCondition) {
		this.stopSqlCondition = stopSqlCondition;
	}


}
