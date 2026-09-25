package com.humanin.planpaz.dto;

import java.util.List;
import java.util.UUID;

public record PublicUserProfileDTO(UUID id, String name, String username, String email, String bio, String avatarUrl,
		long followersCount, long followingCount, boolean isFollowing, long totalPlants, long totalPosts,
		long daysOnApp, long carbonPoints, int ecoscore, List<AchievementProgressDTO> achievements) {
}
