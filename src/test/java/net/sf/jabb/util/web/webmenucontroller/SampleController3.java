package net.sf.jabb.util.web.webmenucontroller;

import net.sf.jabb.util.web.WebMenu;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/sample2")
//@WebMenu
public class SampleController3 {

	@RequestMapping("/sample5")
	@WebMenu("Sample5")
	public String sample5(){
		return null;
	}
	
	@RequestMapping("/sample5/sub1")
	@WebMenu("Sample5_Sub1")
	public String sample5sub1(){
		return null;
	}
	@RequestMapping("/sample5/sub2")
	@WebMenu("Sample5_Sub2")
	public String sample5sub2(){
		return null;
	}
	@RequestMapping("/sample6/sub")
	@WebMenu("Sample6_Sub")
	public String sample6sub(){
		return null;
	}
}
