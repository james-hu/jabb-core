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
package net.sf.jabb.stdr.dispatcher;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.servlet.http.HttpServletRequest;

import net.sf.jabb.stdr.StdrUtil;
import ognl.ObjectPropertyAccessor;
import ognl.OgnlException;
import ognl.OgnlRuntime;

import org.apache.struts2.ServletActionContext;
import org.apache.struts2.dispatcher.ServletDispatcherResult;

import com.opensymphony.xwork2.ActionInvocation;
import com.opensymphony.xwork2.config.entities.ResultConfig;

/**
 * The dispatcher to replace the default dispatcher of Struts2.
 * It is compatible with the default one since it is a subclass
 * of ServletDispatcherResult.
 *
 * <b>Example:</b>
 *
 * <pre>
 * &lt;result name="success" type="dispatcher"&gt;
 *   &lt;param name="location"&gt;foo.jsp&lt;/param&gt;
 * &lt;/result&gt;
 * <!-- END SNIPPET: example --></pre>
 * 
 * @author James (Zhengmao HU)
 *
 */
public class TemplateDispatcherResult extends ServletDispatcherResult {
	private static final long serialVersionUID = -7019301335625791711L;

	/**
	 * These parameter names are pre-defined, they are not defined by user. 
	 * They are: location, namespace, encode, parse.
	 */
    protected List<String> predefinedResultParam = Arrays.asList(new String[] {
            DEFAULT_PARAM, "namespace", "encode", "parse", "location"});
    
    static{
    	OgnlRuntime.setPropertyAccessor(TemplateDispatcherResult.class, 
    			new TemplateDispatcherResult.TemplatePropertyAccessor());
    }
    
	public TemplateDispatcherResult() {
		super();
	}

	public TemplateDispatcherResult(String location) {
		super(location);
	}
	
	public void doExecute(String finalLocation, ActionInvocation invocation) throws Exception {
		HttpServletRequest request = ServletActionContext.getRequest();
		
        // the object to pass parameters
        Map<String, Object> templateParameterMap = new HashMap<String, Object>();

        String resultCode = invocation.getResultCode();
        if (resultCode != null) {
            ResultConfig resultConfig = invocation.getProxy().getConfig().getResults().get(
                    resultCode);
            Map<String, String> resultConfigParams = resultConfig.getParams();
            for (Iterator<Entry<String, String>> i = resultConfigParams.entrySet().iterator(); i.hasNext(); ) {
                Map.Entry<String, String> e = i.next();
                if (! predefinedResultParam.contains(e.getKey())) {
                	templateParameterMap.put(e.getKey().toString(),
                            e.getValue() == null ? "":
                                conditionalParse(e.getValue().toString(), invocation));
                }
            }
        }
        
        StdrUtil.getParameters(request).putAll(templateParameterMap);
		super.doExecute(finalLocation, invocation);
	}

	/**
	 * This is the class used to avoid ognl.NoSuchPropertyException when
	 * the framework tries to set parameters by ognl.
	 * @author James (Zhengmao HU)
	 *
	 */
	static public class TemplatePropertyAccessor extends ObjectPropertyAccessor {

		/**
		 * @see ognl.PropertyAccessor#getProperty(java.util.Map, java.lang.Object, java.lang.Object)
		 */
		@SuppressWarnings("rawtypes")
		@Override
		public Object getProperty(Map context, Object target, Object oname)
				throws OgnlException {
			return super.getProperty(context, target, oname);
		}

		/**
		 * @see ognl.PropertyAccessor#setProperty(java.util.Map, java.lang.Object, java.lang.Object, java.lang.Object)
		 */
		@SuppressWarnings("rawtypes")
		@Override
		public void setProperty(Map context, Object target, Object oname, Object value)
				throws OgnlException {
			try {
				super.setProperty(context, target, oname, value);
			}catch(OgnlException oe){
				// ignore it
			}
		}
	}
	
}
