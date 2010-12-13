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
 * ��������Netty��ζ�XML�ı���Ϣ���зֶε����⡣
 * <p>
 * ���������
 * <pre>
 * {@link ChannelPipeline} pipeline = ...;
 * 
 * XmlDecoder xmlDecoder = new XmlDecoder(10000, "env:Envelope", CharsetUtil.UTF_8);
 *
 * // Decoders
 * pipeline.addLast("frameDecoder", xmlDecoder.{@link #getFrameDecoder()});
 * pipeline.addLast("stringDecoder", xmlDecoder.{@link #getStringDecoder()});
 *
 * </pre>
 * and then you can use a {@link String} instead of a {@link ChannelBuffer}
 * as a message:
 * <pre>
 * void messageReceived({@link ChannelHandlerContext} ctx, {@link MessageEvent} e) {
 *     String msg = (String) e.getMessage();
 *     ch.write("The XML body is '" + msg + "'\n");
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
	 * ����һ��ʵ����������֮��Ӧ��������getFrameDecoder()������getStringDecoder()��������ø�Netty�õ�decoder��
	 * @param maxFrameLength
	 * @param topLevelTagName
	 * @param charset
	 */
	public XmlDecoder(int maxFrameLength, String topLevelTagName, Charset charset){
		frameDecoder = new DelimiterBasedFrameDecoder(maxFrameLength, true, 
				buildDelimiters(topLevelTagName, charset));
		stringDecoder = new XmlStringDecoder(topLevelTagName, charset);
	}
	
	/**
	 * ����XML��ǩ�����ƣ������ʺ�DelimiterBasedFrameDecoder�õ�delimiters��
	 * @param tagName	XML��ǩ�����ƣ������������š�б����Щ��
	 * @param charset	��������XML�ı������õ��ַ������뷽ʽ
	 * @return ��4��delimiter����һ����������ԡ�</����ͷ��Ȼ����tag�����ƣ��ٽ��ŷֱ��ǿո��Ʊ����TAB�����س�������
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
	 * �������������getStringDecoder()�����������һ���á�
	 * @return
	 */
	public DelimiterBasedFrameDecoder getFrameDecoder() {
		return frameDecoder;
	}

	/**
	 * �������������getFrameDecoder()�����������һ���á�
	 * @return
	 */
	public OneToOneDecoder getStringDecoder() {
		return stringDecoder;
	}
	
	/**
	 * �Ѿ���FrameDecoder����������Ϣ����һ���ӹ�Ϊͷβ�����ɾ���XML�ı���
	 * @author Zhengmao HU (James)
	 *
	 */
	static protected class XmlStringDecoder extends OneToOneDecoder{
	    private final Charset charset;
		private final String[] startTags;
		private final String endTag;
		
		/**
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
