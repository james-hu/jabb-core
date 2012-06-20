package net.sf.jabb.netty.test;

import net.sf.jabb.netty.XmlDecoder;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.util.CharsetUtil;

public class XmlDecoderDemo {
	ChannelPipeline pipeline = null;

	/**
	 * @param args
	 */
	public static void main(String[] args) {


	}
	
	void initialize(){
		// construct a decoder
		XmlDecoder xmlDecoder = new XmlDecoder(
				10000, // maximum length of the XML messages
				"env:Envelope", // top level tag of the XML messages
				CharsetUtil.UTF_8); // character encoding of the messages

		// setup the pipeline to use the decoder
		pipeline.addLast("frameDecoder", xmlDecoder.getFrameDecoder());
		pipeline.addLast("stringDecoder", xmlDecoder.getStringDecoder());
	}

	void messageReceived(ChannelHandlerContext ctx, MessageEvent e) {
		String msg = (String) e.getMessage();
		System.out.print("The XML body is '" + msg + "'\n");
	}

}
