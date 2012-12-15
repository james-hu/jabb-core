/*
Copyright 2012 James Hu

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
package net.sf.jabb.util.text;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * An utility to mask and unmask passwords.
 * 
 * @author James Hu
 *
 */
public class PasswordUtility {
	private static final Pattern URL_PATTERN = Pattern.compile("[a-z]+://.*");
	private static final Pattern PASSWORD_IN_URL_PATTERN = Pattern.compile(":(?:[^/]+)@");
	private static final String PASSWORD_IN_URL_MASK = ":*****@";
	//private static final String PASSWORD_IN_URL_MASK_REGEXP = PASSWORD_IN_URL_MASK.replace("*", "\\*");

	/**
	 * Mask password in URL.
	 * 
	 * @param url  The original URL string.
	 * @return		The result containing original URL, masked URL and the password which had been masked.
	 * 				If there is no password filed found in the URL, in the result, original and masked URL
	 * 				will be the same, and password will be null.
	 */
	public static MaskedText maskInUrl(String url){
		MaskedText result = new MaskedText();
		result.setClearText(url);
		result.setText(url);
		result.setMasked(null);
		
		if (url != null){
			Matcher urlMatcher = URL_PATTERN.matcher(url);
			if (urlMatcher.find()) {
				Matcher pwdMatcher = PASSWORD_IN_URL_PATTERN.matcher(url);
				if (pwdMatcher.find()){
					result.setText(pwdMatcher.replaceFirst(PASSWORD_IN_URL_MASK));
					String s = pwdMatcher.group();  // with leading ":" and trailing "@"
					result.setMasked(s.substring(1, s.length()-1));
				}
			}
		}
		return result;
	}
	
	/**
	 * Unmask password in URL.
	 * @param url				The URL in which the password part had been masked.
	 * @param password			The clear password
	 * @return					The URL with clear password.
	 */
	public static String unmaskInUrl(String url, String password){
		String result = null;
		if (url != null){
			Matcher urlMatcher = URL_PATTERN.matcher(url);
			if (urlMatcher.find()) {
				int s = url.indexOf(PASSWORD_IN_URL_MASK);
				if (s >= 0){
					StringBuilder sb = new StringBuilder();
					sb.append(url.substring(0, s));
					if (password != null){
						sb.append(':').append(password);
					}
					sb.append('@');
					sb.append(url.substring(s+PASSWORD_IN_URL_MASK.length(), url.length()));
					result = sb.toString();
				}
			}
		}
		return result == null ? url : result;
	}

}
