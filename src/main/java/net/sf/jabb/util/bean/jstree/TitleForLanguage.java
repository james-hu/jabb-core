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

import java.io.Serializable;


/**
 * @author James Hu
 *
 */
public class TitleForLanguage implements Serializable{
	private static final long serialVersionUID = 8042876245409471549L;
	
	private String language;
	private String title;
	
	public TitleForLanguage(){
		
	}

	public TitleForLanguage(String language, String title){
		this.title = title;
		this.language = language;
	}
	
	@Override
	public String toString(){
		return "(" + (language == null ? "<null>" : language) + ":"
				+ (title == null ? "<null>" : title) + ")";
	}

	public String getLanguage() {
		return language;
	}

	public void setLanguage(String language) {
		this.language = language;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}
	

}
