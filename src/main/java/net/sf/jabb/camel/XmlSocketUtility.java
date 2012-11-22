/*
Copyright 2010-2011 Zhengmao HU (James)

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

package net.sf.jabb.camel;

import java.nio.charset.Charset;

import net.sf.jabb.netty.XmlDecoder;
import net.sf.jabb.util.thread.Sequencer;

import org.apache.camel.CamelContext;
import org.apache.camel.builder.RouteBuilder;
import org.jboss.netty.handler.codec.string.StringEncoder;

/**
 * This utility makes it convenient to provide XML protocol based socket interface 
 * (for example, SOAP over TCP rather than HTTP) in Camel.<br>
 * 这个工具提供一些方便使用的方法，从而可以利用Camel提供基于XML的Socket接口
 * （比如SOAP over TCP而不是HTTP）。
 * 
 * @author Zhengmao HU (James)
 *
 */
public class XmlSocketUtility {
	static protected Sequencer xmlFrameDecoderSeq = new Sequencer();
	static protected Sequencer xmlStringDecoderSeq = new Sequencer();
	static protected Sequencer stringEncoderSeq = new Sequencer();

	
	/**
	 * Creates a XML protocol based socket server in Camel.<br>
	 * 在Camel中，创建一个基于XML的Socket接口服务器。
	 * 
	 * @param camelContext		CamelContext which must be based on CombinedRegistry, because additional entries must
	 * 							be added to the Registry.<br>
	 * 							Camel的Context，它必须用的是CombinedRegistry，因为要往Registry里放东西。
	 * @param serverUri			URI of the socket server which will be used by Camel Netty component
	 * 							to create an Endpoint.<br>
	 * 							服务器的URI，它将被Camel的Netty组件用来创建一个Endpoint。
	 *							 <p>For example: <code>tcp://localhost:9000</code>
	 * @param syncFlag			The sync parameter that will be send to Camel Netty component, 
	 * 							true means sync and false means async.<br>
	 * 							传递给Camel Netty组件的sync参数，true表示同步，false表示异步。
	 * @param toUri				The Pipe that receives the messages, usually it should be either direct or seda.<br>
	 * 							接收到的消息都会被送给这个Pipe，一般来说它要么是direct，要么是seda。
	 * @param topLevelTagName	Name of the top level tag that will be used to find the boundaries of XML messages.<br> 
	 * 							标识XML消息开头和结尾的标签名称。
	 * @param messageCharset	Charset that the XML messages received are supposed to be encoded in.<br>
	 * 							XML消息的字符编码方式
	 * @param maxMessageBytes	The maximum possible length of the XML messages received.<br>
	 * 							接收到的XML消息的最大可能长度
	 * @throws Exception 		Exception if the routes could not be created for whatever reason
	 */
	static public void addServer(CamelContext camelContext, final String serverUri, final boolean syncFlag, final String toUri, 
			String topLevelTagName, Charset messageCharset, int maxMessageBytes) throws Exception {
		// Netty 用的encoder/decoder
		XmlDecoder xmlDecoder = new XmlDecoder(maxMessageBytes, topLevelTagName, messageCharset);
		StringEncoder stringEncoder = new StringEncoder(messageCharset);
		
		final String xmlFrameDecoderName = "xmlFrameDecoder" + xmlFrameDecoderSeq.next();
		final String xmlStringDecoderName = "xmlStringDecoder" + xmlStringDecoderSeq.next();
		final String stringEncoderName = "stringEncoder" + stringEncoderSeq.next();

		RegistryUtility.addDecoder(camelContext, xmlFrameDecoderName, xmlDecoder.getFrameDecoder());
		RegistryUtility.addDecoder(camelContext, xmlStringDecoderName, xmlDecoder.getStringDecoder());
		RegistryUtility.addEncoder(camelContext, stringEncoderName, stringEncoder);

		camelContext.addRoutes(new RouteBuilder(){
			@Override
			public void configure(){
				StringBuilder sb = new StringBuilder();
				sb.append("netty:").append(serverUri);
				sb.append(serverUri.contains("?")? '&' : '?');
				sb.append("sync=").append(syncFlag);
				sb.append("&decoders=#").append(xmlFrameDecoderName)
					.append(",#").append(xmlStringDecoderName)
					.append("&encoders=#").append(stringEncoderName);
				from(sb.toString()).to(toUri);
			}
		});
		
	}
	
	/**
	 * Strips off the top level tag from XML string.<br>
	 * 从XML字符串中去掉最上层的Tag。
	 * 
	 * @param xmlString	The XMl string to be processed
	 * @return	The result string with top level tag removed.
	 */
	static public String stripTopLevelTag(String xmlString){
		return xmlString.substring(xmlString.indexOf('>') + 1, xmlString.lastIndexOf('<'));
	}

}
