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
	protected String factTemplateName;
	
	/**
	 * If we already have the FactTemplate
	 * @param factTemplate
	 * @param dataMap
	 */
	public MapBasedFact(FactTemplate factTemplate, Map<String, Object> dataMap){
		this(dataMap);
		if (factTemplate != null){
			this.factTemplate = factTemplate;
			this.factTemplateName = factTemplate.getName();
		}
	}

	/**
	 * If we only have the name of FactTemplate
	 * @param factTemplateName
	 * @param dataMap
	 */
	public MapBasedFact(String factTemplateName, Map<String, Object> dataMap){
		this(dataMap);
		this.factTemplateName = factTemplateName;
	}
	
	protected MapBasedFact(Map<String, Object> dataMap){
		this.map = dataMap;
		this.factId = globalFactId.addAndGet(1);
		if (this.map == null){
			this.map = new HashMap<String, Object>();
		}
	}
	
	/**
	 * Associate this fact with a package which has a FactTemplate defined with the same FactTemplate name
	 * @param pkg
	 */
	public void associateWithPackage(org.drools.core.rule.Package pkg){
		factTemplate = pkg.getFactTemplate(factTemplateName);
	}

	/**
	 * Associate this fact with a FactTemplate
	 * @param factTemplate
	 */
	public void associateWithFactTemplate(FactTemplate factTemplate){
		this.factTemplate = factTemplate;
		this.factTemplateName = factTemplate.getName();
	}

	@Override
	public long getFactId() {
		return factId;
	}

	@Override
	public FactTemplate getFactTemplate() {
		return factTemplate;
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
