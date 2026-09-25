package com.humanin.planpaz.dto;

import java.util.UUID;

public record UserStatsDTO(
		UUID userId,
		double totalCo2Grams,
		int totalEcoScore,
		long totalPlants,
		long totalPosts,
		long daysOnApp
) {}
