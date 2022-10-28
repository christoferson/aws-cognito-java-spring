package demo.controllers;

import java.util.Locale;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class SercuredPageController {

	@GetMapping("/secured")
	public String home(Locale locale, Model model) {
		return "secured";
	}
	
}
