package com.humanin.planpaz.service;

import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.humanin.planpaz.dto.CommentCreateDTO;
import com.humanin.planpaz.dto.CommentResponseDTO;
import com.humanin.planpaz.model.Comment;
import com.humanin.planpaz.model.Post;
import com.humanin.planpaz.model.User;
import com.humanin.planpaz.repositories.CommentRepository;
import com.humanin.planpaz.repositories.PostRepository;
import com.humanin.planpaz.repositories.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CommentService {

	private final CommentRepository commentRepository;
	private final PostRepository postRepository;
	private final UserRepository userRepository;
	private final ContentModerationService contentModerationService;
	private final AchievementService achievementService;

	@Transactional
	public CommentResponseDTO addComment(UUID postId, CommentCreateDTO dto) {
		contentModerationService.validateContent(dto.getContent());

		Post post = postRepository.findById(postId)
				.orElseThrow(() -> new RuntimeException("Post não encontrado com ID: " + postId));

		User author = userRepository.findById(dto.getAuthorId())
				.orElseThrow(() -> new RuntimeException("Usuário não encontrado com ID: " + dto.getAuthorId()));

		Comment comment = new Comment();
		comment.setPost(post);
		comment.setAuthor(author);
		comment.setContent(dto.getContent());

		if (dto.getParentCommentId() != null) {
			Comment parent = commentRepository.findById(dto.getParentCommentId())
					.orElseThrow(() -> new RuntimeException("Comentário pai não encontrado"));
			comment.setParentComment(parent);
		}

		Comment savedComment = commentRepository.save(comment);

		// Atribuir +2 EcoScore ao autor pelo comentário
		int currentEcoscore = author.getEcoscore() != null ? author.getEcoscore() : 0;
		author.setEcoscore(currentEcoscore + 2);
		userRepository.save(author);

		List<com.humanin.planpaz.dto.AchievementProgressDTO> unlockedList = List.of();
		try {
			unlockedList = achievementService.checkAndGrantAll(author.getId());
		} catch (Exception e) {
			// ignore
		}

		CommentResponseDTO response = CommentResponseDTO.fromEntity(savedComment);
		if (unlockedList != null && !unlockedList.isEmpty()) {
			response.setUnlockedAchievement(unlockedList.get(0));
			response.setUnlockedAchievements(unlockedList);
		}

		return response;
	}

	@Transactional(readOnly = true)
	public Page<CommentResponseDTO> getPostComments(UUID postId, int page, int size) {
		Pageable pageable = PageRequest.of(page, size);
		return commentRepository.findByPostIdAndParentCommentIsNullOrderByCommentedAtDesc(postId, pageable)
				.map(CommentResponseDTO::fromEntity);
	}

	@Transactional(readOnly = true)
	public Page<CommentResponseDTO> getReplies(UUID commentId, int page, int size) {
		Pageable pageable = PageRequest.of(page, size);
		return commentRepository.findByParentCommentIdOrderByCommentedAtAsc(commentId, pageable)
				.map(CommentResponseDTO::fromEntity);
	}

	@Transactional
	public void deleteComment(UUID commentId, UUID authorId) {
		Comment comment = commentRepository.findById(commentId)
				.orElseThrow(() -> new RuntimeException("Comentário não encontrado"));

		if (authorId != null) {
			boolean isCommentAuthor = comment.getAuthor() != null && comment.getAuthor().getId().equals(authorId);
			boolean isPostAuthor = comment.getPost() != null && comment.getPost().getAuthor() != null
					&& comment.getPost().getAuthor().getId().equals(authorId);
			if (!isCommentAuthor && !isPostAuthor) {
				throw new RuntimeException("Você não tem permissão para deletar este comentário.");
			}
		}

		commentRepository.delete(comment);
	}
}