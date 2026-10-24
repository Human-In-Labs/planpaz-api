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

import com.humanin.planpaz.dto.LikeRequestDTO;
import com.humanin.planpaz.service.LikeService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/posts/{postId}/likes")
@RequiredArgsConstructor
public class LikeController {

	private final LikeService likeService;

	@PostMapping
	public ResponseEntity<Map<String, Object>> toggleLike(
			@PathVariable UUID postId,
			@Valid @RequestBody LikeRequestDTO payload) {

		UUID authorId = payload.authorId();
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