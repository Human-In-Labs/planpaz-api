package com.humanin.planpaz.model;

import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "followers")
@Getter
@Setter
@NoArgsConstructor
public class Followers {

	@Id
	@GeneratedValue(strategy = GenerationType.UUID)
	@Column(name = "id", nullable = false)
	private UUID id;

	@ManyToOne
	@JoinColumn(name = "follower_id", nullable = false)
	private User follower;

	@ManyToOne
	@JoinColumn(name = "followed_id", nullable = false)
	private User followed;

	public Followers(User follower, User followed) {
		this.follower = follower;
		this.followed = followed;
	}

	@PrePersist
	public void autofill() {
		if (this.id == null) {
			this.id = UUID.randomUUID();
		}
	}
}