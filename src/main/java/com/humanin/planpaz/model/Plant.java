package com.humanin.planpaz.model;

import java.util.UUID;

import com.humanin.planpaz.model.enums.LuminosityLevel;
import com.humanin.planpaz.model.enums.Size;
import com.humanin.planpaz.model.enums.TemperatureLevel;
import com.humanin.planpaz.model.enums.Type;
import com.humanin.planpaz.model.enums.WateringLevel;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "plant")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Plant {

	@Id
	@GeneratedValue(strategy = GenerationType.UUID)
	private UUID id;

	@Column(nullable = false, length = 50)
	private String name;

	@Column(name = "scientific_name", length = 100)
	private String scientificName;

	@Column(columnDefinition = "TEXT")
	private String description;

	@Enumerated(EnumType.STRING)
	@Column(name = "watering_level")
	private WateringLevel wateringLevel;

	@Enumerated(EnumType.STRING)
	@Column(name = "luminosity_level")
	private LuminosityLevel luminosityLevel;

	@Enumerated(EnumType.STRING)
	@Column(name = "temperature_level")
	private TemperatureLevel temperatureLevel;

	@Enumerated(EnumType.STRING)
	private Size size;

	@Enumerated(EnumType.STRING)
	private Type type;

	@Column(name = "image")
	private String imagePath;
}