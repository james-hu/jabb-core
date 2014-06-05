/**
 * 
 */
package net.sf.jabb.drools;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

import org.drools.core.facttemplates.Fact;
import org.drools.core.facttemplates.FactTemplate;
import org.drools.core.facttemplates.FieldTemplate;


/**
 * Fact template based on a map.
 * @author james.hu
 *
 */
public class MapBasedFact implements Fact {
	private static AtomicLong globalFactId = new AtomicLong();
	
	protected Map<String, Object> map;
	protected FactTemplate factTemplate;
	protected long factId;
	
	public MapBasedFact(FactTemplate factTemplate, Map<String, Object> dataMap){
		this.factTemplate = factTemplate;
		this.map = dataMap;
		this.factId = globalFactId.addAndGet(1);
		if (this.map == null){
			this.map = new HashMap<String, Object>();
		}
	}

	@Override
	public long getFactId() {
		return factId;
	}

	@Override
	public FactTemplate getFactTemplate() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object getFieldValue(int index) {
		FieldTemplate field = factTemplate.getFieldTemplate(index);
		return getFieldValue(field.getName());
	}

	@Override
	public Object getFieldValue(String name) {
		return map.get(name);
	}

	@Override
	public void setFieldValue(String name, Object value) {
		map.put(name, value);
	}

	@Override
	public void setFieldValue(int index, Object value) {
		FieldTemplate field = factTemplate.getFieldTemplate(index);
		setFieldValue(field.getName(), value);
	}

	
	@Override
	public String toString(){
		return map.toString();
	}
}
