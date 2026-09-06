package com.humanin.planpaz.model;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

import com.humanin.planpaz.model.enums.Room;

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
@Table(name = "garden_plant")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class GardenPlant {

	@Id
	@GeneratedValue(strategy = GenerationType.UUID)
	private UUID id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "owner_id", nullable = false)
	private User owner;

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "plant_id", nullable = false)
	private Plant plant;

	@Column(name = "plant_nickname")
	private String nickname;

	@Column(name = "planted_at")
	private LocalDateTime plantedAt;

	@Column(name = "last_watering")
	private LocalDate lastWatering;

	@Column(name = "watering_notification")
	private Boolean wateringNotification = true;

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "stage_id")
	private PlantStage stage;

	@Column(name = "direct_rain")
	private Boolean directRain = false;

	@Enumerated(EnumType.STRING)
	private Room room;

	private String imagePath;
}
