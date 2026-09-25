package com.humanin.planpaz.dto;

import java.time.LocalDate;
import java.util.UUID;

public record PlantStatsDTO(
		UUID plantId,
		double co2Grams,
		int ecoScore,
		long cultivationDays,
		int streakDays,
		LocalDate lastWatering,
		LocalDate lastFertilizing,
		LocalDate lastPruning
) {}
