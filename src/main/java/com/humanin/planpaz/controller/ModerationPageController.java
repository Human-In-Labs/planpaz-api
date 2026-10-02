package com.humanin.planpaz.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ModerationPageController {

	// ==========================
	// PÁGINA DO MÓDULO DE MODERAÇÃO (/moderation)
	// ==========================

	@GetMapping({"/moderation", "/moderation/", "/moderation/login"})
	public String moderationPage() {
		return "forward:/moderation.html";
	}
}
