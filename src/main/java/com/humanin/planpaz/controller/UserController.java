package com.humanin.planpaz.controller;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.humanin.planpaz.dto.ApiResponseDTO;
import com.humanin.planpaz.dto.PublicUserProfileDTO;
import com.humanin.planpaz.dto.UserPreferencesDTO;
import com.humanin.planpaz.dto.UserSettingsDTO;
import com.humanin.planpaz.dto.UserStatsDTO;
import com.humanin.planpaz.dto.UserSummaryDTO;
import com.humanin.planpaz.model.User;
import com.humanin.planpaz.service.UserService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping({ "/api/user", "/api/users" })
@RequiredArgsConstructor
public class UserController {

	private final UserService userService;

	// ==========================
	// TESTE: SE O USUÁRIO TIVER UM TOKEN VÁLIDO ELE VAI PODE ACESSAR ESSE ENDPOINT
	// ==========================

	@GetMapping
	public ResponseEntity<String> getUser() {
		System.out.println("[SUCESS] Usuário autenticado com token válido.");
		return ResponseEntity.ok("Sucesso!");
	}

	// ==========================
	// RETORNA ESTATÍSTICAS DO USUÁRIO AUTENTICADO
	// ==========================

	@GetMapping("/stats")
	public ResponseEntity<UserStatsDTO> getMyStats(@AuthenticationPrincipal User currentUser) {
		UserStatsDTO stats = userService.getUserStats(currentUser.getId());
		return ResponseEntity.ok(stats);
	}

	// ==========================
	// RETORNA ESTATÍSTICAS DO USUÁRIO POR ID
	// ==========================

	@GetMapping("/{id}/stats")
	public ResponseEntity<UserStatsDTO> getUserStatsById(@PathVariable UUID id) {
		UserStatsDTO stats = userService.getUserStats(id);
		return ResponseEntity.ok(stats);
	}

	// ==========================
	// RETORNA TODOS OS ATRIBUTOS DO USER
	// ==========================

	@GetMapping("/settings")
	public ResponseEntity<UserSettingsDTO> getSettings(@AuthenticationPrincipal User currentUser) {
		UserSettingsDTO settings = userService.getUserSettings(currentUser.getId());
		return ResponseEntity.ok(settings);
	}

	// ==========================
	// ATUALIZA AS PREFERÊNCIAS OPCIONAIS DO USUÁRIO
	// ==========================

	@PutMapping("/preferences")
	public ResponseEntity<UserSettingsDTO> updatePreferences(@AuthenticationPrincipal User currentUser,
			@RequestBody UserPreferencesDTO preferences) {
		UserSettingsDTO updated = userService.updatePreferences(currentUser.getId(), preferences);
		return ResponseEntity.ok(updated);
	}

	// ==========================
	// ATUALIZA TODAS AS CONFIGURAÇÕES DO USUÁRIO
	// ==========================

	@PutMapping("/settings")
	public ResponseEntity<UserSettingsDTO> updateSettings(@AuthenticationPrincipal User currentUser,
			@RequestBody UserSettingsDTO settings) {
		UserSettingsDTO updated = userService.updateSettings(currentUser.getId(), settings);
		return ResponseEntity.ok(updated);
	}

	// ==========================
	// VERIFICA DISPONIBILIDADE DO USERNAME
	// ==========================

	@GetMapping("/check-username")
	public ResponseEntity<Map<String, Boolean>> checkUsernameAvailability(@RequestParam String username) {
		boolean exists = userService.existsByUsername(username);
		return ResponseEntity.ok(Map.of("exists", exists, "available", !exists));
	}

	// ==========================
	// PESQUISA USUÁRIOS PELO USERNAME
	// ==========================

	@GetMapping("/search")
	public ResponseEntity<List<UserSummaryDTO>> searchUsers(@RequestParam String username) {
		List<UserSummaryDTO> users = userService.searchByUsername(username);
		return ResponseEntity.ok(users);
	}

	// ==========================
	// SEGUE UM USUÁRIO POR ID
	// ==========================

	@PostMapping("/{id}/follow")
	public ResponseEntity<ApiResponseDTO> followUser(@AuthenticationPrincipal User currentUser, @PathVariable UUID id) {
		userService.followUser(currentUser, id);
		return ResponseEntity.ok(ApiResponseDTO.ok("Você começou a seguir este usuário."));
	}

	// ==========================
	// DEIXA DE SEGUIR UM USUÁRIO POR ID
	// ==========================

	@DeleteMapping("/{id}/unfollow")
	public ResponseEntity<ApiResponseDTO> unfollowUser(@AuthenticationPrincipal User currentUser,
			@PathVariable UUID id) {
		userService.unfollowUser(currentUser, id);
		return ResponseEntity.ok(ApiResponseDTO.ok("Você deixou de seguir este usuário."));
	}

	// ==========================
	// LISTA DE SEGUIDORES DE UM USUÁRIO
	// ==========================

	@GetMapping("/{id}/followers")
	public ResponseEntity<List<UserSummaryDTO>> getFollowers(@PathVariable UUID id) {
		List<UserSummaryDTO> followers = userService.getFollowers(id);
		return ResponseEntity.ok(followers);
	}

	// ==========================
	// LISTA DE SEGUINDO DE UM USUÁRIO
	// ==========================

	@GetMapping("/{id}/following")
	public ResponseEntity<List<UserSummaryDTO>> getFollowing(@PathVariable UUID id) {
		List<UserSummaryDTO> following = userService.getFollowing(id);
		return ResponseEntity.ok(following);
	}

	// ==========================
	// PERFIL PÚBLICO DO USUÁRIO
	// ==========================

	@GetMapping("/{id}/profile")
	public ResponseEntity<PublicUserProfileDTO> getPublicProfile(@AuthenticationPrincipal User currentUser,
			@PathVariable UUID id) {
		PublicUserProfileDTO profile = userService.getPublicUserProfile(currentUser, id);
		return ResponseEntity.ok(profile);
	}

	// ==========================
	// REMOVE UM SEGUIDOR DA SUA CONTA
	// ==========================

	@DeleteMapping("/followers/{followerId}")
	public ResponseEntity<ApiResponseDTO> removeFollower(@AuthenticationPrincipal User currentUser,
			@PathVariable UUID followerId) {
		userService.removeFollower(currentUser, followerId);
		return ResponseEntity.ok(ApiResponseDTO.ok("Seguidor removido com sucesso."));
	}
}