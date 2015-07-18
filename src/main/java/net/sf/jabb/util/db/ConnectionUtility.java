/*
Copyright 2010 Zhengmao HU (James)

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
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

import net.sf.jabb.util.col.MapLister;
import net.sf.jabb.util.prop.PropertiesLoader;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * 数据库连接工具，它提供有关数据库连接获取、关闭的方法。
 * 它用到commons-logging来记录执行情况。
 * 
 * @author Zhengmao HU (James)
 *
 */
public class ConnectionUtility {
	private static final Log log = LogFactory.getLog(ConnectionUtility.class);
	public static final String ROOT_CONFIGURATION_FILE = "db-connections.properties";
	public static final String PROVIDER_PROPERTY_NAME	= ".provider.";
	public static final String DELIMITORS 			= PropertiesLoader.DELIMITERS;
	
	protected static Properties configuration;
	protected static Map<String, DataSource> dataSources;
	protected static Object dataSourcesStructureLock;
	
	protected static Map<String, DataSourceProvider> dataSourceProviders;
	
	static{
		readConfiguration();
		setupDataSourceProviders();

		dataSources = new HashMap<String, DataSource>();
		dataSourcesStructureLock = new Object();
	}
	
	/**
	 * 读取配置信息
	 */
	protected static void readConfiguration(){
		PropertiesLoader propLoader = new PropertiesLoader(ConnectionUtility.class);
		try {
			configuration = propLoader.load(ROOT_CONFIGURATION_FILE);
		} catch (Exception e) {
			log.error("Error when loading configuration from properties file", e);
		}
		if (configuration == null){
			log.error("Configuration properties file not found.");
		}
		log.debug("Combined configuration is:\n" + MapLister.listToString(configuration));
	}
	
	/**
	 * 根据配置信息，初始化DataSourceProvider。
	 */
	protected static void setupDataSourceProviders(){
		List<String> providerProperties = new LinkedList<String>();
		for (Object key: configuration.keySet()){
			String keyName = (String)key;
			if (keyName.startsWith(PROVIDER_PROPERTY_NAME)){
				providerProperties.add(keyName);
			}
		}
		
		dataSourceProviders = new HashMap<String, DataSourceProvider>();
		for (String keyName: providerProperties){
			String providerName = keyName.substring(PROVIDER_PROPERTY_NAME.length(), keyName.length());
			String providerClassName = configuration.getProperty(keyName);
			configuration.remove(keyName);
			DataSourceProvider dsp = null;
			try {
				dsp = (DataSourceProvider)Class.forName(providerClassName).newInstance();
				dataSourceProviders.put(providerName, dsp);
			} catch (Throwable t) {
				if (t instanceof NoClassDefFoundError){
					log.debug("DataSourceProvider not initialized for '" + providerName + "' because the provider class was not found: " + t.getMessage());
				}else{
					log.error("Cannot instantiate DataSourceProvider for '" + providerName + "': " + providerClassName, t);
				}
			}
		}
	}
	
	/**
	 * 从指定的逻辑数据库源中获取数据库连接。
	 * @param source
	 * @return the database connection
	 * @throws SQLException
	 */
	public static Connection getConnection(String source) throws SQLException {
		DataSource ds = getDataSource(source);
		if (ds == null){
			throw new SQLException("ConnectionUtility cannot get data source: " + source);
		}
		return ds.getConnection();
	}
	
	/**
	 * 为指定的逻辑数据库源获取实际可用的DataSource。
	 * @param source
	 * @return the DataSource for that name
	 */
	public static DataSource getDataSource(String source) {
		DataSource ds = dataSources.get(source);
		if (ds == null){
			synchronized (dataSourcesStructureLock){
				ds = dataSources.get(source);
				if (ds == null){
					ds = createDataSource(source);
					if (ds != null){
						dataSources.put(source, ds);
					}
				}
			}
		}
		return ds;
	}
	
	/**
	 * 为指定的逻辑数据库源创建DataSource。
	 * @param source	配置信息
	 * @return the DataSource created
	 */
	public static DataSource createDataSource(String source){
		String typeAndConfig = configuration.getProperty(source);
		if (typeAndConfig == null){
			log.error("No configuration for data source: " + source);
			return null;
		}
		String[] typeAndConfigArray = typeAndConfig.split(DELIMITORS, 2);
		if (typeAndConfigArray.length < 2){
			log.error("Wrong configuration format for data source '" + source + "': " + typeAndConfig);
			return null;
		}
		String type = typeAndConfigArray[0];
		String config = typeAndConfigArray[1];
		return createDataSource(source, type, config);
	}
	
