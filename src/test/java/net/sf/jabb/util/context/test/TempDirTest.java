package net.sf.jabb.util.context.test;

import java.io.File;
import java.io.IOException;

import org.junit.Test;


public class TempDirTest {
	//@Test
	public void tempDir() throws IOException{
		File f = File.createTempFile("myhead", "myext");
		System.out.println(f.getCanonicalPath());
		f.delete();
		f.mkdir();
		System.out.println(f.getCanonicalPath());
		f.deleteOnExit();
		
		File f2 = new File(f, ".test");
		f2.createNewFile();
		System.out.println(f2.getCanonicalPath());
		f2.deleteOnExit();
	}

}
