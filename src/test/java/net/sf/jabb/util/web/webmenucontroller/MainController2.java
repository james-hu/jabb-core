package net.sf.jabb.util.web.webmenucontroller;

import net.sf.jabb.util.web.WebMenu;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/")
@WebMenu
public class MainController2 {

	@RequestMapping("/sub3")
	@WebMenu(value="SubMenu3", order=40)
	public String sub3(){
		return null;
	}
	@RequestMapping("/sub4")
	@WebMenu(value="SubMenu4", order=50)
	public String sub4(){
		return null;
	}
}
