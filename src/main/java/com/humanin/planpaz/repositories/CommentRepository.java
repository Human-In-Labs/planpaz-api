package com.humanin.planpaz.repositories;

import com.humanin.planpaz.model.Comment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface CommentRepository extends JpaRepository<Comment, UUID> {

	// Busca comentários raiz do post (sem comentário pai)
	Page<Comment> findByPostIdAndParentCommentIsNullOrderByCommentedAtDesc(UUID postId, Pageable pageable);

	// Busca as respostas de um comentário específico
	Page<Comment> findByParentCommentIdOrderByCommentedAtAsc(UUID parentCommentId, Pageable pageable);

	// Conta total de comentários em um post
	long countByPostId(UUID postId);

	// Conta total de comentários feitos por um autor
	long countByAuthorId(UUID authorId);

	@org.springframework.data.jpa.repository.Modifying
	@org.springframework.data.jpa.repository.Query("DELETE FROM Comment c WHERE c.post.id = :postId")
	void deleteByPostId(@org.springframework.data.repository.query.Param("postId") UUID postId);

	@org.springframework.data.jpa.repository.Modifying
	@org.springframework.data.jpa.repository.Query("DELETE FROM Comment c WHERE c.parentComment.id = :parentCommentId")
	void deleteByParentCommentId(@org.springframework.data.repository.query.Param("parentCommentId") UUID parentCommentId);
}