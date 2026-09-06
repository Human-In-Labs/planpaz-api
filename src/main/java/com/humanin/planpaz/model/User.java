package com.humanin.planpaz.model;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import org.hibernate.annotations.CreationTimestamp;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.humanin.planpaz.model.enums.ExperienceLevel;
import com.humanin.planpaz.model.enums.Gender;
import com.humanin.planpaz.model.enums.MainGoal;
import com.humanin.planpaz.model.enums.RoomLuminosity;
import com.humanin.planpaz.model.enums.SpaceAvailability;
import com.humanin.planpaz.model.enums.TimeAvailability;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "users")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class User {

	@Id
	@GeneratedValue(strategy = GenerationType.UUID)
	private UUID id;

	@Column(nullable = false)
	private String name;

	@Column(nullable = false, unique = true)
	private String username;

	@Column(nullable = false, unique = true)
	private String email;

	@Column(nullable = false)
	@JsonIgnore
	private String password;

	@Column(name = "created_at", nullable = false, updatable = false)
	@CreationTimestamp
	private LocalDateTime createdAt;

	@Column(columnDefinition = "TEXT")
	private String bio;

	private LocalDate birthdate;

	@Enumerated(EnumType.STRING)
	private Gender gender;

	@Enumerated(EnumType.STRING)
	@Column(name = "main_goal")
	private MainGoal mainGoal;

	@ElementCollection(targetClass = RoomLuminosity.class)
	@CollectionTable(name = "user_room_luminosity", joinColumns = @JoinColumn(name = "user_id"))
	@Enumerated(EnumType.STRING)
	@Column(name = "luminosity")
	private Set<RoomLuminosity> roomLuminosity = new HashSet<>();

	@ElementCollection(targetClass = SpaceAvailability.class)
	@CollectionTable(name = "user_space_availability", joinColumns = @JoinColumn(name = "user_id"))
	@Enumerated(EnumType.STRING)
	@Column(name = "space")
	private Set<SpaceAvailability> spaceAvailability = new HashSet<>();

	@Enumerated(EnumType.STRING)
	@Column(name = "experience_level")
	private ExperienceLevel experienceLevel;

	@Enumerated(EnumType.STRING)
	@Column(name = "time_availability")
	private TimeAvailability timeAvailability;

	@Column(name = "watering_time")
	private LocalTime wateringTime;

	@Column(name = "ecoscore")
	private Integer ecoscore;

	private String city;

	@Column(name = "fcm_token")
	private String fcmToken;
}
