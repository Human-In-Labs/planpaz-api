package com.humanin.planpaz.repositories;

import com.humanin.planpaz.model.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.UUID;

public interface PostRepository extends JpaRepository<Post, UUID> {
	long countByAuthorId(UUID authorId);

	Page<Post> findByAuthorIdOrderByPostedAtDesc(UUID authorId, Pageable pageable);

	Page<Post> findAllByOrderByPostedAtDesc(Pageable pageable);

	@Query("SELECT p FROM Post p WHERE LOWER(p.tags) LIKE LOWER(CONCAT('%', :tag, '%')) ORDER BY p.postedAt DESC")
	Page<Post> findByTag(@Param("tag") String tag, Pageable pageable);
}