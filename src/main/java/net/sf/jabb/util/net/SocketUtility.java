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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.URL;
import java.util.regex.Pattern;

/**
 * Utility for Socket related functions.
 * 
 * @author James Hu
 *
 */
public abstract class SocketUtility {
	static public final String[] CHECK_IP_ENDPOINTS = new String[]{
			"http://whatismyip.akamai.com", 
			"http://checkip.amazonaws.com",
			"http://curlmyip.com",
			"http://ipinfo.io/ip",
			"http://www.trackip.net/ip",
			"http://icanhazip.com"
	};
	static public final String IPv4_ADDRESS_PATTERN = 
			"^([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\." +
			"([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\." +
			"([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\." +
			"([01]?\\d\\d?|2[0-4]\\d|25[0-5])$";
	
	/**
	 * Check if the address string is a valid IPv4 address
	 * @param address	the address string to be tested, can be null
	 * @return	true if it is a valid IPv4 address, false otherwise
	 */
	static public boolean isIPv4Address(String address){
		return address != null && Pattern.matches(IPv4_ADDRESS_PATTERN, address);
	}
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
	
	/**
	 * Get external ip address that the machine is exposed to internet
	 * @return	the ip address or null if unable to determin
	 */
	static public String getExternalIpAddress(){
		String ip = null;
		URL url = null;
		BufferedReader in = null;
		
		for (int i = 0; !isIPv4Address(ip) && i < CHECK_IP_ENDPOINTS.length; i ++, url = null, in = null){
			try{
				url = new URL(CHECK_IP_ENDPOINTS[i]);
				in = new BufferedReader(new InputStreamReader(url.openStream()));
				ip = in.readLine(); //you get the IP as a String
			}catch(Exception e){
				System.err.println("Unable to find out external IP address through end point '" + CHECK_IP_ENDPOINTS[i] + "': " + e.getMessage());
				if (in != null){
					try{
						in.close();
					}catch(Exception e2){
						// ignore
					}
				}
			}
		}
		return ip;
		
	}

}
