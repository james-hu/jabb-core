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
 * �ṩ�������CombinedRegistry�����Netty�����Codec�ķ�����
 * ����ע�⣬������֤��Registry�Ĳ�����ͬ���ġ�
 * 
 * @author Zhengmao HU (James)
 *
 */
public class RegistryUtility {
	static public final String NAME_ENCODERS = "encoders";
	static public final String NAME_DECODERS = "decoders";
	
	/**
	 * ��Registry������һ����Netty�õ�encoder��
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
	 * ��Registry������һ����Netty�õ�decoder��
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
	 * ������codecֱ�Ӽ��뵽Registry�У�����������뵽Registry���encoders��decoders�б��С�
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