	/**
	 * Create a data source
	 * @param source	name of the data source, can be anything
	 * @param type		type of the data source
	 * @param config	configurations, normally path to configuration files
	 * @return	the newly created data source
	 */
	public static DataSource createDataSource(String source, String type, String config){
		DataSourceProvider dsp = dataSourceProviders.get(type);
		if (dsp == null){
			log.error("Unknown data source type for '" + source + "': " + type);
			return null;
		}
		
		DataSource ds = null;
		ds = dsp.createDataSource(source, config);
		if (ds != null){
			log.info("Data source created for: " + source);
		}else{
			log.error("Creation of data source failed for: " + source);
		}
		
		return ds;
	}
	
	/**
	 * Create a data source
	 * @param source	name of the data source, can be anything
	 * @param type		type of the data source
	 * @param configs	configurations, normally paths to configuration files
	 * @return	the newly created data source
	 */
	public static DataSource createDataSource(String source, String type, String... configs){
		String config = StringUtils.join(configs, " ");
		return createDataSource(source, type, config);
	}
	
	/**
	 * Create a data source directly backed by the JDBC driver
	 * @param source	name of the data source
	 * @param properties	properties for the JDBC driver
	 * @return	the data source
	 */
	public static DataSource createDataSource(String source, Properties properties){
		return createDataSource(source, "direct", properties);
	}
	
	public static DataSource createDataSource(String source, String type, Properties properties){
		return createDataSource(source, type, properties, (String[]) null);
	}
	
	public static DataSource createDataSource(String source, String type, Properties properties, String... configs){
		DataSourceProvider dsp = dataSourceProviders.get(type);
		if (dsp == null){
			log.error("Unknown data source type for '" + source + "': " + type);
			return null;
		}
		
		DataSource ds = null;
		ds = dsp.createDataSource(source, properties, StringUtils.join(configs, " "));
		if (ds != null){
			log.info("Data source created for: " + source);
		}else{
			log.error("Creation of data source failed for: " + source);
		}
		
		return ds;
	}
	
	/**
	 * Create DataSource and bind it to JNDI
	 * @param source	configuration
	 * @param jndiName	JNDI name that the DataSource needs to be bind to
	 * @return	The DataSource created
	 */
	public static DataSource createDataSource(String source, String jndiName){
		DataSource ds = createDataSource(source);
		if (ds != null && jndiName != null){
			InitialContext ic;
			try {
				ic = new InitialContext();
				ic.bind(jndiName, ds);
			} catch (NamingException e) {
				log.error("Failed to bind data source '" + source + "' to JNDI name: " + jndiName, e);
			}
		}
		return ds;
	}	
	
	/**
	 * Destroy a data source created before.
	 * If any exception occurred, it will be logged but never propagated.
	 * @param dataSource the data source to be destroyed
	 */
	public static void destroyDataSource(DataSource dataSource){
		synchronized (dataSourcesStructureLock){
			String dsName = null;
			for (Map.Entry<String, DataSource> dsEntry: dataSources.entrySet()){
				DataSource ds = dsEntry.getValue();
				if (ds == dataSource){
					dsName = dsEntry.getKey();
				}
			}
			if (dsName != null){
				for (Map.Entry<String, DataSourceProvider> dspEntry: dataSourceProviders.entrySet()){		// try them one by one
					String dspName = dspEntry.getKey();
					DataSourceProvider dsp = dspEntry.getValue();
					try{
						if (dsp.destroyDataSource(dataSource)){
							dataSources.remove(dsName);
							break;
						}
					}catch(Exception e){
						log.error("Error when destroying data source '" + dsName + "' using provider '" + dspName + "'", e);
					}
				}
			}
		}
	}
	
	/**
	 * Destroy all the data sources created before.
	 * If any exception occurred, it will be logged but never propagated.
	 */
	public static void destroyDataSources(){
		synchronized (dataSourcesStructureLock){
			for (Map.Entry<String, DataSource> dsEntry: dataSources.entrySet()){
				String dsName = dsEntry.getKey();
				DataSource ds = dsEntry.getValue();
				for (Map.Entry<String, DataSourceProvider> dspEntry: dataSourceProviders.entrySet()){		// try them one by one
					String dspName = dspEntry.getKey();
					DataSourceProvider dsp = dspEntry.getValue();
					try{
						if (dsp.destroyDataSource(ds)){
							break;
						}
					}catch(Exception e){
						log.error("Error when destroying data source '" + dsName + "' using provider '" + dspName + "'", e);
					}
				}
			}
			dataSources.clear();
		}
	}
	
