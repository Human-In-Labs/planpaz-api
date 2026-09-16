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

	// ==========================
	// LINK CLICADO NO E-MAIL DE VERIFICAÇÃO (TEMPORARIAMENTE DESATIVADO)
	// ==========================

	/*
	 * @GetMapping("/verify-email") public ResponseEntity<String>
	 * verifyEmail(@RequestParam String token) { try {
	 * authService.verifyEmail(token); return ResponseEntity.ok()
	 * .contentType(MediaType.TEXT_HTML)
	 * .body(paginaDeConfirmacao("E-mail verificado! 🌱",
	 * "Sua conta foi confirmada com sucesso. Você já pode voltar ao app e fazer login."
	 * )); } catch (BusinessException e) { return
	 * ResponseEntity.status(HttpStatus.BAD_REQUEST)
	 * .contentType(MediaType.TEXT_HTML)
	 * .body(paginaDeConfirmacao("Não foi possível verificar 😕", e.getMessage()));
	 * } }
	 */

	// ==========================
	// REENVIA O LINK DE VERIFICAÇÃO (TEMPORARIAMENTE DESATIVADO)
	// ==========================

	/*
	 * @PostMapping("/resend-verification") public ResponseEntity<String>
	 * resendVerification(@RequestParam String email) {
	 * authService.resendVerification(email); return ResponseEntity.
	 * ok("Link de verificação reenviado. Confira sua caixa de entrada."); }
	 */

	/*
	 * // Página HTML simples exibida ao clicar no link do e-mail private String
	 * paginaDeConfirmacao(String titulo, String mensagem) { return
	 * "<!DOCTYPE html><html lang='pt-BR'><head><meta charset='UTF-8'>" +
	 * "<title>PlanPaz</title>" +
	 * "<style>body{font-family:Arial,sans-serif;background:#0f172a;color:#f8fafc;"
	 * +
	 * "display:flex;align-items:center;justify-content:center;height:100vh;margin:0;text-align:center;}"
	 * + ".box{max-width:420px;padding:2rem;} h1{color:#10b981;font-size:1.4rem;}" +
	 * "p{color:#94a3b8;line-height:1.5;}</style></head>" +
	 * "<body><div class='box'><h1>" + titulo + "</h1><p>" + mensagem +
	 * "</p></div></body></html>"; }
	 */
}