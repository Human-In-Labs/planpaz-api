package com.humanin.planpaz.dto;

import java.util.List;

public record ApiResponseDTO(
		String message,
		boolean success,
		AchievementProgressDTO unlockedAchievement,
		List<AchievementProgressDTO> unlockedAchievements
) {
	public static ApiResponseDTO ok(String message) {
		return new ApiResponseDTO(message, true, null, List.of());
	}

	public static ApiResponseDTO ok(String message, AchievementProgressDTO unlockedAchievement) {
		List<AchievementProgressDTO> list = unlockedAchievement != null ? List.of(unlockedAchievement) : List.of();
		return new ApiResponseDTO(message, true, unlockedAchievement, list);
	}

	public static ApiResponseDTO ok(String message, List<AchievementProgressDTO> unlockedAchievements) {
		AchievementProgressDTO first = (unlockedAchievements != null && !unlockedAchievements.isEmpty()) ? unlockedAchievements.get(0) : null;
		List<AchievementProgressDTO> list = unlockedAchievements != null ? unlockedAchievements : List.of();
		return new ApiResponseDTO(message, true, first, list);
	}

	public static ApiResponseDTO error(String message) {
		return new ApiResponseDTO(message, false, null, List.of());
	}
}
