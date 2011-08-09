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
 * 使得Netty能够对XML文本消息进行分段的decoder。
 * <p>
 * The usage is as the following:<br>
 * 使用方法如下：
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
	 * 创建一个实例，创建好之后，它的getFrameDecoder()方法和getStringDecoder()方法都应该被调用
	 * 以获得给Netty用的两个decoder。
	 * 
	 * @param maxFrameLength	Maximum length of XML text messages that might be received.<br>
	 * 							可能接收到的XML消息的最大长度。
	 * @param topLevelTagName	Top level XML tag that marks the beginning and ending of XML messages.<br>
	 * 							用来标记每段XML消息开头与结束的顶层XML标签。
	 * @param charset			Character set that the text messages are encoded in.<br>
	 * 							所接收到的消息的编码字符集。
	 */
	public XmlDecoder(int maxFrameLength, String topLevelTagName, Charset charset){
		frameDecoder = new DelimiterBasedFrameDecoder(maxFrameLength, true, 
				buildDelimiters(topLevelTagName, charset));
		stringDecoder = new XmlStringDecoder(topLevelTagName, charset);
	}
	
	/**
	 * Creates delimiters that will be used to construct DelimiterBasedFrameDecoder.<br>
	 * 根据XML标签的名称，生成适合DelimiterBasedFrameDecoder用的delimiters。
	 * 
	 * @param tagName	Name of the top level XML tag (not including &lt;, /, etc).<br>
	 * 					XML标签的名称（不包括尖括号、斜杠这些）
	 * @param charset	Character set encoding of the messages to be processed.<br>
	 * 					待解析的XML文本所采用的字符集编码方式
	 * @return 			Four delimiters in an array.<br>
	 * 					共4个delimiter放在一个数组里，都以“&lt;/”开头，然后是tag的名称，再接着分别是空格、制表符（TAB）、回车、换行
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
	 * 获得给Netty用的frame decoder。
	 * <p>
	 * Always use this method together with getStringDecoder().<br>
	 * 这个方法总是与getStringDecoder()方法结合起来一起用。
	 * 
	 * @return	The decoder that separates XML messages.<br>
	 * 			用来把XML消息区分开来的decoder。
	 */
	public DelimiterBasedFrameDecoder getFrameDecoder() {
		return frameDecoder;
	}

	/**
	 * Get the string decoder to be used by Netty.<br>
	 * 获得给Netty用的string decoder。
	 * <p>
	 * Always use this method together with getFrameDecoder().<br>
	 * 这个方法总是与getFrameDecoder()方法结合起来一起用。
	 * 
	 * @return the decoder that do final clean up for XML messages.<br>
	 * 			用来对XML消息进行最终清理的decoder。
	 */
	public OneToOneDecoder getStringDecoder() {
		return stringDecoder;
	}
	
	/**
	 * Further process the messages already processed by 
	 * FrameDecoder to make it clean XML text messages.<br>
	 * 把经过FrameDecoder处理过后的消息，进一步加工为头尾完整干净的XML文本。
	 * <p>
	 * Don't use this class directly. It is a supporting class for XmlDecoder.
	 * <p>
	 * 不要直接用这个类，它是支撑XmlDecoder的。
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
		 * 创建一个实例。
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
		 * 先取到字符串，然后把取得的字符串进一步加工，以形成头尾完整干净的XML文本。
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
