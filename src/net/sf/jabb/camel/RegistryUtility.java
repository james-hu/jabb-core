/*
Copyright 2010-2011 Zhengmao HU (James)

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

import org.apache.camel.CamelContext;
import org.apache.camel.impl.PropertyPlaceholderDelegateRegistry;
import org.apache.camel.spi.Registry;
import org.jboss.netty.channel.ChannelDownstreamHandler;
import org.jboss.netty.channel.ChannelHandler;
import org.jboss.netty.channel.ChannelUpstreamHandler;

/**
 * This utility provides convenient methods to work on Camel Registry, 
 * however CombinedRegistry is required in most of the cases.<br> 
 * 提供方便地对Camel的Registry进行操作的方法，不过大部分的时候只有CombinedRegistry才被支持。
 * <p>
 * For example, it provides methods to add Codec(s) required by Netty to Registry.<br>
 * 比如说，它提供向Registry中添加Netty所需的Codec的方法。
 * <p>
 * But you should be aware that it does not ensure multi-threads safe on the manipulation of Registry.<br>
 * 但是注意，它不保证对Registry的操作是多线程安全的。
 * 
 * @author Zhengmao HU (James)
 *
 */
public class RegistryUtility {
	static public final String NAME_ENCODERS = "encoders";
	static public final String NAME_DECODERS = "decoders";
	
	/**
	 * Gets CombinedRegistry from CamelContext.<br>
	 * 从CamelContext中得到CombinedRegistry类型的Registry。
	 * 
	 * @param camelContext	The CamelContext must be based on CombinedRegistry, otherwise ClassCastException will be thrown.<br>
	 * 						这个context必须是采用CombinedRegistry类型的Registry的，否则会抛出格式转换异常。
	 * @return	The Registry that is of CombinedRegistry type.
	 */
	static public CombinedRegistry getCombinedRegistry(CamelContext camelContext){
		Registry reg = camelContext.getRegistry();
		CombinedRegistry registry = null;
		if (reg instanceof PropertyPlaceholderDelegateRegistry){
			registry = (CombinedRegistry) ((PropertyPlaceholderDelegateRegistry)reg).getRegistry();
		}else{ // should not go here
			registry = (CombinedRegistry) reg;
		}
		return registry;
	}
	
	/**
	 * Adds an Netty encoder to Registry.<br>
	 * 向Registry中增加一个给Netty用的encoder。
	 * @param context	The CamelContext must be based on CombinedRegistry, otherwise ClassCastException will be thrown.<br>
	 * 					这个context必须是采用CombinedRegistry类型的Registry的，否则会抛出格式转换异常。
	 * @param name		Name of the encoder in Registry.<br>
	 * 					encoder在Registry中的名字。
	 * @param encoder	The encoder that will be used by Netty.<br>
	 * 					将被Netty用到的encoder。
	 */
	@SuppressWarnings("unchecked")
	static public void addEncoder(CamelContext context, String name, ChannelDownstreamHandler encoder){
		CombinedRegistry registry = getCombinedRegistry(context);
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
				throw new IllegalArgumentException("Preserved name '" + NAME_ENCODERS + "' is already being used by others in at least one of the registries.");
			}
		}
		encoders.add(encoder);
	}

	/**
	 * Adds an Netty decoder to Registry.<br>
	 * 向Registry中增加一个给Netty用的decoder。
	 * @param context	The CamelContext must be based on CombinedRegistry, otherwise ClassCastException will be thrown.<br>
	 * 					这个context必须是采用CombinedRegistry类型的Registry的，否则会抛出格式转换异常。
	 * @param name		Name of the decoder in Registry.<br>
	 * 					decoder在Registry中的名字。
	 * @param decoder	The decoder that will be used by Netty.<br>
	 * 					将被Netty用到的decoder。
	 */
	@SuppressWarnings("unchecked")
	static public void addDecoder(CamelContext context, String name, ChannelUpstreamHandler decoder){
		CombinedRegistry registry = getCombinedRegistry(context);
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
				throw new IllegalArgumentException("Preserved name '" + NAME_DECODERS + "' is already being used by others in at least one of the registries.");
			}
		}
		decoders.add(decoder);
	}
	
	/**
	 * Adds codec to Registry only, it will not handle the manipulating of encoders or decoders list in Registry.<br>
	 * 仅仅把codec直接加入到Registry中，而不处理加入到Registry里的encoders或decoders列表中。
	 * <p>
	 * If a codec with the same name already exists in the Registry, IllegalArgumentException will be thrown.<br>
	 * 如果Registry中原先已经有同名的别的codec，则抛出IllegalArgumentException。
	 * 
	 * @param registry	The CombinedRegistry that the codec will be added into.<br>
	 * 					codec将要被加入的CombinedRegistry。
	 * @param name		Name of the codec in Registry.<br>
	 * 					codec在Registry中的名字。
	 * @param codec		The codec that will be used by Netty.<br>
	 * 					将被Netty使用的codec。
	 */
	static protected void addCodecOnly(CombinedRegistry registry, String name, ChannelHandler codec){
		Object oldValue = registry.lookup(name);
		if (oldValue != null && oldValue != codec){
			throw new IllegalArgumentException("Codec name '" + name + "' is already in use in at least one of the registries.");
		}
		registry.getDefaultSimpleRegistry().put(name, codec);
	}
	
}
