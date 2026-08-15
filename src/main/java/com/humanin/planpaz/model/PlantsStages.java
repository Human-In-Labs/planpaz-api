package com.humanin.planpaz.model;

import java.util.UUID;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

//anotações do JPA
@Entity
@Table(name = "plants_stages")
//anotações do Lombok
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PlantsStages {

	// campos da tabela
	@Id
	@GeneratedValue(strategy = GenerationType.UUID)
	private UUID id;
	private UUID plantId;
	private String name;
	private int days;
	private String description;
	private String imagePath;
	
}
