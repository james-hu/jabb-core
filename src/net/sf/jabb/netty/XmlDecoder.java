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
 * 用来处理Netty如何对XML文本消息进行分段的问题。
 * <p>
 * 代码举例：
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
	 * 创建一个实例。创建好之后，应该用它的getFrameDecoder()方法和getStringDecoder()方法来获得给Netty用的decoder。
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
	 * 根据XML标签的名称，生成适合DelimiterBasedFrameDecoder用的delimiters。
	 * @param tagName	XML标签的名称（不包括尖括号、斜杠这些）
	 * @param charset	待解析的XML文本所采用的字符集编码方式
	 * @return 共4个delimiter放在一个数组里，都以“</”开头，然后是tag的名称，再接着分别是空格、制表符（TAB）、回车、换行
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
	 * 这个方法总是与getStringDecoder()方法结合起来一起用。
	 * @return
	 */
	public DelimiterBasedFrameDecoder getFrameDecoder() {
		return frameDecoder;
	}

	/**
	 * 这个方法总是与getFrameDecoder()方法结合起来一起用。
	 * @return
	 */
	public OneToOneDecoder getStringDecoder() {
		return stringDecoder;
	}
	
	/**
	 * 把经过FrameDecoder处理过后的消息，进一步加工为头尾完整干净的XML文本。
	 * @author Zhengmao HU (James)
	 *
	 */
	static protected class XmlStringDecoder extends OneToOneDecoder{
	    private final Charset charset;
		private final String[] startTags;
		private final String endTag;
		
		/**
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
