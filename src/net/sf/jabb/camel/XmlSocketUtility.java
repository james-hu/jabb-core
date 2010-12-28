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
 * �ṩһЩ��������Camel�ṩ����XML��Socket�ӿڵķ�����
 * 
 * @author Zhengmao HU (James)
 *
 */
public class XmlSocketUtility {
	static protected Sequencer xmlFrameDecoderSeq = new Sequencer();
	static protected Sequencer xmlStringDecoderSeq = new Sequencer();
	static protected Sequencer stringEncoderSeq = new Sequencer();

	
	/**
	 * ��Camel�У�����һ������XML��Socket�ӿڷ�������
	 * @param camelContext		Camel��Context���������õ���CombinedRegistry����ΪҪ��Registry��Ŷ�����
	 * @param serverUri			��������URI��������Camel��Netty�����������һ��Endpoint�����硰tcp://localhost:9000��
	 * @param syncFlag			���ݸ�Camel-Netty��sync������true��ʾͬ����false��ʾ�첽��
	 * @param toPipeName		���յ�����Ϣ���ᱻ�͸����Pipe��һ����˵��Ҫô��direct��Ҫô��seda��
	 * @param topLevelTagName	��ʶXML��Ϣ��ͷ�ͽ�β�ı�ǩ����
	 * @param messageCharset	XML��Ϣ���ַ����뷽ʽ
	 * @param maxMessageBytes	���յ���XML��Ϣ�������ܳ���
	 * @throws Exception 		Exception if the routes could not be created for whatever reason
	 */
	static public void addServer(CamelContext camelContext, final String serverUri, final boolean syncFlag, final String toPipeName, 
			String topLevelTagName, Charset messageCharset, int maxMessageBytes) throws Exception {
		// Netty �õ�encoder/decoder
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
	 * ��XML�ַ�����ȥ�����ϲ��Tag��
	 * @param xmlString
	 * @return
	 */
	static public String stripTopLevelTag(String xmlString){
		return xmlString.substring(xmlString.indexOf('>') + 1, xmlString.lastIndexOf('<'));
	}

}
