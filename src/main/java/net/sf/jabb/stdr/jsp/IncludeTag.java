/*
   Copyright 2009, 2011 James (Zhengmao HU)

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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import net.sf.jabb.stdr.dispatcher.TemplateDispatcherResult;

/**
 * @author James (Zhengmao HU)
 * 
 */
public class IncludeTag extends org.apache.struts2.views.jsp.IncludeTag {
	private static final long serialVersionUID = -7159869592197448964L;
	protected static final Log log = LogFactory.getLog(IncludeTag.class);
	
	public void setParamName(String name){
		@SuppressWarnings("unchecked")
		Map<String, String> templateParameterMap = (Map<String, String>)
			pageContext.getRequest().getAttribute(
				TemplateDispatcherResult.TEMPLATE_PARAMETER_MAP);
		if (templateParameterMap != null){
			String url = templateParameterMap.get(name);
			if (url != null){
				this.setValue(url);
			}else{
				log.error("Can't find value for parameter '" 
						+ name
						+ "' in configuration.");
			}
		}else{
			log.error("Can't find parameter map from attribute '" 
					+ TemplateDispatcherResult.TEMPLATE_PARAMETER_MAP
					+ "' in request context.");
		}
	}

}
