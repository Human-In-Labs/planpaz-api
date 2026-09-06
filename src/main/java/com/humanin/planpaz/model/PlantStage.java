package com.humanin.planpaz.model;

import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "plant_stage")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PlantStage {

	@Id
	@GeneratedValue(strategy = GenerationType.UUID)
	private UUID id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "plant_id", nullable = false)
	private Plant plant;

	@Column(nullable = false, length = 50)
	private String name;

	@Column(name = "\"order\"")
	private Integer order;

	private Integer days;

	@Column(columnDefinition = "TEXT")
	private String description;

	@Column(name = "image")
	private String imagePath;
}
