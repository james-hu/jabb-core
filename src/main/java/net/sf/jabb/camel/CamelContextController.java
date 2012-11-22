/*
Copyright 2010-2011 James Hu

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

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import org.apache.camel.CamelContext;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.impl.DefaultCamelContext;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * As a wrapper, it controls the CamelContext, for example, start, stop, suspend and resume.<br>
 * 作为一个包装层，它控制CamelContext的启动、停止、暂停、继续等。
 * <p>
 * The CamelContextController opens a server socket port and receives control commands from the port.
 * It controls the CamelContext according to the commands received.
 * Its runtime logging information are sent to commons-logging.
 * <p>
 * CamelContextController打开一个服务端口并且从这个端口接收控制命令，根据获得的命令，
 * 对所包装的CamelContext进行控制。
 * 它的运行时log信息被送给commons-logging。 
 * 
 * @author Zhengmao HU (James)
 *
 */
public class CamelContextController implements Runnable{
	private static final Log log = LogFactory.getLog(CamelContextController.class);
	
	/**
	 * The CamelContext that hosts the CamelContextController.
	 */
	protected DefaultCamelContext myContext;
	/**
	 * The CamelContext that the CamelContextController controls.
	 */
	protected CamelContext context;
	/**
	 * The queue for control commands.
	 */
	protected BlockingQueue<String> commandQueue;
	
	/**
	 * Creates an instance which wraps a specified CamelContext, 
	 * listens at a specified port and controls the CamelContext according to 
	 * commands received from that port.<br>
	 * 创建一个实例，它包装指定的某个CamelContext，在指定的端口上监听，
	 * 并根据接受到的命令对包装的CamelContext进行控制。
	 * 
	 * @param camelContext	The CamelContext that will be controlled.<br>
	 * 						将被控制的CamelContext。
	 * @param serverUri		The listening port in Camel Netty URI format, for example: <br>
	 * 						监听端口的URI，向这个端口发送命令就可以控制CamelContext。它遵循Camel中Netty组件的URI格式，比如：
	 * 						<p><code>tcp://localhost:99999?keepAlive=true</code> 
	 * @param autoDisconnect	Whether disconnect the socket connection from server side after each control command was received.<br>
	 * 							是否在收到每个控制命令之后就自动从服务器端断开socket连接。
	 * @throws Exception
	 */
	public CamelContextController(final CamelContext camelContext, final String serverUri, final boolean autoDisconnect) throws Exception{
		context = camelContext;
		commandQueue = new LinkedBlockingQueue<String>();
		
		myContext = new DefaultCamelContext();
		myContext.setName("Controller of '" + context.getName() + "'");
		myContext.addRoutes(new RouteBuilder(){
			@Override
			public void configure(){
				StringBuilder sb = new StringBuilder();
				sb.append("netty:").append(serverUri);
				sb.append(serverUri.contains("?")? '&' : '?');
				sb.append("sync=true&textline=true&disconnect=");
				sb.append(autoDisconnect? "true" : "false");
				from(sb.toString()).process(
						new Processor(){
							public void process(Exchange exchange)
									throws Exception {
								exchange.getOut().setBody(
										command(exchange.getIn().getBody(String.class))
										);
							}
						});
			}
		});
		myContext.start();
	}
	
	/**
	 * Creates an instance which wraps a specified CamelContext, 
	 * listens at a specified port and controls the CamelContext according to 
	 * commands received from that port.<br>
	 * 创建一个实例，它包装指定的某个CamelContext，在指定的端口上监听，
	 * 并根据接受到的命令对包装的CamelContext进行控制。
	 * <p>
	 * The socket connection will be disconnected from server side after each command
	 * is received from client side.<br>
	 * 每当从客户端接收到一个命令，服务器端就会自动断开socket连接。  
	 * 
	 * @param camelContext	The CamelContext that will be controlled.<br>
	 * 						将被控制的CamelContext。
	 * @param serverUri		The listening port in Camel Netty URI format, for example: <br>
	 * 						监听端口的URI，向这个端口发送命令就可以控制CamelContext。它遵循Camel中Netty组件的URI格式，比如：
	 * 						<p><code>tcp://localhost:99999?keepAlive=true</code> 
	 * @throws Exception
	 */
	public CamelContextController(final CamelContext camelContext, final String serverUri) throws Exception{
		this(camelContext, serverUri, true);
	}
	
	/**
	 * Sends command to the control command queue.<br>
	 * 向待处理的控制命令队列中发送一个命令。
	 * <p>
	 * Usually this method should not be called from external directly, 
	 * commands should be sent to the listening port instead.
	 * <p>
	 * 一般不从外部直接调用这个方法，而是向监听端口发送命令。
	 * 
	 * @param cmd	Control commands, for example, start, stop, suspend, resume, and status.<br>
	 * 				控制命令，比如start, stop, suspend, resume, status。
	 */
	public String command(String cmd){
		cmd = cmd.trim().toLowerCase();
		if ("status".equals(cmd)){
			log.info("CamelContext '" + context.getName() + "' received 'status' command.");
			return "Ok.\r\n" 
				+ "  Status: " + context.getStatus().toString() 
				+ "   Uptime: " + context.getUptime()
				+ "\r\n";
		}else if ("exit".equals(cmd)){
			commandQueue.add("stop");
			commandQueue.add("exit");
			return "Ok.\r\n";
		}else if ("stop".equals(cmd)
				|| "start".equals(cmd)
				|| "suspend".equals(cmd)
				|| "resume".equals(cmd)){
			commandQueue.add(cmd);
			return "Ok.\r\n";
		} else{
			return "Unknown command.\r\n";
		}
	}
	
	/**
	 * Starts the command processing loop, exit until "exit" command was received.<br>
	 * 循环处理控制命令，直到收到“exit”命令。
	 */
	public void run(){
		while(true){
			String cmd = null;
			try {
				cmd = commandQueue.take();
			} catch (InterruptedException e) {
				continue;
			}
			if ("exit".equals(cmd)){
				log.info("CamelContextController for '" + context.getName() + "' received '" + cmd + "' command.");
				break;
			}
			log.info("CamelContext '" + context.getName() + "' received '" + cmd + "' command.");
			try {
				context.getClass().getMethod(cmd).invoke(context);
				log.info("CamelContext '" + context.getName() + "' successfully proccessed '" + cmd + "' command.");
			}catch(Exception e){
				log.error("CamelContext '" + context.getName() + "' failed processing '" + cmd + "' command.", e);
			}
		}
		try {
			myContext.stop();
			log.info("CamelContextController for '" + context.getName() + "' will exit.");
		} catch (Exception e) {
			log.error("Error stopping CamelContextController's own CamelContext '" + myContext.getName() + "'.", e);
		}
	}
	
	/**
	 * Starts the CamelContext and the CamelContextController.<br>
	 * 启动CamelContext及CamelContextController。
	 * <p>
	 * They will not exit until the "exit" command was received by CamelContextController.
	 * <p>
	 * 它们直到收到exit命令才一起退出。
	 */
	public void start(){
		command("start");
		run();
	}
}
