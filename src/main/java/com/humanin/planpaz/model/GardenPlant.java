package com.humanin.planpaz.model;

import java.time.LocalDate;
import java.util.UUID;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

//anotações do JPA
@Entity
@Table(name = "garden_plant")
//anotações do Lombok
//@Getter
//@Setter
@AllArgsConstructor
@NoArgsConstructor
public class GardenPlant extends Plant {
	// campos da tabela
	@Id
	@GeneratedValue(strategy = GenerationType.UUID)
	private UUID id;

	@ManyToOne
	@JoinColumn(name = "owner_id") /// chave estrangeira para o usuario
	private User owner;
	@ManyToOne
	@JoinColumn(name = "plant_id") // chave estrangeira para a planta do catalogo
	private Plant plant;

	private String nickName;
	private LocalDate plantedAt;
	private int stage;
	private LocalDate lastWatering; // adicionei isso pq precisamos saber quando foi a ulti,a rega

	public UUID getId() {
		return id;
	}

	public void setId(UUID id) {
		this.id = id;
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

	public String getNickName() {
		return nickName;
	}

	public void setNickName(String plantNickName) {
		this.nickName = plantNickName;
	}

	public LocalDate getPlantedAt() {
		return plantedAt;
	}

	public void setPlantedAt(LocalDate plantedAt) {
		this.plantedAt = plantedAt;
	}

	public int getStage() {
		return stage;
	}

	public void setStage(int stage) {
		this.stage = stage;
	}

	public LocalDate getLastWatering() {
		return lastWatering;
	}

	public void setLastWatering(LocalDate lastWatering) {
		this.lastWatering = lastWatering;
	}

}
