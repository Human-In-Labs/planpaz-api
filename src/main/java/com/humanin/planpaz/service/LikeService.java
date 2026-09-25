package com.humanin.planpaz.service;

import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.humanin.planpaz.model.Like;
import com.humanin.planpaz.model.Post;
import com.humanin.planpaz.model.User;
import com.humanin.planpaz.repositories.LikeRepository;
import com.humanin.planpaz.repositories.PostRepository;
import com.humanin.planpaz.repositories.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class LikeService {

	private final LikeRepository likeRepository;
	private final UserRepository userRepository;
	private final PostRepository postRepository;

	/**
	 * Alterna a curtida: se já existir, remove; se não existir, cria.
	 * 
	 * @return true se curtiu, false se descurtiu.
	 */
	@Transactional
	public boolean toggleLike(UUID postId, UUID authorId) {
		Optional<Like> existingLike = likeRepository.findByAuthorIdAndPostId(authorId, postId);

		if (existingLike.isPresent()) {
			likeRepository.delete(existingLike.get());
			return false; // Descurtiu
		}

		User author = userRepository.findById(authorId)
				.orElseThrow(() -> new RuntimeException("Usuário não encontrado"));
		Post post = postRepository.findById(postId).orElseThrow(() -> new RuntimeException("Post não encontrado"));

		Like like = new Like(author, post);
		likeRepository.save(like);

		// Verificar se o autor do post atingiu um NOVO patamar de 10 curtidas recebidas
		if (post.getAuthor() != null) {
			User postAuthor = post.getAuthor();
			long totalReceivedLikes = likeRepository.countByPostAuthorId(postAuthor.getId());
			int currentTier = (int) (totalReceivedLikes / 10);
			int highestTier = postAuthor.getHighestLikeTier() != null ? postAuthor.getHighestLikeTier() : 0;

			if (currentTier > highestTier) {
				int newPoints = currentTier - highestTier;
				postAuthor.setHighestLikeTier(currentTier);
				int currentEcoscore = postAuthor.getEcoscore() != null ? postAuthor.getEcoscore() : 0;
				postAuthor.setEcoscore(currentEcoscore + newPoints);
				userRepository.save(postAuthor);
			}
		}

		return true; // Curtiu
	}

	@Transactional(readOnly = true)
	public long countLikes(UUID postId) {
		return likeRepository.countByPostId(postId);
	}

	@Transactional(readOnly = true)
	public boolean hasUserLiked(UUID postId, UUID authorId) {
		return likeRepository.existsByAuthorIdAndPostId(authorId, postId);
	}
}