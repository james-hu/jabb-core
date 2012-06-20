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

package net.sf.jabb.util.context;

import java.io.File;
import java.util.Hashtable;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.naming.NoInitialContextException;
import javax.naming.Reference;

/**
 * 找到InitialContext，如果存在则返回缺省的，如果不存在则创建并返回一个
 * com.sun.jndi.fscontext.RefFSContextFactory。
 * <p>
 * Finds InitialContext. If there is one, then return the one. If not,
 * then create a one of type com.sun.jndi.fscontext.RefFSContextFactory and return it.
 * 
 * @author Zhengmao HU (James)
 *
 */
public class InitialContextFinder {
	protected static boolean found = false; 
	
	/**
	 * 找到现成的。
	 * <p>
	 * Finds the existing one.
	 * 
	 * @param environment
	 * @return the default context
	 * @throws NamingException
	 */
	public static InitialContext findDefault(Hashtable<?,?> environment) throws NamingException{
		return new InitialContext(environment);
	}
	
	/**
	 * 找到现成的。
	 * <p>
	 * Finds the existing one.
	 * 
	 * @return the default context
	 * @throws NamingException
	 */
	public static InitialContext findDefault() throws NamingException{
		return findDefault(null);
	}
	
	/**
	 * 找到现成的的或创建一个。
	 * <p>
	 * Finds the existing one or create a new one.
	 * 
	 * @param environment
	 * @return the context found or created
	 * @throws NamingException
	 */
	public static InitialContext findOrCreate(Hashtable<?,?> environment) throws NamingException{
		if (found){
			return findDefault(environment);
		}
		
		InitialContext ctx = null;
		NamingException lastE = null;
		
		synchronized (InitialContextFinder.class){
			// get and test if the initial context is usable
			try {
				ctx = findDefault(environment);
			} catch (NamingException e) {
				lastE = e;
			}
			if (ctx != null){
				try {
					@SuppressWarnings("unused")
					Object o = ctx.lookup(InitialContextFinder.class.getName());
				} catch (NoInitialContextException e) {
					ctx = null;
				} catch (NamingException e) {
					lastE = e;
				}
			}
			
			// create one if needed
			if (ctx == null){
				try {
					ctx = createFSContext();
				} catch (NamingException e) {
					lastE = e;
				}
			}
			
			if (ctx != null){
				found = true;
				return ctx;
			}else{
				if (lastE != null){
					throw lastE;
				}else{
					throw new NamingException("Can't find InitialContext.");
				}
			}
		}
		
	}
	
	/**
	 * 找到现成的的或创建一个。
	 * <p>
	 * Finds the existing one or create a new one.
	 * 
	 * @return the context found or created
	 * @throws NamingException
	 */
	public static InitialContext findOrCreate() throws NamingException{
		return findOrCreate(null);
	}

	
	protected static InitialContext createFSContext() throws NamingException{
		File tempDir = null;
		try{
			tempDir = File.createTempFile("InitialContextFinder", "Temp");
			tempDir.delete();
			tempDir.mkdir();
			tempDir.deleteOnExit();
			
			System.setProperty(Context.INITIAL_CONTEXT_FACTORY,    
				"com.sun.jndi.fscontext.RefFSContextFactory");   
			System.setProperty(Context.PROVIDER_URL, tempDir.toURI().toURL().toString());
		}catch (Exception e){
			throw new NamingException("Can't prepare environment for com.sun.jndi.fscontext.RefFSContextFactory: " + e.getMessage());
		}
		
		InitialContext ic = new InitialContext();
		
		// setup for auto-deletion of files
		Reference ref = new Reference(Object.class.getName());
		ic.rebind(InitialContextFinder.class.getName() + "/test", ref);
		for (File f: tempDir.listFiles()){
			f.deleteOnExit();
		}

		return ic;
	}

}
