package com.humanin.planpaz.model;

import java.time.LocalDateTime;
import java.util.UUID;

import org.hibernate.annotations.CreationTimestamp;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.humanin.planpaz.model.enums.ExperienceLevel;
import com.humanin.planpaz.model.enums.MainGoal;
import com.humanin.planpaz.model.enums.RoomLuminosity;
import com.humanin.planpaz.model.enums.SpaceDisponibility;
import com.humanin.planpaz.model.enums.TimeAvailability;

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

// anotações do JPA
@Entity
@Table(name = "users")
// anotações do Lombok
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class User {
	// atributos obrigatórios
	@Id
	@GeneratedValue(strategy = GenerationType.UUID)
	private UUID id;

	@Column(nullable = false)
	private String name;
	
	@Column(nullable = false)
	private String username;

	@Column(nullable = false)
	private String email;

	@Column(nullable = false)
	@JsonIgnore
	private String password;

	@Column(nullable = false)
	@CreationTimestamp // quando criar um usuario, ja puxa o horario e a data sozinho
	private LocalDateTime createdAt;

	private LocalDateTime wateringTime;
	@Column(name = "ecoscore")
	private Integer ecoScore;

	private String city;

	private String tokenDispositivo;

	// opcionais
	private String bio;
	private LocalDateTime birthdate;

	// preferências para personalização
	@Enumerated(EnumType.STRING)
	private MainGoal mainGoal;

	@Enumerated(EnumType.STRING)
	private RoomLuminosity roomLuminosity;

	@Enumerated(EnumType.STRING)
	private SpaceDisponibility spaceDisponibility;

	@Enumerated(EnumType.STRING)
	private ExperienceLevel experienceLevel;

	@Enumerated(EnumType.STRING)
	private TimeAvailability timeAvailability;

}
