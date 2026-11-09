package com.humanin.planpaz.service;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.humanin.planpaz.dto.PostCreateDTO;
import com.humanin.planpaz.dto.PostResponseDTO;
import com.humanin.planpaz.infra.exception.BusinessException;
import com.humanin.planpaz.model.Post;
import com.humanin.planpaz.model.User;
import com.humanin.planpaz.repositories.CommentRepository;
import com.humanin.planpaz.repositories.LikeRepository;
import com.humanin.planpaz.repositories.PostRepository;
import com.humanin.planpaz.repositories.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PostService {

	private final PostRepository postRepository;
	private final UserRepository userRepository;
	private final LikeRepository likeRepository;
	private final CommentRepository commentRepository;
	private final ContentModerationService contentModerationService;
	private final AchievementService achievementService;

	@Transactional
	public PostResponseDTO createPost(PostCreateDTO dto) {
		contentModerationService.validateContent(dto.getTitle());
		contentModerationService.validateContent(dto.getContent());

		User author = userRepository.findById(dto.getAuthorId())
				.orElseThrow(() -> new RuntimeException("Usuário não encontrado com o ID: " + dto.getAuthorId()));

		Post post = new Post();
		post.setAuthor(author);
		post.setTitle(dto.getTitle());
		post.setContent(dto.getContent());
		post.setMedia(dto.getMedia());

		if (dto.getTags() != null) {
			List<String> sanitizedTags = dto.getTags().stream().filter(t -> t != null && !t.isBlank()).map(String::trim)
					.limit(3).toList();
			post.setTags(sanitizedTags);
		}

		Post savedPost = postRepository.save(post);

		// Atribuir +5 EcoScore ao autor pela criação do post
		int currentEcoscore = author.getEcoscore() != null ? author.getEcoscore() : 0;
		author.setEcoscore(currentEcoscore + 5);
		userRepository.save(author);

		List<com.humanin.planpaz.dto.AchievementProgressDTO> unlockedList = List.of();
		try {
			unlockedList = achievementService.checkAndGrantAll(author.getId());
		} catch (Exception e) {
			// ignore
		}

		PostResponseDTO response = PostResponseDTO.fromEntity(savedPost, 0L, false, 0L);
		if (unlockedList != null && !unlockedList.isEmpty()) {
			response.setUnlockedAchievement(unlockedList.get(0));
			response.setUnlockedAchievements(unlockedList);
		}

		return response;
	}

	@Transactional(readOnly = true)
	public Page<PostResponseDTO> getFeed(int page, int size, UUID currentUserId, String tag, boolean followingOnly) {
		Pageable pageable = PageRequest.of(page, size);
		Page<Post> posts;
		if (followingOnly && currentUserId != null) {
			posts = postRepository.findByFollowedUsers(currentUserId, pageable);
		} else if (tag != null && !tag.isBlank()) {
			posts = postRepository.findByTag(tag.trim(), pageable);
		} else {
			posts = postRepository.findAllByOrderByPostedAtDesc(pageable);
		}
		return mapPageToDTOs(posts, currentUserId);
	}

	@Transactional(readOnly = true)
	public Page<PostResponseDTO> getFeed(int page, int size, UUID currentUserId, String tag) {
		return getFeed(page, size, currentUserId, tag, false);
	}

	@Transactional(readOnly = true)
	public Page<PostResponseDTO> getFeed(int page, int size, UUID currentUserId) {
		return getFeed(page, size, currentUserId, null, false);
	}

	@Transactional(readOnly = true)
	public Page<PostResponseDTO> getPostsByAuthor(UUID authorId, int page, int size, UUID currentUserId) {
		Pageable pageable = PageRequest.of(page, size);
		Page<Post> posts = postRepository.findByAuthorIdOrderByPostedAtDesc(authorId, pageable);
		return mapPageToDTOs(posts, currentUserId);
	}

	@Transactional(readOnly = true)
	public PostResponseDTO getPostById(UUID postId, UUID currentUserId) {
		Post post = postRepository.findById(postId)
				.orElseThrow(() -> new RuntimeException("Post não encontrado com o ID: " + postId));
		return mapToDTO(post, currentUserId);
	}

	@Transactional
	public void deletePost(UUID postId, UUID authorId) {
		Post post = postRepository.findById(postId)
				.orElseThrow(() -> new BusinessException("Post não encontrado com o ID: " + postId));

		if (!post.getAuthor().getId().equals(authorId)) {
			throw new BusinessException("Você não tem permissão para deletar este post.");
		}

		likeRepository.deleteByPostId(postId);
		commentRepository.deleteByPostId(postId);
		postRepository.delete(post);
	}

	private Page<PostResponseDTO> mapPageToDTOs(Page<Post> postsPage, UUID currentUserId) {
		if (postsPage.isEmpty()) {
			return postsPage.map(post -> PostResponseDTO.fromEntity(post, 0L, false, 0L));
		}

		List<UUID> postIds = postsPage.getContent().stream().map(Post::getId).toList();

		Map<UUID, Long> likesCountMap = new HashMap<>();
		for (Object[] row : likeRepository.countByPostIds(postIds)) {
			if (row != null && row.length >= 2 && row[0] instanceof UUID postId && row[1] instanceof Number count) {
				likesCountMap.put(postId, count.longValue());
			}
		}

		Set<UUID> likedPostIds = currentUserId != null
				? likeRepository.findLikedPostIds(currentUserId, postIds)
				: Collections.emptySet();

		Map<UUID, Long> commentsCountMap = new HashMap<>();
		for (Object[] row : commentRepository.countByPostIds(postIds)) {
			if (row != null && row.length >= 2 && row[0] instanceof UUID postId && row[1] instanceof Number count) {
				commentsCountMap.put(postId, count.longValue());
			}
		}

		return postsPage.map(post -> {
			long likesCount = likesCountMap.getOrDefault(post.getId(), 0L);
			boolean likedByCurrentUser = likedPostIds != null && likedPostIds.contains(post.getId());
			long commentsCount = commentsCountMap.getOrDefault(post.getId(), 0L);
			return PostResponseDTO.fromEntity(post, likesCount, likedByCurrentUser, commentsCount);
		});
	}

	private PostResponseDTO mapToDTO(Post post, UUID currentUserId) {
		long likesCount = likeRepository.countByPostId(post.getId());
		boolean likedByCurrentUser = currentUserId != null
				&& likeRepository.existsByAuthorIdAndPostId(currentUserId, post.getId());
		long commentsCount = commentRepository.countByPostId(post.getId());

		return PostResponseDTO.fromEntity(post, likesCount, likedByCurrentUser, commentsCount);
	}
}
