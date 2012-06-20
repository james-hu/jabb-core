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
package net.sf.jabb.util.bean.jstree;

import net.sf.jabb.util.bean.DoubleValueBean;

/**
 * @author James Hu
 *
 */
public class TitleAndLanguage extends DoubleValueBean<String, String> {
	private static final long serialVersionUID = 8042876245409471549L;

	public TitleAndLanguage(String title, String language){
		super(title, language);
	}
	
	public String getTitle(){
		return getValue1();
	}
	
	public String getLanguage(){
		return getValue2();
	}
	
	public void setTitle(String title){
		setValue1(title);
	}
	
	public void setLanguage(String language){
		setValue2(language);
	}
}
