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


import net.sf.jabb.stdr.StdrUtil;

/**
 * @author James (Zhengmao HU)
 *
 */
public class SetTag extends org.apache.struts2.views.jsp.SetTag {

	private static final long serialVersionUID = 4812047788803863101L;

	public void setParamName(String paramName){
		this.setValue("#request." + 
				StdrUtil.TEMPLATE_PARAMETER_MAP
				+ "['" + paramName + "']");
	}


}
