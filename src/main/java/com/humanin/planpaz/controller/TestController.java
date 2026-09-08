package com.humanin.planpaz.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class TestController {
	// ==========================
	// PÁGINA DE TEST MONOLÍTICA PARA TESTAR AS REQUISIÇÕES VISUALMENTE
	// ==========================
	
	@GetMapping("/test")
	public String test() {
		return "forward:/test.html";
	}
}