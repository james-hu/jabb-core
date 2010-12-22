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
 * ����CamelContext��������ֹͣ�ȡ�
 * @author Zhengmao HU (James)
 *
 */
public class CamelContextController implements Runnable{
	static final Log log = LogFactory.getLog(CamelContextController.class);
	
	protected CamelContext myContext;
	protected CamelContext context;
	protected BlockingQueue<String> commandQueue;
	
	/**
	 * ����һ��������ָ��CamelContext�ϣ�������ָ���˿��ϵ�ʵ����
	 * @param camelContext
	 * @param serverUri		�˿�URI��������˿ڷ�������Ϳ��Կ���CamelContext��
	 * @throws Exception
	 */
	public CamelContextController(final CamelContext camelContext, final String serverUri) throws Exception{
		context = camelContext;
		commandQueue = new LinkedBlockingQueue<String>();
		
		myContext = new DefaultCamelContext();
		myContext.addRoutes(new RouteBuilder(){
			@Override
			public void configure(){
				StringBuilder sb = new StringBuilder();
				sb.append("netty:").append(serverUri);
				sb.append(serverUri.contains("?")? '&' : '?');
				sb.append("sync=true&textline=true&disconnect=false");
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
	 * ��CamelContext���Ϳ��ơ�
	 * @param cmd	�����������start, stop, suspend, resume, status��
	 */
	public String command(String cmd){
		cmd = cmd.trim().toLowerCase();
		if ("status".equals(cmd)){
			log.info("CamelContext '" + context.getName() + "' received 'status' command.");
			return "Status: " + context.getStatus().toString() 
				+ "   Uptime: " + context.getUptime();
		}else if ("exit".equals(cmd)){
			commandQueue.add("stop");
			commandQueue.add("exit");
			return "";
		}else if ("stop".equals(cmd)
				|| "start".equals(cmd)
				|| "suspend".equals(cmd)
				|| "resume".equals(cmd)){
			commandQueue.add(cmd);
			return "";
		} else{
			return "Unknown command.";
		}
	}
	
	/**
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
	 * ����Context��ֱ���յ�exit������˳�
	 */
	public void start(){
		command("start");
		run();
	}
}
