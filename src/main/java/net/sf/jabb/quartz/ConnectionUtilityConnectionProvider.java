/**
 * 
 */
package net.sf.jabb.quartz;

import java.sql.Connection;
import java.sql.SQLException;

import net.sf.jabb.util.db.ConnectionUtility;

import org.quartz.utils.ConnectionProvider;

/**
 * It provides database connections to Quartz with ConnectionUtility
 * @author james.hu
 *
 */
public class ConnectionUtilityConnectionProvider implements ConnectionProvider {
	
	private String dataSourceName;

	/* (non-Javadoc)
	 * @see org.quartz.utils.ConnectionProvider#getConnection()
	 */
	@Override
	public Connection getConnection() throws SQLException {
		return ConnectionUtility.getConnection(dataSourceName);
	}

	/* (non-Javadoc)
	 * @see org.quartz.utils.ConnectionProvider#initialize()
	 */
	@Override
	public void initialize() throws SQLException {
	}

	/* (non-Javadoc)
	 * @see org.quartz.utils.ConnectionProvider#shutdown()
	 */
	@Override
	public void shutdown() throws SQLException {
	}

	public String getDataSourceName() {
		return dataSourceName;
	}

	public void setDataSourceName(String dataSourceName) {
		this.dataSourceName = dataSourceName;
	}

}
