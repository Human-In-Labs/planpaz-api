package com.humanin.planpaz.repositories;

import com.humanin.planpaz.model.Like;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Set;
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

	@Query("SELECT l.post.id, COUNT(l) FROM Like l WHERE l.post.id IN :postIds GROUP BY l.post.id")
	List<Object[]> countByPostIds(@Param("postIds") Collection<UUID> postIds);

	@Query("SELECT l.post.id FROM Like l WHERE l.author.id = :authorId AND l.post.id IN :postIds")
	Set<UUID> findLikedPostIds(@Param("authorId") UUID authorId, @Param("postIds") Collection<UUID> postIds);

	@Modifying
	@Query("DELETE FROM Like l WHERE l.post.id = :postId")
	void deleteByPostId(@Param("postId") UUID postId);
}
