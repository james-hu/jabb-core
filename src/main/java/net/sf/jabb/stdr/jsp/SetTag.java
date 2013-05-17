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


import java.util.Enumeration;

import javax.servlet.ServletContext;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import net.sf.jabb.stdr.StdrUtil;

/**
 * @author James (Zhengmao HU)
 *
 */
public class SetTag extends org.apache.struts2.views.jsp.SetTag {

	private static final long serialVersionUID = 4812047788803863101L;
	
	private static final Log log = LogFactory.getLog(SetTag.class);

	public void setParamName(String paramName){
		this.setValue("#request." + 
				StdrUtil.TEMPLATE_PARAMETER_MAP
				+ "['" + paramName + "']");
	}

	public void setType(String typeName){
		if (StdrUtil.TYPE_CURRENT_MENU_ITEM.equals(typeName)){
			setParamName(StdrUtil.CURRENT_MENU_ITEM_PARAMETER);
		}else if (StdrUtil.TYPE_WEB_APP_CONFIG.equals(typeName)){
			setParamName(StdrUtil.WEB_APP_CONFIG_PARAMETER);
		}else if (typeName != null && typeName.startsWith(StdrUtil.TYPE_SPRING_BEAN_PREFIX)){
			String beanName = typeName.substring(StdrUtil.TYPE_SPRING_BEAN_PREFIX.length()).trim();
			Object bean = findSpringBean(beanName);
			if (bean != null){
				String attrName = StdrUtil.PAGE_ATTR_NAME_PREFIX_FOR_SPRING_BEAN + beanName;
				this.pageContext.setAttribute(attrName, bean);
				this.setValue("#attr['" + attrName + "']");
			}else{
				this.setValue("");
				log.error("Spring bean '" + beanName + "' cannot be found.");
			}
		}else{
			this.setValue("");
			log.error("Type name '" + typeName + "' is not valid.");
		}
	}

	/**
	 * Find the Spring bean from WebApplicationContext in servlet context.
	 * @param beanName
	 * @return	the bean or null
	 */
	private Object findSpringBean(String beanName) {
		ServletContext servletContext = this.pageContext.getServletContext();

		Enumeration<String> attrNames = servletContext.getAttributeNames();
		while (attrNames.hasMoreElements()){
			String attrName = attrNames.nextElement();
			if (attrName.startsWith("org.springframework")){
				try{
					WebApplicationContext springContext = WebApplicationContextUtils.getWebApplicationContext(servletContext, attrName);
					Object bean = springContext.getBean(beanName);
					return bean;
				}catch(Exception e){
					// continue if the bean cannot be found.
				}
			}
		}
		return null;
	}

}
