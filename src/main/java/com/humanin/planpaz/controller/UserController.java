package com.humanin.planpaz.controller;

import java.util.List;
import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.humanin.planpaz.dto.UserSummaryDTO;
import com.humanin.planpaz.model.User;
import com.humanin.planpaz.service.UserService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {

	private final UserService userService;

	@GetMapping
	public ResponseEntity<String> getUser() {
		System.out.println("[SUCESS] Usuário autenticado com token válido.");
		return ResponseEntity.ok("Sucesso!");
	}

	// 1. Pesquisar usuários por @username
	@GetMapping("/search")
	public ResponseEntity<List<UserSummaryDTO>> searchUsers(@RequestParam String username) {
		List<UserSummaryDTO> users = userService.searchByUsername(username);
		return ResponseEntity.ok(users);
	}

	// 2. Seguir um usuário pelo ID
	@PostMapping("/{id}/follow")
	public ResponseEntity<Void> followUser(@AuthenticationPrincipal User currentUser, @PathVariable UUID id) {
		userService.followUser(currentUser, id);
		return ResponseEntity.ok().build();
	}

	// 3. Deixar de seguir um usuário (Unfollow)
	@DeleteMapping("/{id}/unfollow")
	public ResponseEntity<Void> unfollowUser(@AuthenticationPrincipal User currentUser, @PathVariable UUID id) {
		userService.unfollowUser(currentUser, id);
		return ResponseEntity.noContent().build();
	}

	// 4. Listar quem está seguindo um determinado usuário
	@GetMapping("/{id}/followers")
	public ResponseEntity<List<UserSummaryDTO>> getFollowers(@PathVariable UUID id) {
		List<UserSummaryDTO> followers = userService.getFollowers(id);
		return ResponseEntity.ok(followers);
	}

	// 5. Listar quem um determinado usuário está seguindo
	@GetMapping("/{id}/following")
	public ResponseEntity<List<UserSummaryDTO>> getFollowing(@PathVariable UUID id) {
		List<UserSummaryDTO> following = userService.getFollowing(id);
		return ResponseEntity.ok(following);
	}

	// 6. Remover alguém da SUA lista de seguidores
	@DeleteMapping("/followers/{followerId}")
	public ResponseEntity<Void> removeFollower(@AuthenticationPrincipal User currentUser,
			@PathVariable UUID followerId) {
		userService.removeFollower(currentUser, followerId);
		return ResponseEntity.noContent().build();
	}
}