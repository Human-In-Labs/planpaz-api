package com.humanin.planpaz.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.humanin.planpaz.model.Achievement;
import com.humanin.planpaz.model.User;
import com.humanin.planpaz.model.UserAchievement;
import com.humanin.planpaz.service.AchievementService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/achievements")
@RequiredArgsConstructor
public class AchievementController {

	private final AchievementService achievementService;

	private User getAuthenticatedUser(Authentication authentication) {
		return (User) authentication.getPrincipal();
	}

	// LISTAR CONQUISTAS DO USUÁRIO
	@GetMapping
	public ResponseEntity<List<UserAchievement>> listar(Authentication authentication) {
		User user = getAuthenticatedUser(authentication);
		List<UserAchievement> conquistas = achievementService.obterConquistasDoUsuario(user.getId());
		return ResponseEntity.ok(conquistas);
	}

	// LISTAR TODAS AS CONQUISTAS DO SISTEMA (CATÁLOGO)
	@GetMapping("/catalog")
	public ResponseEntity<List<Achievement>> listarCatalogo() {
		List<Achievement> todas = achievementService.listarTodas();
		return ResponseEntity.ok(todas);
	}

	// ENDPOINTS DE TESTE MANUAL
	@PostMapping("/cultivar-3")
	public ResponseEntity<Void> concederCultivar3(Authentication authentication) {
		User user = getAuthenticatedUser(authentication);
		achievementService.concederCultivar3Plantas(user.getId());
		return ResponseEntity.ok().build();
	}

	@PostMapping("/cultivar-5")
	public ResponseEntity<Void> concederCultivar5(Authentication authentication) {
		User user = getAuthenticatedUser(authentication);
		achievementService.concederCultivar5Plantas(user.getId());
		return ResponseEntity.ok().build();
	}

	@PostMapping("/cultivar-10")
	public ResponseEntity<Void> concederCultivar10(Authentication authentication) {
		User user = getAuthenticatedUser(authentication);
		achievementService.concederCultivar10Plantas(user.getId());
		return ResponseEntity.ok().build();
	}

	@PostMapping("/cuidar-3-dias")
	public ResponseEntity<Void> concederCuidar3(Authentication authentication) {
		User user = getAuthenticatedUser(authentication);
		achievementService.concederCuidarPlanta3Dias(user.getId());
		return ResponseEntity.ok().build();
	}

	@PostMapping("/cuidar-5-dias")
	public ResponseEntity<Void> concederCuidar5(Authentication authentication) {
		User user = getAuthenticatedUser(authentication);
		achievementService.concederCuidarPlanta5Dias(user.getId());
		return ResponseEntity.ok().build();
	}

	@PostMapping("/cuidar-10-dias")
	public ResponseEntity<Void> concederCuidar10(Authentication authentication) {
		User user = getAuthenticatedUser(authentication);
		achievementService.concederCuidarPlanta10Dias(user.getId());
		return ResponseEntity.ok().build();
	}

	@PostMapping("/estagio-crescimento")
	public ResponseEntity<Void> concederCrescimento(Authentication authentication) {
		User user = getAuthenticatedUser(authentication);
		achievementService.concederEstagioCrescimento(user.getId());
		return ResponseEntity.ok().build();
	}

	@PostMapping("/estagio-colheita")
	public ResponseEntity<Void> concederColheita(Authentication authentication) {
		User user = getAuthenticatedUser(authentication);
		achievementService.concederEstagioColheitaOuFloracao(user.getId());
		return ResponseEntity.ok().build();
	}
}