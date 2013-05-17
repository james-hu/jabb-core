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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import net.sf.jabb.stdr.StdrUtil;

/**
 * @author James (Zhengmao HU)
 * 
 */
public class IncludeTag extends org.apache.struts2.views.jsp.IncludeTag {
	private static final long serialVersionUID = -7159869592197448964L;
	protected static final Log log = LogFactory.getLog(IncludeTag.class);
	
	public void setParamName(String name){
		Map<String, Object> templateParameterMap = StdrUtil.getParameters(pageContext.getRequest());
		if (templateParameterMap != null){
			String url = (String)templateParameterMap.get(name);
			if (url != null){
				String prefix = (String)templateParameterMap.get(StdrUtil.URL_PREFIX_PARAMETER);
				if (prefix != null){
					url = prefix + url;
				}
				String postfix = (String)templateParameterMap.get(StdrUtil.URL_POSTFIX_PARAMETER);
				if (postfix != null){
					url = url + postfix;
				}
				this.setValue(url);
			}else{
				log.error("Can't find value for parameter '" 
						+ name
						+ "' in configuration.");
			}
		}else{
			log.error("Can't find parameter map from attribute '" 
					+ StdrUtil.TEMPLATE_PARAMETER_MAP
					+ "' in request context.");
		}
	}

}
