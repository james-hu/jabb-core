/*
Copyright 2012 James Hu

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
package net.sf.jabb.jetty;

import org.eclipse.jetty.server.Server;
import org.springframework.context.Lifecycle;

/**
 * Helper for managing Jetty Server.
 * @author James Hu
 *
 */
public class ServerHelper implements Lifecycle {
	/**
	 * The Jetty Server
	 */
	protected Server server;
	
	/**
	 * Constructor with no Jetty Server specified.
	 * The Server need to be specified later with setServer(Server svr) method.
	 */
	public ServerHelper(){
	}
	
	/**
	 * Constructor with Jetty Server specified.
	 * @param svr The Jetty Server that will be associated with ServerHelper.
	 */
	public ServerHelper(Server svr){
		this();
		setServer(svr);
	}

	public Server getServer() {
		return server;
	}

	public void setServer(Server server) {
		this.server = server;
	}

	/**
	 * @see org.springframework.context.Lifecycle#isRunning()
	 */
	public boolean isRunning() {
		return server.isRunning();
	}

	/**
	 * RuntimeException will be thrown if the server failed to start.
	 * @see org.springframework.context.Lifecycle#start()
	 */
	public void start(){
		try {
			server.start();
		} catch (Exception e) {
			throw new RuntimeException("Jetty server failed to start.", e);
		}

	}

	/**
	 * RuntimeException will be thrown if the server failed to stop.
	 * @see org.springframework.context.Lifecycle#stop()
	 */
	public void stop() {
		try {
			server.stop();
		} catch (Exception e) {
			throw new RuntimeException("Jetty server failed to stop.", e);
		}

	}

}
