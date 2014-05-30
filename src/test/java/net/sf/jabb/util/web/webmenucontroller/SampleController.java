package net.sf.jabb.util.web.webmenucontroller;

import net.sf.jabb.util.web.WebMenu;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/sample")
@WebMenu(value="Sample", order=90)
public class SampleController {

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
	@WebMenu("Sample4")
	public String sample4(){
		return null;
	}
}
