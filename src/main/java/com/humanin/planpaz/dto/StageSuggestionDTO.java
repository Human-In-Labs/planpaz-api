package com.humanin.planpaz.dto;

import java.util.UUID;

import com.humanin.planpaz.model.PlantStage;

public record StageSuggestionDTO(
		UUID gardenPlantId,
		long cultivationDays,
		PlantStage currentStage,
		PlantStage suggestedStage
) {}
