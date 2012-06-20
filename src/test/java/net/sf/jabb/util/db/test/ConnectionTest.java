package net.sf.jabb.util.db.test;


import java.sql.Connection;
import java.sql.SQLException;

import net.sf.jabb.util.db.ConnectionUtility;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class ConnectionTest {

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}
	
	@Test
	public void none() throws SQLException{
		Connection conn = ConnectionUtility.getConnection("myTry");
		ConnectionUtility.closeConnection(conn);
	}
	
	@Test
	public void simple() throws SQLException{
		Connection conn = ConnectionUtility.getConnection("simple");
		ConnectionUtility.closeConnection(conn);
	}
	
	@Test
	public void oracle_cached() throws SQLException{
		Connection conn = ConnectionUtility.getConnection("oracle_cached");
		ConnectionUtility.closeConnection(conn);
	}
	
	@Test
	public void c3p0_basic() throws SQLException{
		Connection conn = ConnectionUtility.getConnection("c3p0_basic");
		ConnectionUtility.closeConnection(conn);
	}
	
	@Test
	public void c3p0_nopool() throws SQLException{
		Connection conn = ConnectionUtility.getConnection("c3p0_nopool");
		ConnectionUtility.closeConnection(conn);
	}

	@Test
	public void dbcp_basic() throws SQLException{
		Connection conn = ConnectionUtility.getConnection("dbcp_basic");
		ConnectionUtility.closeConnection(conn);
	}

	@Test
	public void proxool_basic() throws SQLException{
		Connection conn = ConnectionUtility.getConnection("proxool_basic");
		ConnectionUtility.closeConnection(conn);
	}

	@Test
	public void weblogic() throws SQLException{
		Connection conn = ConnectionUtility.getConnection("weblogic");
		ConnectionUtility.closeConnection(conn);
	}

	@Test
	public void try4() throws SQLException{
		Connection conn = ConnectionUtility.getConnection("try4");
		ConnectionUtility.closeConnection(conn);
	}

}
