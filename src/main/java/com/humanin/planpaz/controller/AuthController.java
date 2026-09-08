package com.humanin.planpaz.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.humanin.planpaz.dto.LoginRequestDTO;
import com.humanin.planpaz.dto.RegisterRequestDTO;
import com.humanin.planpaz.dto.ResponseDTO;
import com.humanin.planpaz.service.AuthService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
	private final AuthService authService;

	// ==========================
	// REALIZA O LOGIN E RETORNA UM TOKEN JWT
	// ==========================

	@PostMapping("/login")
	public ResponseEntity<ResponseDTO> login(@RequestBody LoginRequestDTO body) {
		ResponseDTO response = authService.login(body);
		return ResponseEntity.ok(response);
	}

	// ==========================
	// REALIZA O PRIMEIRO CADASTRO COM DADOS OBRIGATÓRIOS E RETORNA UM TOKEN JWT
	// ==========================

	@PostMapping("/register")
	public ResponseEntity<ResponseDTO> register(@RequestBody RegisterRequestDTO body) {
		ResponseDTO response = authService.register(body);
		return ResponseEntity.ok(response);
	}
}