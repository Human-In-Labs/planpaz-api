package com.humanin.planpaz.repositories;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import com.humanin.planpaz.model.Followers;
import com.humanin.planpaz.model.User;

public interface FollowersRepository extends JpaRepository<Followers, UUID> { // Alterado para UUID

	boolean existsByFollowerAndFollowed(User follower, User followed);

	Optional<Followers> findByFollowerAndFollowed(User follower, User followed);

	@EntityGraph(attributePaths = {"follower"})
	List<Followers> findByFollowed(User followed);

	@EntityGraph(attributePaths = {"followed"})
	List<Followers> findByFollower(User follower);

	@Modifying(clearAutomatically = true)
	@Query("DELETE FROM Followers f WHERE f.follower = :follower AND f.followed = :followed")
	void deleteByFollowerAndFollowed(@Param("follower") User follower, @Param("followed") User followed);
}