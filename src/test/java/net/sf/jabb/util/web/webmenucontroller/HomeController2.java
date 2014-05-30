package net.sf.jabb.util.web.webmenucontroller;

import net.sf.jabb.util.web.WebMenu;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping
public class HomeController2 {

	@RequestMapping("/home2")
	@WebMenu(value="Home2", order=30)
	public String home(){
		return null;
	}
}
