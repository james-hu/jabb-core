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

import java.util.ArrayList;
import java.util.List;

import org.jboss.netty.channel.ChannelDownstreamHandler;
import org.jboss.netty.channel.ChannelHandler;
import org.jboss.netty.channel.ChannelUpstreamHandler;

/**
 * 提供方便地向CombinedRegistry中添加Netty所需的Codec的方法。
 * 但是注意，它不保证对Registry的操作是同步的。
 * 
 * @author Zhengmao HU (James)
 *
 */
public class RegistryUtility {
	static public final String NAME_ENCODERS = "encoders";
	static public final String NAME_DECODERS = "decoders";
	
	/**
	 * 向Registry中增加一个给Netty用的encoder。
	 * @param registry
	 * @param name
	 * @param encoder
	 */
	@SuppressWarnings("unchecked")
	static public void addEncoder(CombinedRegistry registry, String name, ChannelDownstreamHandler encoder){
		addCodecOnly(registry, name, encoder);
		
		List<ChannelDownstreamHandler> encoders;
		Object o = registry.lookup(NAME_ENCODERS);
		if (o == null){
			encoders = new ArrayList<ChannelDownstreamHandler>();
			registry.getDefaultSimpleRegistry().put(NAME_ENCODERS, encoders);
		}else{
			try{
				encoders = (List<ChannelDownstreamHandler>)o;
			}catch(Exception e){
				throw new IllegalArgumentException("Preserved name '" + NAME_ENCODERS + "' is already in use in at least one of the registries.");
			}
		}
		encoders.add(encoder);
	}

	/**
	 * 向Registry中增加一个给Netty用的decoder。
	 * @param registry
	 * @param name
	 * @param decoder
	 */
	@SuppressWarnings("unchecked")
	static public void addDecoder(CombinedRegistry registry, String name, ChannelUpstreamHandler decoder){
		addCodecOnly(registry, name, decoder);
		
		List<ChannelUpstreamHandler> decoders;
		Object o = registry.lookup(NAME_DECODERS);
		if (o == null){
			decoders = new ArrayList<ChannelUpstreamHandler>();
			registry.getDefaultSimpleRegistry().put(NAME_DECODERS, decoders);
		}else{
			try{
				decoders = (List<ChannelUpstreamHandler>)o;
			}catch(Exception e){
				throw new IllegalArgumentException("Preserved name '" + NAME_DECODERS + "' is already in use in at least one of the registries.");
			}
		}
		decoders.add(decoder);
	}
	
	/**
	 * 仅仅把codec直接加入到Registry中，而不处理加入到Registry里的encoders或decoders列表中。
	 * @param registry
	 * @param name
	 * @param codec
	 */
	static protected void addCodecOnly(CombinedRegistry registry, String name, ChannelHandler codec){
		if (registry.lookup(name) != null){
			throw new IllegalArgumentException("Codec name '" + name + "' is already in use in at least one of the registries.");
		}
		registry.getDefaultSimpleRegistry().put(name, codec);
	}
	
}
