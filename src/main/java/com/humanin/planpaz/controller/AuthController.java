package com.humanin.planpaz.controller;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.humanin.planpaz.dto.LoginRequestDTO;
import com.humanin.planpaz.dto.RegisterRequestDTO;
import com.humanin.planpaz.dto.ResponseDTO;
import com.humanin.planpaz.infra.security.TokenService;
import com.humanin.planpaz.model.User;
import com.humanin.planpaz.repositories.UserRepository;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor  // é a mesma coisa que colocar @Autowired em todas dependências
public class AuthController {
	// dependências
	private final UserRepository repository;
	private final PasswordEncoder passwordEncoder;
	private final TokenService tokenService;
	
	@PostMapping("/login")
	public ResponseEntity<ResponseDTO> login(@RequestBody LoginRequestDTO body) {
		User user = this.repository.findByEmail(body.email()).orElseThrow(() -> new RuntimeException("User not found"));  // aprender a tratar as exceções melhor
		System.out.println("[WARN] Tentativa de login no usuário " + user.getEmail() + ".");
		if (passwordEncoder.matches(body.password(), user.getPassword())) {
			System.out.println("[SUCESS] Senha autenticada com sucesso.");
			String token = this.tokenService.generateToken(user);
			return ResponseEntity.ok(new ResponseDTO(user.getName(), token));
		}
		System.out.println("[ERROR] As senhas não batem.");
		return ResponseEntity.badRequest().build();
		
	}
	
	
	@PostMapping("/register")
	public ResponseEntity<ResponseDTO> register(@RequestBody RegisterRequestDTO body) {
		// checagem se já existe
		Optional<User> user = this.repository.findByEmail(body.email());
		
		if (user.isEmpty()) {
			System.out.println("[WARN] Registro de usuário novo.");
			User newUser = new User();
			newUser.setPassword(passwordEncoder.encode(body.password()));
			newUser.setEmail(body.email());
			newUser.setName(body.name());
			this.repository.save(newUser); 
			
			String token = this.tokenService.generateToken(newUser);
			
			System.out.println("[SUCESS] Novo usuário " + newUser.getEmail() + " criado, com token autorizado.");
			return ResponseEntity.ok(new ResponseDTO(newUser.getName(), token));
		}
		
		System.out.println("[ERROR] Tentativa de registro de usuário já existente.");
		return ResponseEntity.badRequest().build();
		
	}
	
}
