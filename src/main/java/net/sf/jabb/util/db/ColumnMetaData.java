/**
 * 
 */
package net.sf.jabb.util.db;

import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * This class encapsulates all the column related metadata you can get from ResultSetMetaData, 
 * and in additional, some fields are added such as index, labelOrName propertyName.
 * 
 * @author james.hu
 *
 */
public class ColumnMetaData {
	private String catalogName;
	private String className;
	private int displaySize;
	private String label;
	private String name;
	private int type;
	private String typeName;
	private int precision;
	private int scale;
	private String schemaName;
	private String tableName;
	private boolean autoIncrement;
	private boolean caseSensitive;
	private boolean definitelyWritable;
	private int nullable;
	private boolean readOnly;
	private boolean searchable;
	private boolean signed;
	private boolean writable;
	
	private int index;
	private String labelOrName;
	private String propertyName;
	
	/**
	 * Construct an instance with result set metadata and column index
	 * @param m result set metadata
	 * @param column index. the first column has the index of 1
	 * @throws SQLException
	 */
	public ColumnMetaData(ResultSetMetaData m, int column) throws SQLException{
		this.catalogName = m.getCatalogName(column);
		this.className = m.getColumnClassName(column);
		this.displaySize = m.getColumnDisplaySize(column);
		this.label = m.getColumnLabel(column);
		this.name = m.getColumnName(column);
		this.type = m.getColumnType(column);
		this.typeName = m.getColumnTypeName(column);
		this.precision = m.getPrecision(column);
		this.scale = m.getScale(column);
		this.schemaName = m.getSchemaName(column);
		this.tableName = m.getTableName(column);
		this.autoIncrement = m.isAutoIncrement(column);
		this.caseSensitive = m.isCaseSensitive(column);
		this.definitelyWritable = m.isDefinitelyWritable(column);
		this.nullable = m.isNullable(column);
		this.readOnly = m.isReadOnly(column);
		this.searchable = m.isSearchable(column);
		this.signed = m.isSigned(column);
		this.writable = m.isWritable(column);
		
		this.index = column;
		this.labelOrName = this.label;
		if (this.labelOrName == null || this.labelOrName.length() == 0){
			this.labelOrName = this.name;
		}
	}
	
	/**
	 * Get all the column meta data and put them into a map keyed by column label or name
	 * @param m the result set meta data
	 * @return
	 * @throws SQLException
	 */
	static public Map<String, ColumnMetaData> createMapByLabelOrName(ResultSetMetaData m) throws SQLException{
		Map<String, ColumnMetaData> result = new HashMap<String, ColumnMetaData>();
		for (int i = 1; i <= m.getColumnCount(); i ++){
			ColumnMetaData cm = new ColumnMetaData(m, i);
			result.put(cm.getLabelOrName(), cm);
		}
		return result;
	}
	
	/**
	 * Get all the column meta data and put them into a list in their default order
	 * @param m the result set meta data
	 * @return
	 * @throws SQLException
	 */
	static public List<ColumnMetaData> createList(ResultSetMetaData m) throws SQLException{
		List<ColumnMetaData> result = new ArrayList<ColumnMetaData>(m.getColumnCount());
		for (int i = 1; i <= m.getColumnCount(); i ++){
			ColumnMetaData cm = new ColumnMetaData(m, i);
			result.add(cm);
		}
		return result;
	}
	

	public String getCatalogName() {
		return catalogName;
	}

	public void setCatalogName(String catalogName) {
		this.catalogName = catalogName;
	}

	public String getClassName() {
		return className;
	}

	public void setClassName(String className) {
		this.className = className;
	}

	public int getDisplaySize() {
		return displaySize;
	}

	public void setDisplaySize(int displaySize) {
		this.displaySize = displaySize;
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public String getTypeName() {
		return typeName;
	}

	public void setTypeName(String typeName) {
		this.typeName = typeName;
	}

	public int getPrecision() {
		return precision;
	}

	public void setPrecision(int precision) {
		this.precision = precision;
	}

	public int getScale() {
		return scale;
	}

	public void setScale(int scale) {
		this.scale = scale;
	}

	public String getSchemaName() {
		return schemaName;
	}

	public void setSchemaName(String schemaName) {
		this.schemaName = schemaName;
	}

	public String getTableName() {
		return tableName;
	}

	public void setTableName(String tableName) {
		this.tableName = tableName;
	}

	public boolean isAutoIncrement() {
		return autoIncrement;
	}

	public void setAutoIncrement(boolean autoIncrement) {
		this.autoIncrement = autoIncrement;
	}

	public boolean isCaseSensitive() {
		return caseSensitive;
	}

	public void setCaseSensitive(boolean caseSensitive) {
		this.caseSensitive = caseSensitive;
	}

	public boolean isDefinitelyWritable() {
		return definitelyWritable;
	}

	public void setDefinitelyWritable(boolean definitelyWritable) {
		this.definitelyWritable = definitelyWritable;
	}

	public int getNullable() {
		return nullable;
	}

	public void setNullable(int nullable) {
		this.nullable = nullable;
	}

	public boolean isReadOnly() {
		return readOnly;
	}

	public void setReadOnly(boolean readOnly) {
		this.readOnly = readOnly;
	}

	public boolean isSearchable() {
		return searchable;
	}

	public void setSearchable(boolean searchable) {
		this.searchable = searchable;
	}

	public boolean isSigned() {
		return signed;
	}

	public void setSigned(boolean signed) {
		this.signed = signed;
	}

	public boolean isWritable() {
		return writable;
	}

	public void setWritable(boolean writable) {
		this.writable = writable;
	}



	public int getIndex() {
		return index;
	}



	public void setIndex(int index) {
		this.index = index;
	}



	public String getPropertyName() {
		return propertyName;
	}



	public void setPropertyName(String propertyName) {
		this.propertyName = propertyName;
	}

	public String getLabelOrName() {
		return labelOrName;
	}

	public void setLabelOrName(String labelOrName) {
		this.labelOrName = labelOrName;
	}
}
