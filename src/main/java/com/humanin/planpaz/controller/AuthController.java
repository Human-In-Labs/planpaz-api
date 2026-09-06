package com.humanin.planpaz.controller;

import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.humanin.planpaz.dto.LoginRequestDTO;
import com.humanin.planpaz.dto.RegisterRequestDTO;
import com.humanin.planpaz.dto.ResponseDTO;
import com.humanin.planpaz.infra.security.TokenService;
import com.humanin.planpaz.model.User;
import com.humanin.planpaz.repositories.UserRepository;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

	private final UserRepository repository;
	private final PasswordEncoder passwordEncoder;
	private final TokenService tokenService;

	@PostMapping("/login")
	public ResponseEntity<ResponseDTO> login(@RequestBody LoginRequestDTO body) {

		System.out.println("========== LOGIN CHEGOU NO CONTROLLER ==========");
		System.out.println("EMAIL RECEBIDO: " + body.email());

		User user = this.repository.findByEmail(body.email())
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

		System.out.println("[WARN] Tentativa de login no usuário " + user.getEmail() + ".");
		if (passwordEncoder.matches(body.password(), user.getPassword())) {

			System.out.println("[SUCCESS] Senha autenticada com sucesso.");
			System.out.println("EMAIL: " + user.getEmail());
			System.out.println("ID: " + user.getId());

			String token = this.tokenService.generateToken(user);

			System.out.println("TOKEN GERADO COM SUCESSO");

			// Retorna o ResponseDTO contendo nome, username e token
			return ResponseEntity.ok(new ResponseDTO(user.getName(), user.getUsername(), token));
		}

		System.out.println("[ERROR] As senhas não batem.");
		return ResponseEntity.badRequest().build();
	}

	@PostMapping("/register")
	public ResponseEntity<ResponseDTO> register(@RequestBody RegisterRequestDTO body) {
		// Checagem se já existe por e-mail
		Optional<User> user = this.repository.findByEmail(body.email());

		if (user.isEmpty()) {
			System.out.println("[WARN] Registro de usuário novo.");
			User newUser = new User();
			newUser.setPassword(passwordEncoder.encode(body.password()));
			newUser.setEmail(body.email());
			newUser.setName(body.name());
			newUser.setUsername(body.username());

			// Campos opcionais
			if (body.bio() != null) newUser.setBio(body.bio());
			if (body.birthdate() != null) newUser.setBirthdate(body.birthdate());
			if (body.gender() != null) newUser.setGender(body.gender());
			if (body.mainGoal() != null) newUser.setMainGoal(body.mainGoal());
			if (body.roomLuminosity() != null) newUser.setRoomLuminosity(body.roomLuminosity());
			if (body.spaceAvailability() != null) newUser.setSpaceAvailability(body.spaceAvailability());
			if (body.experienceLevel() != null) newUser.setExperienceLevel(body.experienceLevel());
			if (body.timeAvailability() != null) newUser.setTimeAvailability(body.timeAvailability());
			if (body.wateringTime() != null) newUser.setWateringTime(body.wateringTime());
			if (body.city() != null) newUser.setCity(body.city());
			if (body.fcmToken() != null) newUser.setFcmToken(body.fcmToken());

			this.repository.save(newUser);

			String token = this.tokenService.generateToken(newUser);

			System.out.println("[SUCCESS] Novo usuário " + newUser.getEmail() + " criado, com token autorizado.");

			// Retorna o ResponseDTO contendo nome, username e token
			return ResponseEntity.ok(new ResponseDTO(newUser.getName(), newUser.getUsername(), token));
		}

		System.out.println("[ERROR] Tentativa de registro de usuário já existente.");
		return ResponseEntity.badRequest().build();
	}
}