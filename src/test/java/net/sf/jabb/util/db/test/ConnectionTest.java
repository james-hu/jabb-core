package net.sf.jabb.util.db.test;


import static org.junit.Assert.*;

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
	public void none(){
		try{
			Connection conn = ConnectionUtility.getConnection("myTry");
			ConnectionUtility.closeConnection(conn);
		}catch(SQLException e){
			e.printStackTrace();
			//fail(e.getMessage());
		}
	}
	
	@Test
	public void simple(){
		Connection conn;
		try {
			conn = ConnectionUtility.getConnection("simple");
			ConnectionUtility.closeConnection(conn);
		} catch (SQLException e) {
			e.printStackTrace();
			//fail(e.getMessage());
		}
	}
	
	@Test
	public void oracle_cached(){
		Connection conn;
		try {
			conn = ConnectionUtility.getConnection("oracle_cached");
			ConnectionUtility.closeConnection(conn);
		} catch (SQLException e) {
			e.printStackTrace();
			//fail(e.getMessage());
		}
	}
	
	@Test
	public void c3p0_basic(){
		Connection conn;
		try {
			conn = ConnectionUtility.getConnection("c3p0_basic");
			ConnectionUtility.closeConnection(conn);
		} catch (SQLException e) {
			e.printStackTrace();
			//fail(e.getMessage());
		}
	}
	
	@Test
	public void c3p0_nopool(){
		Connection conn;
		try {
			conn = ConnectionUtility.getConnection("c3p0_nopool");
			ConnectionUtility.closeConnection(conn);
		} catch (SQLException e) {
			e.printStackTrace();
			//fail(e.getMessage());
		}
	}

	@Test
	public void dbcp_basic(){
		Connection conn;
		try {
			conn = ConnectionUtility.getConnection("dbcp_basic");
			ConnectionUtility.closeConnection(conn);
		} catch (SQLException e) {
			e.printStackTrace();
			//fail(e.getMessage());
		}
	}

	@Test
	public void proxool_basic(){
		Connection conn;
		try {
			conn = ConnectionUtility.getConnection("proxool_basic");
			ConnectionUtility.closeConnection(conn);
		} catch (SQLException e) {
			e.printStackTrace();
			//fail(e.getMessage());
		}
	}

	@Test
	public void weblogic(){
		Connection conn;
		try {
			conn = ConnectionUtility.getConnection("weblogic");
			ConnectionUtility.closeConnection(conn);
		} catch (SQLException e) {
			e.printStackTrace();
			//fail(e.getMessage());
		}
	}

	@Test
	public void try4(){
		Connection conn;
		try {
			conn = ConnectionUtility.getConnection("try4");
			ConnectionUtility.closeConnection(conn);
		} catch (SQLException e) {
			e.printStackTrace();
			//fail(e.getMessage());
		}
	}

}
