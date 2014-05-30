package net.sf.jabb.util.web.webmenucontroller;

import net.sf.jabb.util.web.WebMenu;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping
public class HomeController {

	@RequestMapping("/home1")
	@WebMenu("Home1")
	public String home(){
		return null;
	}
	
	@RequestMapping("/dynamic")
	@WebMenu(value="HomeDynamic", dynamic=true, order=999)
	public String homeDynamic(){
		return null;
	}

}
