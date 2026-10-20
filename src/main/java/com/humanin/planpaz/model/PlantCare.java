package com.humanin.planpaz.model;

import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.humanin.planpaz.model.enums.CultivationMethod;
import com.humanin.planpaz.model.enums.Difficulty;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "plant_care")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class PlantCare {

	@Id
	@GeneratedValue(strategy = GenerationType.UUID)
	private UUID id;

	@OneToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "plant_id", nullable = false, unique = true)
	private Plant plant;

	@Enumerated(EnumType.STRING)
	@Column(name = "cultivation_method")
	private CultivationMethod cultivationMethod;

	@Column(columnDefinition = "TEXT")
	private String materials;

	@Column(name = "propagation_instructions", columnDefinition = "TEXT")
	private String propagationInstructions;

	@Column(columnDefinition = "TEXT")
	private String soil;

	@Column(columnDefinition = "TEXT")
	private String climate;

	@Column(columnDefinition = "TEXT")
	private String lighting;

	@Column(columnDefinition = "TEXT")
	private String watering;

	@Column(columnDefinition = "TEXT")
	private String pruning;

	@Column(columnDefinition = "TEXT")
	private String harvesting;

	@Enumerated(EnumType.STRING)
	private Difficulty difficulty;

	@Column(name = "toxicity_alert", columnDefinition = "TEXT")
	private String toxicityAlert;

	@Column(name = "hazards_alert", columnDefinition = "TEXT")
	private String hazardsAlert;

	@Column(name = "invasive_alert", columnDefinition = "TEXT")
	private String invasiveAlert;

	@Column(name = "transplant_notes", columnDefinition = "TEXT")
	private String transplantNotes;

	@Column(name = "container_specs", columnDefinition = "TEXT")
	private String containerSpecs;

	@Column(name = "general_tips", columnDefinition = "TEXT")
	private String generalTips;
}
