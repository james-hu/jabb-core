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
package net.sf.jabb.netty;

import java.nio.charset.Charset;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.handler.codec.frame.DelimiterBasedFrameDecoder;
import org.jboss.netty.handler.codec.oneone.OneToOneDecoder;

/**
 * A decoder for Netty to handle XML messages.<br>
 * ʹ��Netty�ܹ���XML�ı���Ϣ���зֶε�decoder��
 * <p>
 * The usage is as the following:<br>
 * ʹ�÷������£�
 * <pre>
 * {@link ChannelPipeline} pipeline = ...;
 * 
 * // construct a decoder 
 * XmlDecoder xmlDecoder = new XmlDecoder(
 * 		10000, 				// maximum length of the XML messages
 * 		"env:Envelope", 	// top level tag of the XML messages
 * 		CharsetUtil.UTF_8);	// character encoding of the messages
 *
 * // setup the pipeline to use the decoder
 * pipeline.addLast("frameDecoder", xmlDecoder.{@link #getFrameDecoder()});
 * pipeline.addLast("stringDecoder", xmlDecoder.{@link #getStringDecoder()});
 *
 * </pre>
 * and then you can use a {@link String} instead of a {@link ChannelBuffer}
 * when processing messages:
 * <pre>
 * void messageReceived({@link ChannelHandlerContext} ctx, {@link MessageEvent} e) {
 *     String msg = (String) e.getMessage();
 *     System.out.print("The XML body is '" + msg + "'\n");
 * }
 * </pre>
 * 
 * @author Zhengmao HU (James)
 *
 */
public class XmlDecoder {
	protected static char[] startTagEndingChar = new char[] {'>', ' ', '\t', '\r', '\n'};
	protected static byte[] endTagEndingByte = new byte[] {0x3E, 0x20, 0x09, 0x0D, 0x0A};
	

	protected DelimiterBasedFrameDecoder frameDecoder;
	protected XmlStringDecoder stringDecoder;
	
	/**
	 * Constructor after which both its getFrameDecoder() and getStringDecoder() methods should be
	 * called to get two decoders for Netty.<br>
	 * ����һ��ʵ����������֮������getFrameDecoder()������getStringDecoder()������Ӧ�ñ�����
	 * �Ի�ø�Netty�õ�����decoder��
	 * 
	 * @param maxFrameLength	Maximum length of XML text messages that might be received.<br>
	 * 							���ܽ��յ���XML��Ϣ����󳤶ȡ�
	 * @param topLevelTagName	Top level XML tag that marks the beginning and ending of XML messages.<br>
	 * 							�������ÿ��XML��Ϣ��ͷ������Ķ���XML��ǩ��
	 * @param charset			Character set that the text messages are encoded in.<br>
	 * 							�����յ�����Ϣ�ı����ַ�����
	 */
	public XmlDecoder(int maxFrameLength, String topLevelTagName, Charset charset){
		frameDecoder = new DelimiterBasedFrameDecoder(maxFrameLength, true, 
				buildDelimiters(topLevelTagName, charset));
		stringDecoder = new XmlStringDecoder(topLevelTagName, charset);
	}
	
	/**
	 * Creates delimiters that will be used to construct DelimiterBasedFrameDecoder.<br>
	 * ����XML��ǩ�����ƣ������ʺ�DelimiterBasedFrameDecoder�õ�delimiters��
	 * 
	 * @param tagName	Name of the top level XML tag (not including &lt;, /, etc).<br>
	 * 					XML��ǩ�����ƣ������������š�б����Щ��
	 * @param charset	Character set encoding of the messages to be processed.<br>
	 * 					��������XML�ı������õ��ַ������뷽ʽ
	 * @return 			Four delimiters in an array.<br>
	 * 					��4��delimiter����һ����������ԡ�&lt;/����ͷ��Ȼ����tag�����ƣ��ٽ��ŷֱ��ǿո��Ʊ����TAB�����س�������
	 */
	protected ChannelBuffer[] buildDelimiters(String tagName, Charset charset){
		byte[] nameBytes = tagName.getBytes(charset);
		ChannelBuffer[] delimiters = new ChannelBuffer[endTagEndingByte.length];
		int i = 0;
		for (byte space: endTagEndingByte){
			byte[] tagBytes = new byte[nameBytes.length + 3];
			tagBytes[0] = 0x3C; //  '<'
			tagBytes[1] = 0x2F; //  '/'
			for (int j=0; j < nameBytes.length; j ++){
				tagBytes[j+2] = nameBytes[j];
			}
			tagBytes[tagBytes.length - 1] = space;
			delimiters[i++] = ChannelBuffers.wrappedBuffer(tagBytes);
		}
		return delimiters;
	}

	/**
	 * Get the frame decoder to be used by Netty.<br>
	 * ��ø�Netty�õ�frame decoder��
	 * <p>
	 * Always use this method together with getStringDecoder().<br>
	 * �������������getStringDecoder()�����������һ���á�
	 * 
	 * @return	The decoder that separates XML messages.<br>
	 * 			������XML��Ϣ���ֿ�����decoder��
	 */
	public DelimiterBasedFrameDecoder getFrameDecoder() {
		return frameDecoder;
	}

	/**
	 * Get the string decoder to be used by Netty.<br>
	 * ��ø�Netty�õ�string decoder��
	 * <p>
	 * Always use this method together with getFrameDecoder().<br>
	 * �������������getFrameDecoder()�����������һ���á�
	 * 
	 * @return the decoder that do final clean up for XML messages.<br>
	 * 			������XML��Ϣ�������������decoder��
	 */
	public OneToOneDecoder getStringDecoder() {
		return stringDecoder;
	}
	
	/**
	 * Further process the messages already processed by 
	 * FrameDecoder to make it clean XML text messages.<br>
	 * �Ѿ���FrameDecoder����������Ϣ����һ���ӹ�Ϊͷβ�����ɾ���XML�ı���
	 * <p>
	 * Don't use this class directly. It is a supporting class for XmlDecoder.
	 * <p>
	 * ��Ҫֱ��������࣬����֧��XmlDecoder�ġ�
	 * 
	 * @author Zhengmao HU (James)
	 *
	 */
	static protected class XmlStringDecoder extends OneToOneDecoder{
	    private final Charset charset;
		private final String[] startTags;
		private final String endTag;
		
		/**
		 * Constructor.<br>
		 * ����һ��ʵ����
		 * @param tagName
		 * @param charset
		 */
		public XmlStringDecoder(String tagName, Charset charset){
			this.charset = charset;
			startTags = new String[startTagEndingChar.length];
			int i = 0;
			for (char ending: startTagEndingChar){
				startTags[i++] = "<" + tagName + ending ;
			}
			endTag = "</" + tagName + '>';
		}

		/**
		 * Get the text and process its beginning and ending to make it a clean XML text.<br>
		 * ��ȡ���ַ�����Ȼ���ȡ�õ��ַ�����һ���ӹ������γ�ͷβ�����ɾ���XML�ı���
		 */
	    @Override
	    protected Object decode(
	            ChannelHandlerContext ctx, Channel channel, Object msg) throws Exception {
	        if (!(msg instanceof ChannelBuffer)) {
	            return msg;
	        }
	        String xml = ((ChannelBuffer) msg).toString(charset);
	        
	        int start = -1;
	        for (String startTag: startTags){
	        	start = xml.indexOf(startTag);
	        	if (start != -1){
	        		break;
	        	}
	        }
	        if (start == -1){
	        	return null;	// discard
	        }
	        
	        xml = xml.substring(start) + endTag;
	        return xml;
	    }

	}

}
