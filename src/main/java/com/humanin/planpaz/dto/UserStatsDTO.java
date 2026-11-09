package com.humanin.planpaz.dto;

import java.util.UUID;

public record UserStatsDTO(
		UUID userId,
		double totalCo2Grams,
		int totalEcoScore,
		long totalPlants,
		long totalPosts,
		long daysOnApp,
		int streakCount,
		long followersCount,
		long followingCount
) {
	public UserStatsDTO(
			UUID userId,
			double totalCo2Grams,
			int totalEcoScore,
			long totalPlants,
			long totalPosts,
			long daysOnApp,
			int streakCount
	) {
		this(userId, totalCo2Grams, totalEcoScore, totalPlants, totalPosts, daysOnApp, streakCount, 0L, 0L);
	}
}
