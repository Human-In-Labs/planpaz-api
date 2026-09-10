package com.humanin.planpaz.controller;

import java.util.Map;
import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping; 
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.humanin.planpaz.service.LikeService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/posts/{postId}/likes")
@RequiredArgsConstructor
public class LikeController {

	private final LikeService likeService;

	// Adicione a anotação @PostMapping aqui
	@PostMapping
	public ResponseEntity<Map<String, Object>> toggleLike(@PathVariable UUID postId,
			@RequestBody Map<String, UUID> payload) {

		UUID authorId = payload.get("authorId");

		if (authorId == null) {
			throw new IllegalArgumentException("O campo 'authorId' é obrigatório no corpo da requisição.");
		}

		boolean liked = likeService.toggleLike(postId, authorId);
		long totalLikes = likeService.countLikes(postId);

		return ResponseEntity.ok(Map.of("liked", liked, "totalLikes", totalLikes));
	}

	// Retorna a contagem total de likes de um post
	@GetMapping("/count")
	public ResponseEntity<Long> countLikes(@PathVariable UUID postId) {
		return ResponseEntity.ok(likeService.countLikes(postId));
	}
}