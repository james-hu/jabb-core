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
package net.sf.jabb.util.web;

import java.util.List;
import java.util.Map;

import net.sf.jabb.util.bean.StringKeyValueBean;

/**
 * Configuration information of a web application
 * @author James Hu
 *
 */
public class WebApplicationConfiguration {
	protected List<String> mainMenuItems;
	protected List<StringKeyValueBean> supportedLocales;
	protected String defaultLocale;
	protected String jQueryTheme;

	public List<String> getMainMenuItems() {
		return mainMenuItems;
	}

	public void setMainMenuItems(List<String> mainMenuItems) {
		this.mainMenuItems = mainMenuItems;
	}

	public List<StringKeyValueBean> getSupportedLocales() {
		return supportedLocales;
	}

	public void setSupportedLocales(List<StringKeyValueBean> supportedLocales) {
		this.supportedLocales = supportedLocales;
	}

	public String getDefaultLocale() {
		return defaultLocale;
	}

	public void setDefaultLocale(String defaultLocale) {
		this.defaultLocale = defaultLocale;
	}

	public String getjQueryTheme() {
		return jQueryTheme;
	}

	public void setjQueryTheme(String jQueryTheme) {
		this.jQueryTheme = jQueryTheme;
	}



}
