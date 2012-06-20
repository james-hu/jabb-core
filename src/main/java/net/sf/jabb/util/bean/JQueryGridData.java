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

import java.util.LinkedList;
import java.util.List;

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
	}
	
	public void addRow(Row row){
		rows.add(row);
	}
	
	public void addRow(Object row){
		rows.add(row);
	}
	
	public void addRow(Object id, Object cell){
		rows.add(new Row(id, cell));
	}
	
	public void addRows(List<?> rows) {
		this.rows.addAll(rows);
	}
	
	public void clearRows(){
		rows.clear();
	}
	
	public int getTotal() {
		return total;
	}
	public void setTotal(int total) {
		this.total = total;
	}
	public int getPage() {
		return page;
	}
	public void setPage(int page) {
		this.page = page;
	}
	public int getRecords() {
		return records;
	}
	public void setRecords(int records) {
		this.records = records;
	}
	public List<Object> getRows() {
		return rows;
	}
}
