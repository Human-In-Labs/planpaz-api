package com.humanin.planpaz.repositories;

import com.humanin.planpaz.model.Like;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface LikeRepository extends JpaRepository<Like, UUID> {

	// Busca a curtida de um autor específico em um post
	Optional<Like> findByAuthorIdAndPostId(UUID authorId, UUID postId);

	// Verifica se o usuário já curtiu
	boolean existsByAuthorIdAndPostId(UUID authorId, UUID postId);

	// Conta o total de curtidas do post
	long countByPostId(UUID postId);

	// Conta o total de curtidas recebidas em todas as publicações de um autor
	long countByPostAuthorId(UUID authorId);
}