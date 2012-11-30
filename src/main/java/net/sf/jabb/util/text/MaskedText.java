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

/**
 * A POJO for storing masked text. 
 * The clearText field is the original text before being masked.
 * The text field is the text in which part of the content had been masked.
 * The masked field is the masked content in clear.
 * 
 * @author James Hu
 *
 */
public class MaskedText {
	protected String clearText;
	protected String text;
	protected String masked;
	
	public MaskedText(){
		
	}
	
	public String getClearText() {
		return clearText;
	}
	public void setClearText(String clearText) {
		this.clearText = clearText;
	}
	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public String getMasked() {
		return masked;
	}
	public void setMasked(String masked) {
		this.masked = masked;
	}
}
