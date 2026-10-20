package com.humanin.planpaz.dto;

import java.util.UUID;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.humanin.planpaz.model.PlantCare;
import com.humanin.planpaz.model.enums.CultivationMethod;
import com.humanin.planpaz.model.enums.Difficulty;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record PlantCareResponseDTO(
		UUID id,
		UUID plantId,
		String plantName,
		CultivationMethod cultivationMethod,
		String materials,
		String propagationInstructions,
		String soil,
		String climate,
		String lighting,
		String watering,
		String pruning,
		String harvesting,
		Difficulty difficulty,
		String toxicityAlert,
		String hazardsAlert,
		String invasiveAlert,
		String transplantNotes,
		String containerSpecs,
		String generalTips
) {
	public static PlantCareResponseDTO fromEntity(PlantCare care) {
		if (care == null) return null;
		return new PlantCareResponseDTO(
				care.getId(),
				care.getPlant() != null ? care.getPlant().getId() : null,
				care.getPlant() != null ? care.getPlant().getName() : null,
				care.getCultivationMethod(),
				care.getMaterials(),
				care.getPropagationInstructions(),
				care.getSoil(),
				care.getClimate(),
				care.getLighting(),
				care.getWatering(),
				care.getPruning(),
				care.getHarvesting(),
				care.getDifficulty(),
				care.getToxicityAlert(),
				care.getHazardsAlert(),
				care.getInvasiveAlert(),
				care.getTransplantNotes(),
				care.getContainerSpecs(),
				care.getGeneralTips()
		);
	}
}
