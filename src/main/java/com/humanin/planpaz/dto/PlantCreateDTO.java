
package com.humanin.planpaz.dto;

import com.humanin.planpaz.model.Plant;
import com.humanin.planpaz.model.enums.LuminosityLevel;
import com.humanin.planpaz.model.enums.Size;
import com.humanin.planpaz.model.enums.TemperatureLevel;
import com.humanin.planpaz.model.enums.Type;
import com.humanin.planpaz.model.enums.WateringLevel;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record PlantCreateDTO(
		@NotBlank(message = "O nome da espécie é obrigatório.") @jakarta.validation.constraints.Size(max = 50, message = "O nome deve ter no máximo 50 caracteres.") String name,

		@jakarta.validation.constraints.Size(max = 100, message = "O nome científico deve ter no máximo 100 caracteres.") String scientificName,

		String description, String careGuide,

		@NotNull(message = "O nível de rega é obrigatório.") WateringLevel wateringLevel,

		@NotNull(message = "O nível de luminosidade é obrigatório.") LuminosityLevel luminosityLevel,

		TemperatureLevel temperatureLevel, Size size, Type type, String imagePath) {
	public Plant toEntity() {
		Plant p = new Plant();
		p.setName(name);
		p.setScientificName(scientificName);
		p.setDescription(description);
		p.setCareGuide(careGuide);
		p.setWateringLevel(wateringLevel);
		p.setLuminosityLevel(luminosityLevel);
		p.setTemperatureLevel(temperatureLevel);
		p.setSize(size);
		p.setType(type);
		p.setImagePath(imagePath);
		return p;
	}
}
