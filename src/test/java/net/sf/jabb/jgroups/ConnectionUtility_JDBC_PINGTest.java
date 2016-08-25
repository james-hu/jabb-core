/**
 * 
 */
package net.sf.jabb.jgroups;

import static org.junit.Assert.*;

import org.junit.Test;

/**
 * @author James Hu (Zhengmao Hu)
 *
 */
public class ConnectionUtility_JDBC_PINGTest {

	@Test
	public void shouldFixSqlForPostgreSQL() {
		ConnectionUtility_JDBC_PING p = new ConnectionUtility_JDBC_PING();
		assertEquals("create table if not exists JGROUPSPING (", p.fixDdlForPostgreSQL("CREATE TABLE JGROUPSPING ("));
		assertEquals("create table if not exists JGROUPSPING (", p.fixDdlForPostgreSQL("create table JGROUPSPING ("));
		assertEquals("ping_data bytea DEFAULT NULL, ", p.fixDdlForPostgreSQL("ping_data VarBinary(5000) DEFAULT NULL, "));
		assertEquals("ping_data bytea DEFAULT NULL, ", p.fixDdlForPostgreSQL("ping_data varbinary(5000) DEFAULT NULL, "));
		assertEquals("cluster_name varchar(200) NOT NULL, ", p.fixDdlForPostgreSQL("cluster_name varchar(200) NOT NULL, "));
	}

	@Test
	public void shouldFixSqlForMySQL() {
		ConnectionUtility_JDBC_PING p = new ConnectionUtility_JDBC_PING();
		assertEquals("create table if not exists JGROUPSPING (", p.fixDdlForMySQL("CREATE TABLE JGROUPSPING ("));
		assertEquals("create table if not exists JGROUPSPING (", p.fixDdlForMySQL("create table JGROUPSPING ("));
		assertEquals("ping_data varbinary(5000) DEFAULT NULL, ", p.fixDdlForMySQL("ping_data varbinary(5000) DEFAULT NULL, "));
		assertEquals("cluster_name varchar(200) NOT NULL, ", p.fixDdlForMySQL("cluster_name varchar(200) NOT NULL, "));
	}

}
