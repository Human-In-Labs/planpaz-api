package com.humanin.planpaz.model;

import java.time.LocalDateTime;
import java.util.UUID;

import org.hibernate.annotations.CreationTimestamp;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(
	name = "user_achievements",
	uniqueConstraints = {
		@UniqueConstraint(name = "uk_user_achievement", columnNames = {"user_id", "achievement_id"})
	}
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserAchievement {

	@Id
	@GeneratedValue(strategy = GenerationType.UUID)
	private UUID id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id", nullable = false)
	@JsonIgnore
	private User user;

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "achievement_id", nullable = false)
	private Achievement achievement;

	@CreationTimestamp
	@Column(name = "unlocked_at", nullable = false, updatable = false)
	private LocalDateTime unlockedAt;

	public UserAchievement(User user, Achievement achievement) {
		this.user = user;
		this.achievement = achievement;
	}

	// Facilita serialização JSON direta para o front-end
	public String getDescription() {
		return achievement != null ? achievement.getName() + ": " + achievement.getDescription() : "";
	}
}
