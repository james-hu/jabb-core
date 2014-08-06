/**
 * 
 */
package net.sf.jabb.util.db;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import net.sf.cglib.beans.BeanGenerator;

import org.apache.commons.dbutils.BeanProcessor;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.text.WordUtils;

/**
 * An utility to convert ResultSet to something else, such like dynamically generated beans. 
 * When mapping column labels/names to property names, by default, 
 * non-ascii characters will be removed and only first letter of the words will be capitalized except 
 * for the first letter of the property names which will be in lower case. 
 * For example, THIS_IS_1ST_COLUMN_$$ will become thisIs1stColumn.
 * Values of the properties are got from ResultSet.getObject() method.
 * 
 * @author james.hu
 *
 */
public class ResultSetUtility {
	protected Map<String, String> columnToPropertyOverrides = new HashMap<String, String>();
	// TODO: check only the columns and properties rather than the whole ResultSetMetaData
	protected Map<ResultSetMetaData, Class<?>> beanClasses = new HashMap<ResultSetMetaData, Class<?>>();

	/**
	 * Constructor allows overriding of column to property name mapping 
	 * @param columnToPropertyOverrides  the keys are column names/labels, and the values are property names
	 */
	public ResultSetUtility(Map<String, String> columnToPropertyOverrides) {
		this();
		if (columnToPropertyOverrides != null) {
			this.columnToPropertyOverrides.putAll(columnToPropertyOverrides);  // save it for later usage by buildBeanClass()
		}
	}

	/**
	 * Default constructor
	 */
	public ResultSetUtility() {
	}
	
	/**
	 * Convert current row of the ResultSet to a Map. The keys of the Map are property names transformed from column names.
	 * @param rs
	 * @return
	 * @throws SQLException
	 */
	public Map<String, Object> convertToMap(ResultSet rs) throws SQLException{
		return convertToMap(rs, null);
	}
	
	public Map<String, Object> convertToMap(ResultSet rs, Map<String, ColumnMetaData> alreadyDeterminedMappings) throws SQLException{
		ResultSetMetaData rsmd = rs.getMetaData();
		Map<String, ColumnMetaData> columnToPropertyMappings = alreadyDeterminedMappings;
		if (columnToPropertyMappings == null){
			columnToPropertyMappings = createColumnToPropertyMappings(rsmd);
		}
		
		Map<String, Object> map = new HashMap<String, Object>();
		int cols = rsmd.getColumnCount();
		for (int col = 1; col <= cols; col++) {
			// column name
			String columnName = columnLabelOrName(rsmd, col);
			
			// property name
			String propertyName = columnToPropertyMappings.get(columnName).getPropertyName();		// not possible to get null from this

			map.put(propertyName, rs.getObject(col));
		}		
		return map;
	}

	
	/**
	 * Convert all rows of the ResultSet to a Map. The keys of the Map are property names transformed from column names.
	 * @param rs
	 * @return
	 * @throws SQLException
	 */
	public List<Map<String, Object>> convertAllToMaps(ResultSet rs) throws SQLException{
		return convertAllToMaps(rs, null);
	}
	
	/**
	 * Convert all rows of the ResultSet to a Map. The keys of the Map are property names transformed from column names.
	 * @param rs
	 * @param alreadyDeterminedMappings
	 * @return
	 * @throws SQLException
	 */
	public List<Map<String, Object>> convertAllToMaps(ResultSet rs, Map<String, ColumnMetaData> alreadyDeterminedMappings) throws SQLException{
		ResultSetMetaData rsmd = rs.getMetaData();
		Map<String, ColumnMetaData> columnToPropertyMappings = alreadyDeterminedMappings;
		if (columnToPropertyMappings == null){
			columnToPropertyMappings = createColumnToPropertyMappings(rsmd);
		}
		
		List<Map<String, Object>> list = new LinkedList<Map<String, Object>>();
		while(rs.next()){
			Map<String, Object> map = convertToMap(rs, columnToPropertyMappings);
			list.add(map);
		}
		
		return list;
	}
	
	/**
	 * Convert current row of the ResultSet to a bean of a dynamically generated class. It requires CGLIB.
	 * @param rs the ResultSet
	 * @return a bean of a dynamically generated class
	 * @throws SQLException
	 */
	public Object convertToDynamicBean(ResultSet rs) throws SQLException{
		ResultSetMetaData rsmd = rs.getMetaData();
		
		Map<String, ColumnMetaData> columnToPropertyMappings = createColumnToPropertyMappings(rsmd);
		
		Class<?> beanClass = reuseOrBuildBeanClass(rsmd, columnToPropertyMappings);
		BeanProcessor beanProcessor = new BeanProcessor(simpleColumnToPropertyMappings(columnToPropertyMappings));
		return beanProcessor.toBean(rs, beanClass);
	}
	
