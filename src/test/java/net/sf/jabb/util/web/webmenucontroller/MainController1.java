package net.sf.jabb.util.web.webmenucontroller;

import net.sf.jabb.util.web.WebMenu;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/")
@WebMenu(value="Main Menu", order=-1)
public class MainController1 {

	@RequestMapping("/sub1")
	@WebMenu(value="SubMenu1", order=30)
	public String sub1(){
		return null;
	}
	@RequestMapping("/sub2")
	@WebMenu(value="SubMenu2", order=30)
	public String sub2(){
		return null;
	}
	@RequestMapping("/dyn")
	@WebMenu(value="Dynamic", order=-1, dynamic=true)
	public String dyn(){
		return null;
	}
}
