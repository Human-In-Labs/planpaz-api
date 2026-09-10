package com.humanin.planpaz.service;

import com.humanin.planpaz.dto.PostCreateDTO;
import com.humanin.planpaz.dto.PostResponseDTO;
import com.humanin.planpaz.model.Post;
import com.humanin.planpaz.model.User;
import com.humanin.planpaz.repositories.CommentRepository;
import com.humanin.planpaz.repositories.LikeRepository;
import com.humanin.planpaz.repositories.PostRepository;
import com.humanin.planpaz.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PostService {

	private final PostRepository postRepository;
	private final UserRepository userRepository;
	private final LikeRepository likeRepository;
	private final CommentRepository commentRepository;

	@Transactional
	public PostResponseDTO createPost(PostCreateDTO dto) {
		User author = userRepository.findById(dto.getAuthorId())
				.orElseThrow(() -> new RuntimeException("Usuário não encontrado com o ID: " + dto.getAuthorId()));

		Post post = new Post();
		post.setAuthor(author);
		post.setTitle(dto.getTitle());
		post.setContent(dto.getContent());
		post.setMedia(dto.getMedia());

		Post savedPost = postRepository.save(post);
		return mapToDTO(savedPost, dto.getAuthorId());
	}

	@Transactional(readOnly = true)
	public Page<PostResponseDTO> getFeed(int page, int size, UUID currentUserId) {
		Pageable pageable = PageRequest.of(page, size);
		return postRepository.findAllByOrderByPostedAtDesc(pageable).map(post -> mapToDTO(post, currentUserId));
	}

	@Transactional(readOnly = true)
	public Page<PostResponseDTO> getPostsByAuthor(UUID authorId, int page, int size, UUID currentUserId) {
		Pageable pageable = PageRequest.of(page, size);
		return postRepository.findByAuthorIdOrderByPostedAtDesc(authorId, pageable)
				.map(post -> mapToDTO(post, currentUserId));
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
				.orElseThrow(() -> new RuntimeException("Post não encontrado com o ID: " + postId));

		if (!post.getAuthor().getId().equals(authorId)) {
			throw new RuntimeException("Você não tem permissão para deletar este post.");
		}

		postRepository.delete(post);
	}

	private PostResponseDTO mapToDTO(Post post, UUID currentUserId) {
		long likesCount = likeRepository.countByPostId(post.getId());
		boolean likedByCurrentUser = currentUserId != null
				&& likeRepository.existsByAuthorIdAndPostId(currentUserId, post.getId());
		long commentsCount = commentRepository.countByPostId(post.getId());

		return PostResponseDTO.fromEntity(post, likesCount, likedByCurrentUser, commentsCount);
	}
}