package com.humanin.planpaz.controller;

import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.humanin.planpaz.dto.CommentCreateDTO;
import com.humanin.planpaz.dto.CommentResponseDTO;
import com.humanin.planpaz.service.CommentService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/posts/{postId}/comments")
@RequiredArgsConstructor
public class CommentController {

	private final CommentService commentService;

	@PostMapping
	public ResponseEntity<CommentResponseDTO> addComment(@PathVariable UUID postId,
			@Valid @RequestBody CommentCreateDTO dto) {
		CommentResponseDTO response = commentService.addComment(postId, dto);
		return ResponseEntity.status(HttpStatus.CREATED).body(response);
	}

	@GetMapping
	public ResponseEntity<Page<CommentResponseDTO>> getPostComments(@PathVariable UUID postId,
			@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size) {
		Page<CommentResponseDTO> comments = commentService.getPostComments(postId, page, size);
		return ResponseEntity.ok(comments);
	}

	@GetMapping("/{commentId}/replies")
	public ResponseEntity<Page<CommentResponseDTO>> getReplies(@PathVariable UUID postId, @PathVariable UUID commentId,
			@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size) {
		Page<CommentResponseDTO> replies = commentService.getReplies(commentId, page, size);
		return ResponseEntity.ok(replies);
	}

	@DeleteMapping("/{commentId}")
	public ResponseEntity<Void> deleteComment(@PathVariable UUID postId, @PathVariable UUID commentId,
			@RequestParam UUID authorId) {
		commentService.deleteComment(commentId, authorId);
		return ResponseEntity.noContent().build();
	}
}