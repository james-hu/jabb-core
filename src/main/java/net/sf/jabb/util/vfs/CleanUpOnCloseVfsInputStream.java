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

import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.vfs2.FileObject;
import org.apache.commons.vfs2.FileSystemException;
import org.apache.commons.vfs2.FileSystemManager;

/**
 * Cleans up when got closed.
 * @author James Hu
 *
 */
public class CleanUpOnCloseVfsInputStream extends InputStream {
	protected FileSystemManager fsManager;
	protected FileObject file;
	protected InputStream inputStream;
	
	/**
	 * Encapsulate the original InputStream of a file.
	 * @param file		the file whose input stream will be opened and encapsulated.
	 * @param fsManager the file manager that will be closed when the input stream got closed.
	 * @throws FileSystemException
	 */
	public CleanUpOnCloseVfsInputStream(FileObject file, FileSystemManager fsManager) throws FileSystemException{
		this.file = file;
		this.fsManager = fsManager;
		this.inputStream = file.getContent().getInputStream();
	}

	/* (non-Javadoc)
	 * @see java.io.InputStream#read()
	 */
	@Override
	public int read() throws IOException {
		return inputStream.read();
	}
	
	@Override
	public int read(byte[] b) throws IOException{
		return inputStream.read(b);
	}
	
	@Override
	public int read(byte[] b, int off, int len) throws IOException{
		return inputStream.read(b, off, len);
	}
	
	@Override
	public long skip(long n) throws IOException{
		return inputStream.skip(n);
	}
	
	@Override
	public int available() throws IOException{
		return inputStream.available();
	}
	
	@Override
	public void close() throws IOException{
		try{
			inputStream.close();
		}catch(IOException ioe){
			throw ioe;
		}finally{
			VfsUtility.close(fsManager, file);
		}
	}
	
	@Override
	public void mark(int readlimit){
		inputStream.mark(readlimit);
	}
	
	@Override
	public void reset() throws IOException{
		inputStream.reset();
	}
	
	@Override
	public boolean markSupported(){
		return inputStream.markSupported();
	}
	

}
