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
package net.sf.jabb.util.net;

import java.io.IOException;
import java.net.ServerSocket;

/**
 * Utility for Socket related functions.
 * 
 * @author James Hu
 *
 */
public class SocketUtility {
	/**
	 * Check whether the specified server socket port is free or not. 
	 * Be aware that this method is not safe if there are so many programs trying to open server sockets.
	 * @param port the port number to be checked
	 * @return true if the port is free, or false if it is being used by other program.
	 */
	static public boolean isServerPortFree(int port){
		ServerSocket s;
		try {
			s = new ServerSocket(port);
			s.close();
		} catch (IOException e) {
			return false;
		}
		return true;
	}
	
	/**
	 * Get a free server port that can be used.  
	 * A preferred port number can be specified.
	 * Be aware that this method is not safe if there are so many programs trying to open server sockets.
	 * @param port preferred port number, or zero if no preference.
	 * @return the preferred port or any other free port, or zero if any exception occurred.
	 */
	static public int getFreeServerPort(int port){
		int resultPort = 0;
		ServerSocket s;
		try {
			s = new ServerSocket(port);
			resultPort = s.getLocalPort();
			s.close();
		} catch (IOException e) {
			resultPort = 0;
		}
		return resultPort;
	}
	
	/**
	 * Get a free server port that can be used. 
	 * Be aware that this method is not safe if there are so many programs trying to open server sockets.
	 * @return any free port, or zero if any exception occurred.
	 */
	static public int getFreeServerPort(){
		return getFreeServerPort(0);
	}
	
	/**
	 * Get a free server port that can be used.
	 * Several preferred port numbers can be specified.
	 * @param tryOthers  If all preferred ports are occupied, try other free ports or not.
	 * @param ports	preferred port numbers
	 * @return	A free port number, or zero if not found or exception occurred.
	 */
	static public int getFreeServerPort(boolean tryOthers, int... ports){
		for (int port: ports){
			if (isServerPortFree(port)){
				return port;
			}
		}
		return tryOthers? getFreeServerPort() : 0;
	}
	
	/**
	 * Get a free server port that can be used.
	 * Several preferred port numbers can be specified.
	 * If all preferred ports are occupied, other free ports will be tried.
	 * @param ports	preferred port numbers
	 * @return	A free port number, or zero if not found or exception occurred.
	 */
	static public int getFreeServerPort(int... ports){
		return getFreeServerPort(true, ports);
	}

}
