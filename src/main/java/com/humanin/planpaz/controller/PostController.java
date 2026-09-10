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

import com.humanin.planpaz.dto.PostCreateDTO;
import com.humanin.planpaz.dto.PostResponseDTO;
import com.humanin.planpaz.service.PostService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/posts")
@RequiredArgsConstructor
public class PostController {

	private final PostService postService;

	@PostMapping
	public ResponseEntity<PostResponseDTO> createPost(@Valid @RequestBody PostCreateDTO dto) {
		PostResponseDTO response = postService.createPost(dto);
		return ResponseEntity.status(HttpStatus.CREATED).body(response);
	}

	@GetMapping
	public ResponseEntity<Page<PostResponseDTO>> getFeed(@RequestParam(defaultValue = "0") int page,
			@RequestParam(defaultValue = "15") int size, @RequestParam(required = false) UUID currentUserId) {
		Page<PostResponseDTO> feed = postService.getFeed(page, size, currentUserId);
		return ResponseEntity.ok(feed);
	}

	@GetMapping("/{id}")
	public ResponseEntity<PostResponseDTO> getPostById(@PathVariable UUID id,
			@RequestParam(required = false) UUID currentUserId) {
		PostResponseDTO post = postService.getPostById(id, currentUserId);
		return ResponseEntity.ok(post);
	}

	@GetMapping("/author/{authorId}")
	public ResponseEntity<Page<PostResponseDTO>> getPostsByAuthor(@PathVariable UUID authorId,
			@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "15") int size,
			@RequestParam(required = false) UUID currentUserId) {
		Page<PostResponseDTO> posts = postService.getPostsByAuthor(authorId, page, size, currentUserId);
		return ResponseEntity.ok(posts);
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<Void> deletePost(@PathVariable UUID id, @RequestParam UUID authorId) {
		postService.deletePost(id, authorId);
		return ResponseEntity.noContent().build();
	}
}