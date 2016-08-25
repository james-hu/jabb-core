/**
 * 
 */
package net.sf.jabb.jgroups;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.SQLException;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.sql.DataSource;

import org.jgroups.conf.ClassConfigurator;
import org.jgroups.protocols.JDBC_PING;

import net.sf.jabb.util.db.ConnectionUtility;

/**
 * Subclass of JDBC_PING that can use data sources managed by {@link net.sf.jabb.util.db.ConnectionUtility}
 * The name defined in datasource_jndi_name property is considered as the name of data source managed by ConnectionUtility
 * rather than the actual JNDI name
 * @author James Hu
 *
 */
public class ConnectionUtility_JDBC_PING extends JDBC_PING {
	protected AtomicBoolean neverConnected = new AtomicBoolean(false);
	
	static {
        ClassConfigurator.addProtocol((short) 1545, ConnectionUtility_JDBC_PING.class);
    }
	
	@Override
    protected DataSource getDataSourceFromJNDI(String name) {
		DataSource ds = ConnectionUtility.getDataSource(name);
		if (ds == null){
			throw new IllegalArgumentException(
                    "Data source name defined in 'datasource_jndi_name' property is not found: " + name);
		}
		return ds;
    }

	@Override
	protected Connection getConnection() {
		Connection conn = super.getConnection();
		if (neverConnected.compareAndSet(false, true)){
			DatabaseMetaData meta;
			try {
				meta = conn.getMetaData();
				String dbName = meta.getDatabaseProductName();
				if ("PostgreSQL".equals(dbName)){
					this.initialize_sql = fixDdlForPostgreSQL(this.initialize_sql);
				}else if ("MySQL".equals(dbName)){
					this.initialize_sql = fixDdlForMySQL(this.initialize_sql);
				}
			} catch (SQLException e) {
				log.warn("Unable to replace SQL according to database type", e);
			}
		}
		return conn;
	}
	
	String fixDdlForPostgreSQL(String sql){
		String s = sql.replaceAll("(?i)varbinary\\([0-9]+\\)", "bytea");
		s = s.replaceAll("(?i)CREATE TABLE", "create table if not exists");
		return s;
	}
	
	String fixDdlForMySQL(String sql){
		String s = sql.replaceAll("(?i)CREATE TABLE", "create table if not exists");
		return s;
	}

}
