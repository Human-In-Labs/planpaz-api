package com.humanin.planpaz.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/user")
public class UserController {
	@GetMapping
	public ResponseEntity<String> getUser() {
		System.out.println("[SUCESS] Usuário autenticado com token válido.");
		return ResponseEntity.ok("Sucesso!");
	}
}
