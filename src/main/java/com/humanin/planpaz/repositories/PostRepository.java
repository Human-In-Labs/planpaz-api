package com.humanin.planpaz.repositories;

import com.humanin.planpaz.model.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface PostRepository extends JpaRepository<Post, UUID> {
	Page<Post> findByAuthorIdOrderByPostedAtDesc(UUID authorId, Pageable pageable);

	Page<Post> findAllByOrderByPostedAtDesc(Pageable pageable);
}