	/**
	 * Convert all rows of the ResultSet to a list of beans of a dynamically generated class. It requires CGLIB.
	 * @param rs the ResultSet
	 * @return a list of beans
	 * @throws SQLException
	 */
	public List<?> convertAllToDynamicBeans(ResultSet rs) throws SQLException{
		ResultSetMetaData rsmd = rs.getMetaData();
		
		Map<String, ColumnMetaData> columnToPropertyMappings = createColumnToPropertyMappings(rsmd);
		
		Class<?> beanClass = reuseOrBuildBeanClass(rsmd, columnToPropertyMappings);
		BeanProcessor beanProcessor = new BeanProcessor(simpleColumnToPropertyMappings(columnToPropertyMappings));
		return beanProcessor.toBeanList(rs, beanClass);
	}
	
	/**
	 * Convert Map&lt;String, ColumnMetaData&gt; to Map&lt;String, String&gt; with only the propertyName in value
	 * @param cm
	 * @return
	 */
	private Map<String, String> simpleColumnToPropertyMappings(Map<String, ColumnMetaData> cm){
		Map<String, String> result = new HashMap<String, String>(cm.size());
		for (Entry<String, ColumnMetaData> entry: cm.entrySet()){
			result.put(entry.getKey(), entry.getValue().getPropertyName());
		}
		return result;
	}

	/**
	 * 
	 * @param rsmd
	 * @param columnToPropertyMappings	It must have already been populated for all the columns
	 * @return
	 * @throws SQLException
	 */
	protected Class<?> reuseOrBuildBeanClass(ResultSetMetaData rsmd, Map<String, ColumnMetaData> columnToPropertyMappings) throws SQLException {
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
	 * @param columnToPropertyMappings	It must have already been populated for all the columns
	 * @return
	 * @throws SQLException
	 */
	protected Class<?> buildBeanClass(final ResultSetMetaData rsmd, Map<String, ColumnMetaData> columnToPropertyMappings) throws SQLException {
		BeanGenerator bg = new BeanGenerator();
		
		int cols = rsmd.getColumnCount();
		for (int col = 1; col <= cols; col++) {
			// column name
			String columnName = columnLabelOrName(rsmd, col);
			
			// property name
			String propertyName = columnToPropertyMappings.get(columnName).getPropertyName();		// not possible to get null from this
			if (propertyName != null){
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
		}
	
		Class<?> claz = (Class<?>) bg.createClass();
		return claz;
	}
	
	/**
	 * To determine the final column to property mappings.
	 * @param rsmd
	 * @return the mapping that already applied overridden
	 * @throws SQLException 
	 */
	public Map<String, ColumnMetaData> createColumnToPropertyMappings(final ResultSetMetaData rsmd) throws SQLException{
		Map<String, ColumnMetaData> columnToPropertyMappings = ColumnMetaData.createMapByLabelOrName(rsmd);
		for (ColumnMetaData cm: columnToPropertyMappings.values()) {
			// property name
			String propertyName = null;
			propertyName = columnToPropertyOverrides.get(cm.getLabelOrName());
			
			if (propertyName == null){
				propertyName = columnNameToPropertyName(cm.getLabelOrName());
			}
			
			cm.setPropertyName(propertyName);
		}
		return columnToPropertyMappings;
	}
	
	/**
	 * Convert column name to property name, for example, THIS_IS_1ST_COLUMN_$$ will become thisIs1stColumn
	 * @param columnName  label or name of the column
	 * @return  for example, THIS_IS_1ST_COLUMN_$$ will become thisIs1stColumn
	 */
	protected String columnNameToPropertyName(String columnName){
		String normalized = columnName.replaceAll("[^a-zA-Z0-9]+", " ");
		String capitalized = WordUtils.capitalizeFully(normalized);
		String blankRemmoved = StringUtils.remove(capitalized, ' ');
		return StringUtils.uncapitalize(blankRemmoved);
	}
	
	/**
	 * Convert column name to property name for a column, for example, THIS_IS_1ST_COLUMN_$$ will become thisIs1stColumn
	 * @param rsmd	the metadata
	 * @param col	the column number
	 * @return  for example, THIS_IS_1ST_COLUMN_$$ will become thisIs1stColumn
	 * @throws SQLException
	 */
	protected String columnToPropertyName(ResultSetMetaData rsmd, int col) throws SQLException{
		String columnName = rsmd.getColumnLabel(col);
		if (null == columnName || 0 == columnName.length()) {
			columnName = rsmd.getColumnName(col);
		}
		return columnNameToPropertyName(columnName);
	}
	
	/**
	 * Get the label or name of a column
	 * @param rsmd
	 * @param col
	 * @return
	 * @throws SQLException
	 */
	public String columnLabelOrName(ResultSetMetaData rsmd, int col) throws SQLException{
		String columnName = rsmd.getColumnLabel(col);
		if (null == columnName || 0 == columnName.length()) {
			columnName = rsmd.getColumnName(col);
		}
		return columnName;
	}

}
