package com.humanin.planpaz.dto;

import java.util.UUID;

public record StageSuggestionDTO(
		UUID gardenPlantId,
		long cultivationDays,
		PlantStageResponseDTO currentStage,
		PlantStageResponseDTO suggestedStage
) {}
