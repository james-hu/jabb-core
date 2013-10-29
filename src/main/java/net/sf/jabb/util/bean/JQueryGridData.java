/*
Copyright 2012 James Hu

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
package net.sf.jabb.util.bean;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * JSON data required by jqGrid.
 * @author James Hu
 *
 */
public class JQueryGridData {
	public class Row{
		protected Object id;
		protected Object cell;
		
		public Row(Object id, Object cell){
			this.id = id;
			this.cell = cell;
		}
		
		public Object getId() {
			return id;
		}
		public void setId(Object id) {
			this.id = id;
		}
		public Object getCell() {
			return cell;
		}
		public void setCell(Object cell) {
			this.cell = cell;
		}
	}
	protected int total;
	protected int page;
	protected int records;
	protected List<Object> rows;
	
	public JQueryGridData(){
		rows = new LinkedList<Object>();
		total = 1;
		records = 0;
		page = 1;
	}
	
	public void addRow(Object row){
		rows.add(row);
	}
	
	public void addRow(Object id, Object cell){
		rows.add(new Row(id, cell));
	}
	
	public void addRows(Collection<?> rows) {
		for (Object row: rows){
			addRow(row);
		}
	}
	
	/**
	 * Add rows from a JDBC ResultSet. Each row will be a HashMap mapped from a record of ResultSet.
	 * @param rs
	 * @throws SQLException 
	 */
	public void addRows(ResultSet rs) throws SQLException{
		ResultSetMetaData rsmd = rs.getMetaData();
		int columnCount = rsmd.getColumnCount();
		
		while (rs.next()) {
			Map<String, Object> row = new HashMap<String, Object>();
			for (int i = 1; i < columnCount + 1; i++) {
				String colName = rsmd.getColumnName(i);
				Object colObj = rs.getObject(i);
				if (colObj == null){		// avoid error if trying to convert data types
					row.put(colName, colObj);
				}else{
					if (colObj instanceof oracle.sql.Datum){
						colObj = ((oracle.sql.TIMESTAMP)colObj).toJdbc();
					}
					
					if (colObj instanceof java.sql.Timestamp){
						row.put(colName, new Date(((java.sql.Timestamp)colObj).getTime()));
					}else if (colObj instanceof java.sql.Date){
						row.put(colName, new Date(((java.sql.Date)colObj).getTime()));
					}else{
						row.put(colName, colObj);
					}
				}
			}
			addRow(row);
		}
	}
	
	public void clearRows(){
		rows.clear();
	}
	
	public int getTotal() {
		return total;
	}
	/**
	 * Set total number of pages
	 * @param total
	 */
	public void setTotal(int total) {
		this.total = total;
	}
	public int getPage() {
		return page;
	}
	/**
	 * Set current page number
	 * @param page
	 */
	public void setPage(int page) {
		this.page = page;
	}
	public int getRecords() {
		return records;
	}
	/**
	 * Set total number of records
	 * @param records
	 */
	public void setRecords(int records) {
		this.records = records;
	}
	/**
	 * Set number of records according to current size of the rows list.
	 * @param records
	 */
	public void setRecords() {
		this.records = rows.size();
	}
	
	public List<Object> getRows() {
		return rows;
	}
}
