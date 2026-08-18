package com.humanin.planpaz.model;

import java.time.LocalDate;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "garden_plant")
public class GardenPlant {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private UUID id;

	@Column(name = "nick_name")
	private String nickName;

	@Column(name = "last_watering")
	private LocalDate lastWatering;

	@Column(name = "planted_at")
	private LocalDate plantedAt;

	private Integer stage;

	@ManyToOne
	@JoinColumn(name = "owner_id")
	private User owner;

	@ManyToOne
	@JoinColumn(name = "plant_id")
	private Plant plant;

	public UUID getId() {
		return id;
	}

	public void setId(UUID id) {
		this.id = id;
	}

	public String getNickName() {
		return nickName;
	}

	public void setNickName(String nickName) {
		this.nickName = nickName;
	}

	public LocalDate getLastWatering() {
		return lastWatering;
	}

	public void setLastWatering(LocalDate lastWatering) {
		this.lastWatering = lastWatering;
	}

	public LocalDate getPlantedAt() {
		return plantedAt;
	}

	public void setPlantedAt(LocalDate plantedAt) {
		this.plantedAt = plantedAt;
	}

	public Integer getStage() {
		return stage;
	}

	public void setStage(Integer stage) {
		this.stage = stage;
	}

	public User getOwner() {
		return owner;
	}

	public void setOwner(User owner) {
		this.owner = owner;
	}

	public Plant getPlant() {
		return plant;
	}

	public void setPlant(Plant plant) {
		this.plant = plant;
	}

}
