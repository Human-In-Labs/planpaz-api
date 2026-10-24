package com.humanin.planpaz.dto;

import java.util.UUID;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.humanin.planpaz.model.enums.LuminosityLevel;
import com.humanin.planpaz.model.enums.Size;
import com.humanin.planpaz.model.enums.TemperatureLevel;
import com.humanin.planpaz.model.enums.Type;
import com.humanin.planpaz.model.enums.WateringLevel;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record PlantResponseDTO(UUID id, String name, String scientificName, String description, String careGuide,
		WateringLevel wateringLevel, LuminosityLevel luminosityLevel, TemperatureLevel temperatureLevel, Size size,
		Type type, String imagePath, Boolean isRecommended, PlantCareResponseDTO plantCare) {

	public PlantResponseDTO(UUID id, String name, String scientificName, String description, String careGuide,
			WateringLevel wateringLevel, LuminosityLevel luminosityLevel, TemperatureLevel temperatureLevel, Size size,
			Type type, String imagePath, Boolean isRecommended) {
		this(id, name, scientificName, description, careGuide, wateringLevel, luminosityLevel, temperatureLevel, size,
				type, imagePath, isRecommended, null);
	}

	public static PlantResponseDTO fromEntity(com.humanin.planpaz.model.Plant p) {
		if (p == null) {
			return null;
		}
		PlantCareResponseDTO careDTO = p.getCare() != null ? PlantCareResponseDTO.fromEntity(p.getCare()) : null;
		return new PlantResponseDTO(
				p.getId(),
				p.getName(),
				p.getScientificName(),
				p.getDescription(),
				p.getCareGuide(),
				p.getWateringLevel(),
				p.getLuminosityLevel(),
				p.getTemperatureLevel(),
				p.getSize(),
				p.getType(),
				p.getImagePath(),
				null,
				careDTO
		);
	}
}