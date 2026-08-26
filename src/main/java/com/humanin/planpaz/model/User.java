package com.humanin.planpaz.model;

import java.time.LocalDateTime;
import java.util.UUID;

import org.hibernate.annotations.CreationTimestamp;

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
import lombok.NoArgsConstructor;

// anotações do JPA
@Entity
@Table(name = "users")
// anotações do Lombok
//@Getter
//@Setter
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
	private String email;

	@Column(nullable = false)
	private String password;

	@Column(nullable = false)
	@CreationTimestamp // quando criar um usuario, ja puxa o horario e a data sozinho
	private LocalDateTime createdAt;

	// opcionais
	private String bio;
	private LocalDateTime birthdate;
	private int gender; // int porque no banco é 0 e 1

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


	// getters e setters
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

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public LocalDateTime getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(LocalDateTime createdAt) {
		this.createdAt = createdAt;
	}

	public String getBio() {
		return bio;
	}

	public void setBio(String bio) {
		this.bio = bio;
	}

	public LocalDateTime getbirthdate() {
		return birthdate;
	}

	public void setbirthdate(LocalDateTime birthdate) {
		this.birthdate = birthdate;
	}

	public int getGender() {
		return gender;
	}

	public void setGender(int gender) {
		this.gender = gender;
	}	

	public MainGoal getMainGoal() {
		return mainGoal;
	}

	public void setMainGoal(MainGoal mainGoal) {
		this.mainGoal = mainGoal;
	}

	public RoomLuminosity getRoomLuminosity() {
		return roomLuminosity;
	}

	public void setRoomLuminosity(RoomLuminosity roomLuminosity) {
		this.roomLuminosity = roomLuminosity;
	}

	public SpaceDisponibility getSpaceDisponibility() {
		return spaceDisponibility;
	}

	public void setSpaceDisponibility(SpaceDisponibility spaceDisponibility) {
		this.spaceDisponibility = spaceDisponibility;
	}

	public ExperienceLevel getExperienceLevel() {
		return experienceLevel;
	}

	public void setExperienceLevel(ExperienceLevel experienceLevel) {
		this.experienceLevel = experienceLevel;
	}

	public TimeAvailability getTimeAvailability() {
		return timeAvailability;
	}

	public void setTimeAvailability(TimeAvailability timeAvailability) {
		this.timeAvailability = timeAvailability;
	}

}