	/**
	 * 获得Statement.executeBatch()所修改的总记录数。
	 * @param batchExecuteResult	Statement.executeBatch()所返回的数组
	 * @return  0或正数，表示总记录数；-2表示未知；其余表示executeBatch()执行出错；
	 */
	public static int getBatchUpdateCount(int[] batchExecuteResult){
		int result = 0;
		for (int c: batchExecuteResult){
			if (c >= 0){
				// real count
				result += c;
			}else if (c == -2){
				// unknown
				return -2;
			}else{
				// error or others
				// do nothing
			}
		}
		return result;
	}
	
	/**
	 * Closes database Connection.
	 * No exception will be thrown even if occurred during closing,
	 * instead, the exception will be logged at warning level.
	 * 
	 * @param conn	database connection that need to be closed
	 */
	public static void closeConnection(Connection conn){
		if (conn != null) {
			try {
				conn.close();
			} catch (Exception e) {
				log.warn("Exception when closing database connection.", e);
			}
		}
	}
	
	/**
	 * Closes database Statement.
	 * No exception will be thrown even if occurred during closing,
	 * instead, the exception will be logged at warning level.
	 * 
	 * @param st	the Statement that need to be closed
	 */
	public static void closeStatement(Statement st){
		if (st != null) {
			try {
				st.close();
			} catch (Exception e) {
				log.warn("Exception when closing database statement.", e);
			}
		}
	}
	
	/**
	 * Closes database ResultSet
	 * No exception will be thrown even if occurred during closing,
	 * instead, the exception will be logged at warning level.
	 * 
	 * @param rs	the ResultSet that need to be closed
	 */
	public static void closeResultSet(ResultSet rs){
		if (rs != null) {
			try {
				rs.close();
			} catch (Exception e) {
				log.warn("Exception when closing database result set.", e);
			}
		}
	}
	

	/**
	 * Close database connection and statement.
	 * No exception will be thrown even if occurred during closing,
	 * instead, the exception will be logged at warning level.
	 * 
	 * @param conn
	 * @param st
	 */
	public static void closeConnection(Connection conn, Statement st){
		closeStatement(st);
		closeConnection(conn);
	}

	public static void closeConnection(Connection conn, Statement st1, Statement st2){
		closeStatement(st1);
		closeStatement(st2);
		closeConnection(conn);
	}

	public static void closeConnection(Connection conn, Statement st, ResultSet rs){
		closeConnection(conn, st, rs, null);
	}
	
	public static void closeConnection(Connection conn, Statement st1, Statement st2, ResultSet rs){
		closeConnection(conn, st1, st2, rs, null);
	}
	
	public static void closeConnection(Connection conn, Statement st, ResultSet rs1, ResultSet rs2){
		closeConnection(conn, st, rs1, rs2, null);
	}
	
	public static void closeConnection(Connection conn, Statement st1, Statement st2, ResultSet rs1, ResultSet rs2){
		closeConnection(conn, st1, st2, rs1, rs2, null);
	}

	public static void closeConnection(Connection conn, Statement st, ResultSet rs1, ResultSet rs2, ResultSet rs3){
		closeConnection(conn, st, rs1, rs2, rs3, null);
	}

	public static void closeConnection(Connection conn, Statement st1, Statement st2, ResultSet rs1, ResultSet rs2, ResultSet rs3){
		closeResultSet(rs1);
		closeResultSet(rs2);
		closeResultSet(rs3);
		closeStatement(st1);
		closeStatement(st2);
		closeConnection(conn);
	}

	public static void closeConnection(Connection conn, Statement st, ResultSet... rss){
		closeConnection(conn, st, null, rss);
	}

	public static void closeConnection(Connection conn, Statement st1, Statement st2, ResultSet... rss){
		for (ResultSet rs: rss){
			closeResultSet(rs);
		}
		closeStatement(st1);
		closeStatement(st2);
		closeConnection(conn);
	}
	
	/**
	 * Summarize the returned array of {@link java.sql.Statement#executeBatch()}
	 * @param result	result of {@link java.sql.Statement#executeBatch()}
	 * @return		at least how many rows had been successfully updated
	 */
	public static int totalUpdateCount(int[] result){
		int total = 0;
		for (int i: result){
			if (i >= 0){
				total += i;
			}
		}
		return total;
	}
}
