/**
 * 
 */
package net.sf.jabb.stdr.view;

import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;

import javax.servlet.http.HttpServletRequest;

import net.sf.jabb.stdr.StdrUtil;

import org.apache.commons.lang3.text.StrMatcher;
import org.apache.commons.lang3.text.StrTokenizer;
import org.springframework.context.MessageSource;
import org.springframework.web.servlet.view.JstlView;

/**
 * An extended JstlView that understands template descriptor in the view name.
 * The view names with template descriptor look like: /WEB-INF/jsp/(template/normal/StandardTemplate(leftPanel=user/UserList,rightPanel=user/UserDetail)).jsp
 * <br> The above example should be interpreted as: 
 * <br> 1) /WEB-INF/jsp/template/normal/StandardTemplate.jsp is the template page
 * <br> 2) /WEB-INF/jsp/user/UserList.jsp is the value of the parameter named leftPanel
 * <br> 3) /WEB-INF/jsp/user/UserDetail.jsp is the value of the parameter named rightPanel
 * <br> Names and values of template parameters can be quoted if needed, for example, leftPanel='user files/UserList' or 'User Name'='   '
 * @author James Hu
 *
 */
public class TemplateJstlView extends JstlView {
	protected static final int LEFT_BRACKET = '(';
	protected static final int RIGHT_BRACKET = ')';
	protected static final int COMMA = ',';
	
	protected String originalViewName;

	public TemplateJstlView(){
		super();
	}
	
	public TemplateJstlView(String viewName){
		super(extractTemplateUrl(viewName));
		this.originalViewName = viewName;
	}
	
	public TemplateJstlView(String viewName, MessageSource messageSource){
		super(extractTemplateUrl(viewName), messageSource);
		this.originalViewName = viewName;
	}
	
	@Override
	public void setUrl(String viewName){
		super.setUrl(extractTemplateUrl(viewName));
		this.originalViewName = viewName;
	}

	@Override
	protected void exposeHelpers(HttpServletRequest request) throws Exception {
		Map<String, String> templateParameters = extractTemplateParameters(originalViewName);
		if (templateParameters != null){
			StdrUtil.setParameters(request, templateParameters);
		}
		super.exposeHelpers(request);
	}
	
	/**
	 * Parse the template descriptor to extract template URL
	 * @param viewName
	 * @return	URL of the template
	 */
	static protected String extractTemplateUrl(String viewName){
		int i1 = -1;
		int i2 = -1;
		int i3 = -1;
		int i4 = -1;
		i1 = viewName.indexOf(LEFT_BRACKET);
		if (i1 != -1){
			i2 = viewName.indexOf(LEFT_BRACKET, i1+1);
		}
		i4 = viewName.lastIndexOf(RIGHT_BRACKET);
		if (i4 != -1){
			i3 = viewName.lastIndexOf(RIGHT_BRACKET, i4-1);
		}
		
		if ((i1 == -1 || i4 == -1)		// no starting or ending 
				|| (i2*i3 < 0) 			// not matching
				|| (i2 > i3)){			// not matching
			return viewName;		// no valid template descriptor
		}
		
		//////// the format is guaranteed to be valid after this point
		
		String url = null;
		
		if (i2 != -1){
			url = viewName.substring(i1+1, i2);		// prefix{url{...}}postfix
		}else{
			url = viewName.substring(i1+1, i4);		// prefix{url}postfix
		}

		if (i1 > 0){
			String prefix = viewName.substring(0, i1);
			url = prefix + url;
		}
		if (i4 < viewName.length() - 1){
			String postfix = viewName.substring(i4 + 1);
			url = url + postfix;
		}
		
		return url;
	}
	
	/**
	 * Parse the template descriptor to extract template parameters
	 * @param viewName
	 * @return
	 */
	static protected Map<String, String> extractTemplateParameters(String viewName){
		int i1 = -1;
		int i2 = -1;
		int i3 = -1;
		int i4 = -1;
		i1 = viewName.indexOf(LEFT_BRACKET);
		if (i1 != -1){
			i2 = viewName.indexOf(LEFT_BRACKET, i1+1);
		}
		i4 = viewName.lastIndexOf(RIGHT_BRACKET);
		if (i4 != -1){
			i3 = viewName.lastIndexOf(RIGHT_BRACKET, i4-1);
		}
		
		if ((i1 == -1 || i4 == -1)		// no starting or ending 
				|| (i2 == -1 || i3 == -1) 			// not found
				|| (i2 > i3)){			// not matching
			return null;		// no valid template descriptor
		}
		
		//////// the format is guaranteed to be valid after this point
		
		Map<String, String> parameters = new HashMap<String, String>();

		if (i1 > 0){
			String prefix = viewName.substring(0, i1);
			parameters.put(StdrUtil.URL_PREFIX_PARAMETER, prefix);
		}
		if (i4 < viewName.length() - 1){
			String postfix = viewName.substring(i4 + 1);
			parameters.put(StdrUtil.URL_POSTFIX_PARAMETER, postfix);
		}
		
		StrTokenizer tokenizer = new StrTokenizer(viewName.substring(i2+1, i3), 
				StrMatcher.charSetMatcher(',', '='), 
				StrMatcher.singleQuoteMatcher());
		tokenizer.setEmptyTokenAsNull(true);
		while(tokenizer.hasNext()){
			String name = tokenizer.next();
			String value = null;
			try{
				value = tokenizer.next();
			}catch(NoSuchElementException e){
				// do nothing, value should be null anyway.
			}
			parameters.put(name, value);
		}
		return parameters;
	}
}
