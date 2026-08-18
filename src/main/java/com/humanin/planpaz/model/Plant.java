package com.humanin.planpaz.model;

import java.util.UUID;

import com.humanin.planpaz.model.enums.ExperienceLevel;
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

//anotações do JPA
@Entity
@Table(name = "plant")
//anotações do Lombok
//@Getter
//@Setter
public class Plant {

	// campos da tabela
	@Id
	@GeneratedValue(strategy = GenerationType.UUID)
	private UUID id;
	@Column(unique = true)
	private String name;
	private String scientificName;
	private String description;
	@Enumerated(EnumType.STRING)
	private WateringLevel wateringLevel;
	@Enumerated(EnumType.STRING)
	private LuminosityLevel luminosityLevel;
	@Enumerated(EnumType.STRING)
	private TemperatureLevel temperatureLevel;
	@Enumerated(EnumType.STRING)
	private Size size;
	@Enumerated(EnumType.STRING)
	private Type type;
	private String imagePath;
	@Enumerated(EnumType.STRING)
	private ExperienceLevel experienceLevel;

	public UUID getId() {
		return id;
	}

	public void setId(UUID id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getScientificName() {
		return scientificName;
	}

	public void setScientificName(String scientificName) {
		this.scientificName = scientificName;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public WateringLevel getWateringLevel() {
		return wateringLevel;
	}

	public void setWateringLevel(WateringLevel wateringLevel) {
		this.wateringLevel = wateringLevel;
	}

	public LuminosityLevel getLuminosityLevel() {
		return luminosityLevel;
	}

	public void setLuminosityLevel(LuminosityLevel luminosityLevel) {
		this.luminosityLevel = luminosityLevel;
	}

	public TemperatureLevel getTemperatureLevel() {
		return temperatureLevel;
	}

	public void setTemperatureLevel(TemperatureLevel temperatureLevel) {
		this.temperatureLevel = temperatureLevel;
	}

	public Size getSize() {
		return size;
	}

	public void setSize(Size size) {
		this.size = size;
	}

	public Type getType() {
		return type;
	}

	public void setType(Type type) {
		this.type = type;
	}

	public String getImagePath() {
		return imagePath;
	}

	public void setImagePath(String imagePath) {
		this.imagePath = imagePath;
	}

}