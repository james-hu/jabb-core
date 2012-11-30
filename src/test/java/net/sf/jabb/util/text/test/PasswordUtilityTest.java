package net.sf.jabb.util.text.test;

import static org.junit.Assert.*;

import net.sf.jabb.util.text.MaskedText;
import net.sf.jabb.util.text.PasswordUtility;

import org.junit.Test;

public class PasswordUtilityTest {

	@Test
	public void test() {
		MaskedText mt = null;
		String url = null;
		String pwd = null;
		String mask = "*****";
		
		pwd = "The Password";
		url = "sftp://root:" + pwd + "@1.2.3/opt/abc.html";
		mt = PasswordUtility.maskInUrl(url);
		assertEquals(pwd, mt.getMasked());
		assertEquals(url.replaceFirst(pwd, mask), mt.getText());
		assertEquals(url, mt.getClearText());
		assertEquals(url, PasswordUtility.unmaskInUrl(mt.getText(), pwd));
		assertEquals(url.replaceFirst(pwd, "xyz"), PasswordUtility.unmaskInUrl(mt.getText(), "xyz"));
		
		pwd= "new!:pwd";
		url = "ftp://user:" + pwd + "@news.com/file";
		mt = PasswordUtility.maskInUrl(url);
		assertEquals(pwd, mt.getMasked());
		assertEquals(url.replaceFirst(pwd, mask), mt.getText());
		assertEquals(url, mt.getClearText());
		assertEquals(url, PasswordUtility.unmaskInUrl(mt.getText(), pwd));

		url="http://www.news.com/index.html";
		mt = PasswordUtility.maskInUrl(url);
		assertEquals(null, mt.getMasked());
		assertEquals(url, mt.getText());
		assertEquals(url, mt.getClearText());
		assertEquals(url, PasswordUtility.unmaskInUrl(mt.getText(), "abc"));
		
		url = "ftp://user:new!:p*wd@news.com/folder/new!:p*wd/index.html";
		mt = PasswordUtility.maskInUrl(url);
		assertEquals("new!:p*wd", mt.getMasked());
		assertEquals("ftp://user:*****@news.com/folder/new!:p*wd/index.html", mt.getText());
		assertEquals(url, mt.getClearText());
		assertEquals(url, PasswordUtility.unmaskInUrl(mt.getText(), "new!:p*wd"));
		assertEquals("ftp://user:xyz@news.com/folder/new!:p*wd/index.html", PasswordUtility.unmaskInUrl(mt.getText(), "xyz"));
		assertEquals("ftp://user@news.com/folder/new!:p*wd/index.html", PasswordUtility.unmaskInUrl(mt.getText(), null));
		assertEquals("ftp://user:@news.com/folder/new!:p*wd/index.html", PasswordUtility.unmaskInUrl(mt.getText(), ""));
		
		url = null;
		mt = PasswordUtility.maskInUrl(url);
		assertNotNull(mt);
		assertNull(mt.getText());
		assertNull(mt.getMasked());
		assertNull(mt.getClearText());
		assertNull(PasswordUtility.unmaskInUrl(null, null));
		assertNull(PasswordUtility.unmaskInUrl(null, ""));
		assertNull(PasswordUtility.unmaskInUrl(null, "abc"));

		url = "";
		mt = PasswordUtility.maskInUrl(url);
		assertNotNull(mt);
		assertEquals("", mt.getText());
		assertNull(mt.getMasked());
		assertEquals("", mt.getClearText());
		assertEquals("", PasswordUtility.unmaskInUrl("", null));
		assertEquals("", PasswordUtility.unmaskInUrl("", ""));
		assertEquals("", PasswordUtility.unmaskInUrl("", "abc"));

	}

}
