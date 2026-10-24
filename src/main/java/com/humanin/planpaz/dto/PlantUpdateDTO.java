
package com.humanin.planpaz.dto;

import com.humanin.planpaz.model.enums.LuminosityLevel;
import com.humanin.planpaz.model.enums.Size;
import com.humanin.planpaz.model.enums.TemperatureLevel;
import com.humanin.planpaz.model.enums.Type;
import com.humanin.planpaz.model.enums.WateringLevel;

public record PlantUpdateDTO(
		@jakarta.validation.constraints.Size(max = 50, message = "O nome deve ter no máximo 50 caracteres.") String name,

		@jakarta.validation.constraints.Size(max = 100, message = "O nome científico deve ter no máximo 100 caracteres.") String scientificName,

		String description, String careGuide, WateringLevel wateringLevel, LuminosityLevel luminosityLevel,
		TemperatureLevel temperatureLevel, Size size, Type type, String imagePath) {
}
