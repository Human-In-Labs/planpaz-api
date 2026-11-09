package com.humanin.planpaz.repositories;

import com.humanin.planpaz.model.Comment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

public interface CommentRepository extends JpaRepository<Comment, UUID> {

	// Busca comentários raiz do post (sem comentário pai)
	@EntityGraph(attributePaths = { "author" })
	Page<Comment> findByPostIdAndParentCommentIsNullOrderByCommentedAtDesc(UUID postId, Pageable pageable);

	// Busca as respostas de um comentário específico
	@EntityGraph(attributePaths = { "author" })
	Page<Comment> findByParentCommentIdOrderByCommentedAtAsc(UUID parentCommentId, Pageable pageable);

	// Busca em lote todas as respostas de uma página de comentários pais
	@EntityGraph(attributePaths = { "author", "parentComment" })
	List<Comment> findByParentCommentIdInOrderByCommentedAtAsc(Collection<UUID> parentIds);

	// Conta total de comentários em um post
	long countByPostId(UUID postId);

	@Query("SELECT c.post.id, COUNT(c) FROM Comment c WHERE c.post.id IN :postIds GROUP BY c.post.id")
	List<Object[]> countByPostIds(@Param("postIds") Collection<UUID> postIds);

	// Conta total de comentários feitos por um autor
	long countByAuthorId(UUID authorId);

	@Modifying(clearAutomatically = true)
	@Query("DELETE FROM Comment c WHERE c.post.id = :postId")
	void deleteByPostId(@Param("postId") UUID postId);

	@Modifying(clearAutomatically = true)
	@Query("DELETE FROM Comment c WHERE c.parentComment.id = :parentCommentId")
	void deleteByParentCommentId(@Param("parentCommentId") UUID parentCommentId);
}
