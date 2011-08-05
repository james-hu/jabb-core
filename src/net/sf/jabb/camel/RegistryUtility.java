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
 * �ṩ����ض�Camel��Registry���в����ķ����������󲿷ֵ�ʱ��ֻ��CombinedRegistry�ű�֧�֡�
 * <p>
 * For example, it provides methods to add Codec(s) required by Netty to Registry.<br>
 * ����˵�����ṩ��Registry�����Netty�����Codec�ķ�����
 * <p>
 * But you should be aware that it does not ensure multi-threads safe on the manipulation of Registry.<br>
 * ����ע�⣬������֤��Registry�Ĳ����Ƕ��̰߳�ȫ�ġ�
 * 
 * @author Zhengmao HU (James)
 *
 */
public class RegistryUtility {
	static public final String NAME_ENCODERS = "encoders";
	static public final String NAME_DECODERS = "decoders";
	
	/**
	 * Gets CombinedRegistry from CamelContext.<br>
	 * ��CamelContext�еõ�CombinedRegistry���͵�Registry��
	 * 
	 * @param camelContext	The CamelContext must be based on CombinedRegistry, otherwise ClassCastException will be thrown.<br>
	 * 						���context�����ǲ���CombinedRegistry���͵�Registry�ģ�������׳���ʽת���쳣��
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
	 * ��Registry������һ����Netty�õ�encoder��
	 * @param context	The CamelContext must be based on CombinedRegistry, otherwise ClassCastException will be thrown.<br>
	 * 					���context�����ǲ���CombinedRegistry���͵�Registry�ģ�������׳���ʽת���쳣��
	 * @param name		Name of the encoder in Registry.<br>
	 * 					encoder��Registry�е����֡�
	 * @param encoder	The encoder that will be used by Netty.<br>
	 * 					����Netty�õ���encoder��
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
	 * ��Registry������һ����Netty�õ�decoder��
	 * @param context	The CamelContext must be based on CombinedRegistry, otherwise ClassCastException will be thrown.<br>
	 * 					���context�����ǲ���CombinedRegistry���͵�Registry�ģ�������׳���ʽת���쳣��
	 * @param name		Name of the decoder in Registry.<br>
	 * 					decoder��Registry�е����֡�
	 * @param decoder	The decoder that will be used by Netty.<br>
	 * 					����Netty�õ���decoder��
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
	 * ������codecֱ�Ӽ��뵽Registry�У�����������뵽Registry���encoders��decoders�б��С�
	 * <p>
	 * If a codec with the same name already exists in the Registry, IllegalArgumentException will be thrown.<br>
	 * ���Registry��ԭ���Ѿ���ͬ���ı��codec�����׳�IllegalArgumentException��
	 * 
	 * @param registry	The CombinedRegistry that the codec will be added into.<br>
	 * 					codec��Ҫ�������CombinedRegistry��
	 * @param name		Name of the codec in Registry.<br>
	 * 					codec��Registry�е����֡�
	 * @param codec		The codec that will be used by Netty.<br>
	 * 					����Nettyʹ�õ�codec��
	 */
	static protected void addCodecOnly(CombinedRegistry registry, String name, ChannelHandler codec){
		Object oldValue = registry.lookup(name);
		if (oldValue != null && oldValue != codec){
			throw new IllegalArgumentException("Codec name '" + name + "' is already in use in at least one of the registries.");
		}
		registry.getDefaultSimpleRegistry().put(name, codec);
	}
	
}
