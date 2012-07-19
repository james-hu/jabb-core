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
package net.sf.jabb.util.vfs;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.commons.vfs2.FileObject;
import org.apache.commons.vfs2.FileSystemException;
import org.apache.commons.vfs2.FileSystemManager;
import org.apache.commons.vfs2.FileSystemOptions;
import org.apache.commons.vfs2.auth.StaticUserAuthenticator;
import org.apache.commons.vfs2.impl.DefaultFileSystemManager;
import org.apache.commons.vfs2.impl.StandardFileSystemManager;
import org.apache.commons.vfs2.provider.http.HttpFileSystemConfigBuilder;

/**
 * Wrapper on top of commons-vfs.
 * 
 * @author James Hu
 *
 */
public class VfsUtility {
	private static final Log log = LogFactory.getLog(VfsUtility.class);
	
	/**
	 * Get a new instance of FileSystemManager.
	 * @return	an instance of FileSystemManager
	 */
	static public FileSystemManager getManager(){
		StandardFileSystemManager fsManager = new StandardFileSystemManager();
		try {
			fsManager.init();
		} catch (FileSystemException e) {
			log.error("Cannot initialize StandardFileSystemManager.", e);
		}
		return fsManager;
	}
	
	/**
	 * Close the FileSystemManager.
	 * @param fsManager the file system to be closed. It can be null.
	 */
	static public void close(FileSystemManager fsManager){
		if (fsManager != null){
			if (fsManager instanceof DefaultFileSystemManager){
				((DefaultFileSystemManager) fsManager).close();
			}else{
				throw new IllegalStateException("Only instance of DefaultFileSystemManager can be closed here.");
			}
		}
	}
	
	/**
	 * Close the FileObject.
	 * @param fo the FileObject to be closed. It can be null.
	 */
	static public void close(FileObject fo){
		if (fo != null){
			try {
				fo.close();
			} catch (FileSystemException e) {
				log.debug("Exception when closing FileObject: " + fo.getName(), e);
			}
		}
	}
	
	/**
	 * Close FileObject(s)
	 * @param fos FileObject(s) to be closed.
	 */
	static public void close(FileObject... fos){
		for (FileObject fo: fos){
			close(fo);
		}
	}
	
	/**
	 * Close both FileObject and FileSystemManager.
	 * @param fsManager	 The FileSystemManager to be closed. It can be null.
	 * @param fo	The FileObject to be closed. It can be null.
	 */
	static public void close(FileSystemManager fsManager, FileObject fo){
		close(fo);
		close(fsManager);
	}
	
	/**
	 * Close both FileObject(s) and FileSystemManager.
	 * @param fsManager	 The FileSystemManager to be closed. It can be null.
	 * @param fo	The FileObjects to be closed. It can contain null.
	 */
	static public void close(FileSystemManager fsManager, FileObject... fos){
		close(fos);
		close(fsManager);
	}
	
	/**
	 * Configure FileSystemOptions for HttpFileSystem
	 * @param fsOptions
	 * @param webProxyHost
	 * @param webProxyPort
	 * @param webProxyUserName
	 * @param webProxyPassword
	 */
	static public void configHttpFileSystemProxy(FileSystemOptions fsOptions, 
			String webProxyHost, Integer webProxyPort, String webProxyUserName, String webProxyPassword){
		if (webProxyHost != null && webProxyPort != null){
			HttpFileSystemConfigBuilder.getInstance().setProxyHost(fsOptions, webProxyHost);
			HttpFileSystemConfigBuilder.getInstance().setProxyPort(fsOptions, webProxyPort);
			if (webProxyUserName != null){
				StaticUserAuthenticator auth = new StaticUserAuthenticator(webProxyUserName, webProxyPassword, null); 
				HttpFileSystemConfigBuilder.getInstance().setProxyAuthenticator(fsOptions, auth);
			}
		}

	}
	
}
