/**
 * 
 */
package net.sf.jabb.jgroups;

import javax.sql.DataSource;

import net.sf.jabb.util.db.ConnectionUtility;

import org.jgroups.conf.ClassConfigurator;
import org.jgroups.protocols.JDBC_PING;

/**
 * Subclass of JDBC_PING that can use data sources managed by {@link net.sf.jabb.util.db.ConnectionUtility}
 * The name defined in datasource_jndi_name property is considered as the name of data source managed by ConnectionUtility
 * rather than the actual JNDI name
 * @author James Hu
 *
 */
public class ConnectionUtility_JDBC_PING extends JDBC_PING {
	
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

}
