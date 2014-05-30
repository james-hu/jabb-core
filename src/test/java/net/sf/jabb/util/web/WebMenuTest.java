package net.sf.jabb.util.web;

import static org.junit.Assert.*;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.ContextHierarchy;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@ContextHierarchy({
    @ContextConfiguration(name = "root",  locations = "classpath:net/sf/jabb/util/web/webmenutest.xml"),
})
public class WebMenuTest {

	@Test
	public void test() {
		/*
		 Check the log and you should be able to see the output like:
		 
		 
"Main Menu"(null)
  "Dynamic"(/dyn) [Dynamic]
  "SubMenu1"(/sub1)
  "SubMenu2"(/sub2)
  "SubMenu3"(/sub3)
  "SubMenu4"(/sub4)
"Home1"(/home1)
"Home2"(/home2)
"Sample"(null)
  "Sample1"(/sample/sample1)
  "Sample2"(/sample/sample2)
  "Sample3"(/sample/sample3)
  "Sample4"(/sample/sample4)
"Sample2"(null)
  "Sample1"(/sample2/sample1)
  "Sample2"(/sample2/sample2)
  "Sample3"(/sample2/sample3)
  "Sample5"(null)
    "Sample5_Sub1"(/sample2/sample5/sub1)
    "Sample5_Sub2"(/sample2/sample5/sub2)
  "Sample6_Sub"(/sample2/sample6/sub)
  "Sample4"(/sample2/sample4) [Dynamic]
"HomeDynamic"(/dynamic) [Dynamic]
		 */
	}

}
