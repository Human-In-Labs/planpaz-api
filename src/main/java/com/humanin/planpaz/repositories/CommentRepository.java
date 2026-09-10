package com.humanin.planpaz.repositories;

import com.humanin.planpaz.model.Comment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface CommentRepository extends JpaRepository<Comment, UUID> {

	// Busca comentários raiz do post (sem comentário pai)
	Page<Comment> findByPostIdAndParentCommentIsNullOrderByCommentedAtDesc(UUID postId, Pageable pageable);

	// Busca as respostas de um comentário específico
	Page<Comment> findByParentCommentIdOrderByCommentedAtAsc(UUID parentCommentId, Pageable pageable);

	// Conta total de comentários em um post
	long countByPostId(UUID postId);
}