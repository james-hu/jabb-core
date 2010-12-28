/*
Copyright 2010 Zhengmao HU (James)

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
import org.apache.camel.impl.PropertyPlaceholderDelegateRegistry;
import org.apache.camel.spi.Registry;
import org.jboss.netty.handler.codec.string.StringEncoder;

/**
 * 提供一些方便利用Camel提供基于XML的Socket接口的方法。
 * 
 * @author Zhengmao HU (James)
 *
 */
public class XmlSocketUtility {
	static protected Sequencer xmlFrameDecoderSeq = new Sequencer();
	static protected Sequencer xmlStringDecoderSeq = new Sequencer();
	static protected Sequencer stringEncoderSeq = new Sequencer();

	
	/**
	 * 在Camel中，创建一个基于XML的Socket接口服务器。
	 * @param camelContext		Camel的Context，它必须用的是CombinedRegistry，因为要往Registry里放东西。
	 * @param serverUri			服务器的URI，它将被Camel的Netty组件用来创建一个Endpoint。比如“tcp://localhost:9000”
	 * @param syncFlag			传递给Camel-Netty的sync参数，true表示同步，false表示异步。
	 * @param toPipeName		接收到的消息都会被送给这个Pipe，一般来说它要么是direct，要么是seda。
	 * @param topLevelTagName	标识XML消息开头和结尾的标签名称
	 * @param messageCharset	XML消息的字符编码方式
	 * @param maxMessageBytes	接收到的XML消息的最大可能长度
	 * @throws Exception 		Exception if the routes could not be created for whatever reason
	 */
	static public void addServer(CamelContext camelContext, final String serverUri, final boolean syncFlag, final String toPipeName, 
			String topLevelTagName, Charset messageCharset, int maxMessageBytes) throws Exception {
		// Netty 用的encoder/decoder
		XmlDecoder xmlDecoder = new XmlDecoder(maxMessageBytes, topLevelTagName, messageCharset);
		StringEncoder stringEncoder = new StringEncoder(messageCharset);
		
		final String xmlFrameDecoderName = "xmlFrameDecoder" + xmlFrameDecoderSeq.next();
		final String xmlStringDecoderName = "xmlStringDecoder" + xmlStringDecoderSeq.next();
		final String stringEncoderName = "stringEncoder" + stringEncoderSeq.next();

		Registry reg = camelContext.getRegistry();
		CombinedRegistry registry = null;
		if (reg instanceof PropertyPlaceholderDelegateRegistry){
			registry = (CombinedRegistry) ((PropertyPlaceholderDelegateRegistry)reg).getRegistry();
		}else{ // should not go here
			registry = (CombinedRegistry) reg;
		}
		RegistryUtility.addDecoder(registry, xmlFrameDecoderName, xmlDecoder.getFrameDecoder());
		RegistryUtility.addDecoder(registry, xmlStringDecoderName, xmlDecoder.getStringDecoder());
		RegistryUtility.addEncoder(registry, stringEncoderName, stringEncoder);

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
				from(sb.toString()).to(toPipeName);
			}
		});
		
	}
	
	/**
	 * 从XML字符串中去掉最上层的Tag。
	 * @param xmlString
	 * @return
	 */
	static public String stripTopLevelTag(String xmlString){
		return xmlString.substring(xmlString.indexOf('>') + 1, xmlString.lastIndexOf('<'));
	}

}
