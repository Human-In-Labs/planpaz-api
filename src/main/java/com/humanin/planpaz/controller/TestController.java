package com.humanin.planpaz.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import com.humanin.planpaz.model.User;

@Controller
public class TestController {
	@GetMapping("/test")
	public String test() {
		// testing lombok
		User user = new User();
		user.setEmail("aa@gmail.com");
		System.out.println(user.getEmail());
		return "a";
	}
}