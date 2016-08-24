/**
 * 
 */
package net.sf.jabb.util.web;

import static org.junit.Assert.assertNull;

import javax.servlet.http.HttpServletRequest;

import org.junit.Test;
import org.mockito.Mockito;

/**
 * @author James Hu (Zhengmao Hu)
 *
 */
public class HttpServletRequestUtilityTest {
	@Test
	public void testGetUsername(){
		HttpServletRequest request = Mockito.mock(HttpServletRequest.class);
		Mockito.when(request.getParameter(Mockito.anyString())).thenReturn(null);
		Mockito.when(request.getHeader("Authorization")).thenReturn(null, "", " ", "Basic", "Basic ");	
		
		for (int i = 0; i < 5; i++){
			assertNull(HttpServletRequestUtility.getBasicAuthUsername(request));
		}
	}


}
