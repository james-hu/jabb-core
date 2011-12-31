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
 * ��Ϊһ����װ�㣬������CamelContext��������ֹͣ����ͣ�������ȡ�
 * <p>
 * The CamelContextController opens a server socket port and receives control commands from the port.
 * It controls the CamelContext according to the commands received.
 * Its runtime logging information are sent to commons-logging.
 * <p>
 * CamelContextController��һ������˿ڲ��Ҵ�����˿ڽ��տ���������ݻ�õ����
 * ������װ��CamelContext���п��ơ�
 * ��������ʱlog��Ϣ���͸�commons-logging�� 
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
	 * ����һ��ʵ��������װָ����ĳ��CamelContext����ָ���Ķ˿��ϼ�����
	 * �����ݽ��ܵ�������԰�װ��CamelContext���п��ơ�
	 * 
	 * @param camelContext	The CamelContext that will be controlled.<br>
	 * 						�������Ƶ�CamelContext��
	 * @param serverUri		The listening port in Camel Netty URI format, for example: <br>
	 * 						�����˿ڵ�URI��������˿ڷ�������Ϳ��Կ���CamelContext������ѭCamel��Netty�����URI��ʽ�����磺
	 * 						<p><code>tcp://localhost:99999?keepAlive=true</code> 
	 * @param autoDisconnect	Whether disconnect the socket connection from server side after each control command was received.<br>
	 * 							�Ƿ����յ�ÿ����������֮����Զ��ӷ������˶Ͽ�socket���ӡ�
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
							@Override
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
	 * ����һ��ʵ��������װָ����ĳ��CamelContext����ָ���Ķ˿��ϼ�����
	 * �����ݽ��ܵ�������԰�װ��CamelContext���п��ơ�
	 * <p>
	 * The socket connection will be disconnected from server side after each command
	 * is received from client side.<br>
	 * ÿ���ӿͻ��˽��յ�һ������������˾ͻ��Զ��Ͽ�socket���ӡ�  
	 * 
	 * @param camelContext	The CamelContext that will be controlled.<br>
	 * 						�������Ƶ�CamelContext��
	 * @param serverUri		The listening port in Camel Netty URI format, for example: <br>
	 * 						�����˿ڵ�URI��������˿ڷ�������Ϳ��Կ���CamelContext������ѭCamel��Netty�����URI��ʽ�����磺
	 * 						<p><code>tcp://localhost:99999?keepAlive=true</code> 
	 * @throws Exception
	 */
	public CamelContextController(final CamelContext camelContext, final String serverUri) throws Exception{
		this(camelContext, serverUri, true);
	}
	
	/**
	 * Sends command to the control command queue.<br>
	 * �������Ŀ�����������з���һ�����
	 * <p>
	 * Usually this method should not be called from external directly, 
	 * commands should be sent to the listening port instead.
	 * <p>
	 * һ�㲻���ⲿֱ�ӵ����������������������˿ڷ������
	 * 
	 * @param cmd	Control commands, for example, start, stop, suspend, resume, and status.<br>
	 * 				�����������start, stop, suspend, resume, status��
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
	 * ѭ������������ֱ���յ���exit�����
	 */
	@Override
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
	 * ����CamelContext��CamelContextController��
	 * <p>
	 * They will not exit until the "exit" command was received by CamelContextController.
	 * <p>
	 * ����ֱ���յ�exit�����һ���˳���
	 */
	public void start(){
		command("start");
		run();
	}
}
