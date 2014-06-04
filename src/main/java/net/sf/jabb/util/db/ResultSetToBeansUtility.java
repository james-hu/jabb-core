/**
 * 
 */
package net.sf.jabb.util.db;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sf.cglib.beans.BeanGenerator;

import org.apache.commons.dbutils.BeanProcessor;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.text.WordUtils;

/**
 * An utility to convert ResultSet to dynamically generated beans. 
 * When mapping column labels/names to property names, by default, 
 * non-ascii characters will be removed and only first letter of the words will be capitalized except 
 * for the first letter of the property names which will be in lower case. 
 * For example, THIS_IS_1ST_COLUMN_$$ will become thisIs1stColumn.
 * Values of the properties are got from ResultSet.getObject() method.
 * 
 * @author james.hu
 *
 */
public class ResultSetToBeansUtility {
	protected Map<String, String> columnToPropertyOverrides;
	protected Map<ResultSetMetaData, Class<?>> beanClasses = new HashMap<ResultSetMetaData, Class<?>>();

	/**
	 * Constructor allows overriding of column to property name mapping 
	 * @param columnToPropertyOverrides  the keys are column names/labels, and the values are property names
	 */
	public ResultSetToBeansUtility(Map<String, String> columnToPropertyOverrides) {
		if (columnToPropertyOverrides == null) {
			throw new IllegalArgumentException(
					"columnToPropertyOverrides map cannot be null");
		}
		this.columnToPropertyOverrides = columnToPropertyOverrides;  // save it for later usage by buildBeanClass()
	}

	/**
	 * Default constructor
	 */
	public ResultSetToBeansUtility() {
		this(new HashMap<String, String>());
	}
	
	/**
	 * Convert current row of the ResultSet to a bean of a dynamically generated class.
	 * @param rs the ResultSet
	 * @return a bean of a dynamically generated class
	 * @throws SQLException
	 */
	public Object convert(ResultSet rs) throws SQLException{
		Map<String, String> columnToPropertyMappings = new HashMap<String, String>(columnToPropertyOverrides); // it will be modified reuseOrBuildBeanClass()
		Class<?> beanClass = reuseOrBuildBeanClass(rs.getMetaData(), columnToPropertyMappings);
		BeanProcessor beanProcessor = new BeanProcessor(columnToPropertyMappings);
		return beanProcessor.toBean(rs, beanClass);
	}
	
	/**
	 * Convert all rows of the ResultSet to a list of beans of a dynamically generated class.
	 * @param rs the ResultSet
	 * @return a list of beans
	 * @throws SQLException
	 */
	public List<?> convertAll(ResultSet rs) throws SQLException{
		Map<String, String> columnToPropertyMappings = new HashMap<String, String>(columnToPropertyOverrides);  // it will be modified in reuseOrBuildBeanClass()
		Class<?> beanClass = reuseOrBuildBeanClass(rs.getMetaData(), columnToPropertyMappings);
		BeanProcessor beanProcessor = new BeanProcessor(columnToPropertyMappings);
		return beanProcessor.toBeanList(rs, beanClass);
	}

	/**
	 * 
	 * @param rsmd
	 * @param columnToPropertyMappings	it may be modified inside this method
	 * @return
	 * @throws SQLException
	 */
	protected Class<?> reuseOrBuildBeanClass(ResultSetMetaData rsmd, Map<String, String> columnToPropertyMappings) throws SQLException {
		Class<?> result = beanClasses.get(rsmd);
		if (result == null){
			synchronized(beanClasses){
				result = beanClasses.get(rsmd);
				if (result == null){
					result = buildBeanClass(rsmd, columnToPropertyMappings);
					beanClasses.put(rsmd, result);
				}
			}
		}
		return result;
	}

	/**
	 * 
	 * @param rsmd
	 * @param columnToPropertyMappings	It will be modified inside this method to append missing mappings
	 * @return
	 * @throws SQLException
	 */
	protected Class<?> buildBeanClass(final ResultSetMetaData rsmd, Map<String, String> columnToPropertyMappings) throws SQLException {
		BeanGenerator bg = new BeanGenerator();
		
		int cols = rsmd.getColumnCount();
		for (int col = 1; col <= cols; col++) {
			// column name
			String columnName = rsmd.getColumnLabel(col);
			if (null == columnName || 0 == columnName.length()) {
				columnName = rsmd.getColumnName(col);
			}
			
			// property name
			String propertyName = null;
			propertyName = columnToPropertyMappings.get(columnName);
			
			if (propertyName == null){
				propertyName = columnNameToPropertyName(columnName);
				columnToPropertyMappings.put(columnName, propertyName);
			}
			
			// property type
			Class<?> propertyClass = Object.class; //null;
			/*
			switch (rsmd.getColumnType(col)){
				case Types.DATE:
				case Types.TIME:
				case Types.TIMESTAMP:
					propertyClass = java.util.Date.class;
					break;
				case Types.BIGINT:
					propertyClass = Long.class;
					break;
				case Types.INTEGER:
				case Types.TINYINT:
				case Types.SMALLINT:
					propertyClass = Integer.class;
					break;
				case Types.REAL:
				case Types.FLOAT:
					propertyClass = Float.class;
					break;
				case Types.DOUBLE:
					propertyClass = Double.class;
					break;
				case Types.DECIMAL:
				case Types.NUMERIC:
					propertyClass = BigDecimal.class;
					break;
				case Types.CHAR:
				case Types.VARCHAR:
				case Types.LONGNVARCHAR:
				case Types.CLOB:
					propertyClass = String.class;
					break;
				case Types.BIT:
				case Types.BOOLEAN:
					propertyClass = Boolean.class;
					break;
				default:
					propertyClass = Object.class;
			}
			*/
			bg.addProperty(propertyName, propertyClass);
		}
		
		Class<?> claz = (Class<?>) bg.createClass();
		return claz;
	}
	
	/**
	 * Convert column name to property name, for example, THIS_IS_1ST_COLUMN_$$ will become thisIs1stColumn
	 * @param columnName
	 * @return
	 */
	protected String columnNameToPropertyName(String columnName){
		String normalized = columnName.replaceAll("[^a-zA-Z0-9]+", " ");
		String capitalized = WordUtils.capitalizeFully(normalized);
		String blankRemmoved = StringUtils.remove(capitalized, ' ');
		return StringUtils.uncapitalize(blankRemmoved);
	}

}
