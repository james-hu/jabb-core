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
 * ��������ṩһЩ����ʹ�õķ������Ӷ���������Camel�ṩ����XML��Socket�ӿ�
 * ������SOAP over TCP������HTTP����
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
	 * ��Camel�У�����һ������XML��Socket�ӿڷ�������
	 * 
	 * @param camelContext		CamelContext which must be based on CombinedRegistry, because additional entries must
	 * 							be added to the Registry.<br>
	 * 							Camel��Context���������õ���CombinedRegistry����ΪҪ��Registry��Ŷ�����
	 * @param serverUri			URI of the socket server which will be used by Camel Netty component
	 * 							to create an Endpoint.<br>
	 * 							��������URI��������Camel��Netty�����������һ��Endpoint��
	 *							 <p>For example: <code>tcp://localhost:9000</code>
	 * @param syncFlag			The sync parameter that will be send to Camel Netty component, 
	 * 							true means sync and false means async.<br>
	 * 							���ݸ�Camel Netty�����sync������true��ʾͬ����false��ʾ�첽��
	 * @param toUri				The Pipe that receives the messages, usually it should be either direct or seda.<br>
	 * 							���յ�����Ϣ���ᱻ�͸����Pipe��һ����˵��Ҫô��direct��Ҫô��seda��
	 * @param topLevelTagName	Name of the top level tag that will be used to find the boundaries of XML messages.<br> 
	 * 							��ʶXML��Ϣ��ͷ�ͽ�β�ı�ǩ���ơ�
	 * @param messageCharset	Charset that the XML messages received are supposed to be encoded in.<br>
	 * 							XML��Ϣ���ַ����뷽ʽ
	 * @param maxMessageBytes	The maximum possible length of the XML messages received.<br>
	 * 							���յ���XML��Ϣ�������ܳ���
	 * @throws Exception 		Exception if the routes could not be created for whatever reason
	 */
	static public void addServer(CamelContext camelContext, final String serverUri, final boolean syncFlag, final String toUri, 
			String topLevelTagName, Charset messageCharset, int maxMessageBytes) throws Exception {
		// Netty �õ�encoder/decoder
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
	 * ��XML�ַ�����ȥ�����ϲ��Tag��
	 * 
	 * @param xmlString	The XMl string to be processed
	 * @return	The result string with top level tag removed.
	 */
	static public String stripTopLevelTag(String xmlString){
		return xmlString.substring(xmlString.indexOf('>') + 1, xmlString.lastIndexOf('<'));
	}

}
