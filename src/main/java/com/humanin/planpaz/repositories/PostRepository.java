package com.humanin.planpaz.repositories;

import com.humanin.planpaz.model.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.UUID;

public interface PostRepository extends JpaRepository<Post, UUID> {
	long countByAuthorId(UUID authorId);

	@Override
	@EntityGraph(attributePaths = { "author" })
	Optional<Post> findById(UUID id);

	@EntityGraph(attributePaths = { "author" })
	Page<Post> findByAuthorIdOrderByPostedAtDesc(UUID authorId, Pageable pageable);

	@EntityGraph(attributePaths = { "author" })
	Page<Post> findAllByOrderByPostedAtDesc(Pageable pageable);

	@EntityGraph(attributePaths = { "author" })
	@Query("SELECT p FROM Post p WHERE LOWER(p.tags) LIKE LOWER(CONCAT('%', :tag, '%')) ORDER BY p.postedAt DESC")
	Page<Post> findByTag(@Param("tag") String tag, Pageable pageable);

	@EntityGraph(attributePaths = { "author" })
	@Query("SELECT p FROM Post p WHERE p.author.id IN (SELECT f.followed.id FROM Follow f WHERE f.follower.id = :followerId) ORDER BY p.postedAt DESC")
	Page<Post> findByFollowedUsers(@Param("followerId") UUID followerId, Pageable pageable);
}
