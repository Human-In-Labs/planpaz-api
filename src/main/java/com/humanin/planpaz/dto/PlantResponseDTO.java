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
		Type type, String imagePath, Boolean isRecommended) {
}