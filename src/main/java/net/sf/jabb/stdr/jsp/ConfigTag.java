/*
   Copyright 2009, 2011, 2013 James (Zhengmao HU)

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
package net.sf.jabb.stdr.jsp;

import java.util.Map;

import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.tagext.BodyContent;
import javax.servlet.jsp.tagext.BodyTagSupport;

import net.sf.jabb.stdr.StdrUtil;

/**
 * @author James (Zhengmao HU)
 *
 */
public class ConfigTag extends BodyTagSupport {
	private static final long serialVersionUID = -3586508779295000782L;

	protected String paramName;
	protected String value;
	protected String valueFrom;

	public void setParamName(String name){
		this.paramName = name;
	}
	
	public void setValue(String v){
		this.value = v;
	}
	
	public void setValueFrom(String vf){
		this.valueFrom = vf;
	}
	
	public int doAfterBody() throws JspTagException{
		BodyContent body = getBodyContent();
		String v = body.getString();
		if (v != null){
			this.value = v;
		}
		return SKIP_BODY;
	}
	
	public int doEndTag() throws JspTagException{
		Map<String, Object> templateParameterMap = StdrUtil.getParameters(pageContext.getRequest());
		if (valueFrom == null){
			templateParameterMap.put(paramName, value);
		}else{
			templateParameterMap.put(paramName,
					templateParameterMap.get(valueFrom));
		}
		return EVAL_PAGE;
	}

}
