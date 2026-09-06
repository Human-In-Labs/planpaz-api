package com.humanin.planpaz.repositories;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import com.humanin.planpaz.model.UserAchievement;

public interface UserAchievementRepository extends JpaRepository<UserAchievement, UUID> {

	@EntityGraph(attributePaths = {"achievement"})
	List<UserAchievement> findByUserId(UUID userId);

	boolean existsByUserIdAndAchievementId(UUID userId, UUID achievementId);
}
