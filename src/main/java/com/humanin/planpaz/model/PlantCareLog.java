package com.humanin.planpaz.model;

import java.time.LocalDateTime;
import java.util.UUID;

import org.hibernate.annotations.CreationTimestamp;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.humanin.planpaz.model.enums.CareType;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
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
@Table(name = "plant_care_log")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PlantCareLog {

	@Id
	@GeneratedValue(strategy = GenerationType.UUID)
	private UUID id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "garden_plant_id", nullable = false)
	@JsonIgnore
	private GardenPlant gardenPlant;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id", nullable = false)
	@JsonIgnore
	private User user;

	@Enumerated(EnumType.STRING)
	@Column(name = "care_type", nullable = false)
	private CareType careType;

	@Column(name = "points_earned")
	private Integer pointsEarned;

	@CreationTimestamp
	@Column(name = "performed_at", nullable = false, updatable = false)
	private LocalDateTime performedAt;
}
