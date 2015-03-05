package net.sf.jabb.util.db;

import java.sql.Connection;
import java.sql.SQLException;

import javax.sql.DataSource;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.listener.CommonsLoggingListener;
import org.apache.tools.ant.taskdefs.SQLExec;
import org.apache.tools.ant.taskdefs.SQLExec.DelimiterType;

/**
 * Wrapper around Ant SQLExec
 * @author James Hu
 *
 */
public class AntSqlExec extends SQLExec{
	protected DataSource dataSource;
	protected Connection conn;
	
	public AntSqlExec(DataSource dataSource, String sql){
		this(dataSource, sql, null, null);
	}
	
	public AntSqlExec(DataSource dataSource, String sql, String delimiter, String delimiterType){
		super();
		Project project = new Project();
        project.init();
        project.addBuildListener(new CommonsLoggingListener());
        setProject(project);
        setTaskType("sql");
        setTaskName("sql");
        
        if (delimiter != null){
        	this.setDelimiter(delimiter);
        }
        if (delimiterType != null){
        	this.setDelimiterType((DelimiterType)DelimiterType.getInstance(DelimiterType.class, delimiterType));
        }
        
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