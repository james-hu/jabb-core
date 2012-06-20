/*
 * Copyright 2009 Red Hat, Inc.
 *
 * Red Hat licenses this file to you under the Apache License, version 2.0
 * (the "License"); you may not use this file except in compliance with the
 * License.  You may obtain a copy of the License at:
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the
 * License for the specific language governing permissions and limitations
 * under the License.
 */
package net.sf.jabb.util.misctest.netty;

import java.net.InetSocketAddress;
import java.util.concurrent.Executors;

import org.jboss.netty.bootstrap.ClientBootstrap;
//import org.jboss.netty.channel.ChannelFuture;
import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.channel.ChannelPipelineFactory;
import org.jboss.netty.channel.Channels;
import org.jboss.netty.channel.socket.nio.NioClientSocketChannelFactory;

/**
 * Sends one message when a connection is open and echoes back any received
 * data to the server.  Simply put, the echo client initiates the ping-pong
 * traffic between the echo client and server by sending the first message to
 * the server.
 *
 * @author <a href="http://www.jboss.org/netty/">The Netty Project</a>
 * @author <a href="http://gleamynode.net/">Trustin Lee</a>
 *
 * @version $Rev: 2080 $, $Date: 2010-01-26 18:04:19 +0900 (Tue, 26 Jan 2010) $
 *
 */
public class EchoClient {

    public static void main(String[] args) throws Exception {
        // Print usage if no argument is specified.
        if (args.length < 3 || args.length > 4) {
            System.err.println(
                    "Usage: " + EchoClient.class.getSimpleName() +
                    " <total connections> <host> <port> [<first message size>]");
            return;
        }

        // Parse options.
        final int totalConnections = Integer.parseInt(args[0]);
        final String host = args[1];
        final int port = Integer.parseInt(args[2]);
        final int firstMessageSize;
        if (args.length == 4) {
            firstMessageSize = Integer.parseInt(args[3]);
        } else {
            firstMessageSize = 256;
        }

        // Configure the client.
        ClientBootstrap bootstrap = new ClientBootstrap(
                new NioClientSocketChannelFactory(
                        Executors.newCachedThreadPool(),
                        Executors.newCachedThreadPool()));

        final EchoClientHandler handler = new EchoClientHandler(firstMessageSize);
        // Set up the pipeline factory.
        bootstrap.setPipelineFactory(new ChannelPipelineFactory() {
            public ChannelPipeline getPipeline() throws Exception {
                return Channels.pipeline(handler);
            }
        });
        
        // remember to modify registry on Windows Server 2003: 
        // HKEY_LOCAL_MACHINE\SYSTEM\CurrentControlSet\Services\Tcpip\Parameters  MaxUserPort

        while (handler.getTotalConnections() < totalConnections){
            // Start the connection attempt.
            //ChannelFuture future = 
            		bootstrap.connect(new InetSocketAddress(host, port));
            //System.out.println("Client created: " + future.getChannel().getLocalAddress());
            try {
				Thread.sleep(30);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
        }
        /*
        // Wait until the connection is closed or the connection attempt fails.
        future.getChannel().getCloseFuture().awaitUninterruptibly();

        // Shut down thread pools to exit.
        bootstrap.releaseExternalResources();
        */
    }
}

