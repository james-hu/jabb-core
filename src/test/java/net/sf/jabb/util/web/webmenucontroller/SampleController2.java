package net.sf.jabb.util.web.webmenucontroller;

import net.sf.jabb.util.web.WebMenu;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/sample2")
@WebMenu(value="Sample2", order=99)
public class SampleController2 {

	@RequestMapping("/sample1")
	@WebMenu("Sample1")
	public String sample1(){
		return null;
	}
	@RequestMapping("/sample2")
	@WebMenu("Sample2")
	public String sample2(){
		return null;
	}
	@RequestMapping("/sample3")
	@WebMenu("Sample3")
	public String sample3(){
		return null;
	}
	@RequestMapping("/sample4")
	@WebMenu(value="Sample4", dynamic=true, order=100)
	public String sample4(){
		return null;
	}
}
