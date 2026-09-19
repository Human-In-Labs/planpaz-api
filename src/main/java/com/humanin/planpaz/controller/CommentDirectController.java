package com.humanin.planpaz.controller;

import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.humanin.planpaz.model.User;
import com.humanin.planpaz.service.CommentService;

import lombok.RequiredArgsConstructor;

import com.humanin.planpaz.dto.ApiResponseDTO;

@RestController
@RequestMapping("/api/comments")
@RequiredArgsConstructor
public class CommentDirectController {

	private final CommentService commentService;

	@DeleteMapping("/{id}")
	public ResponseEntity<ApiResponseDTO> deleteComment(@PathVariable UUID id, @RequestParam(required = false) UUID authorId, Authentication authentication) {
		UUID userId = authorId;
		if (userId == null && authentication != null && authentication.getPrincipal() instanceof User user) {
			userId = user.getId();
		}
		commentService.deleteComment(id, userId);
		return ResponseEntity.ok(ApiResponseDTO.ok("Comentário excluído com sucesso."));
	}
}
