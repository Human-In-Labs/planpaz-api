package com.humanin.planpaz.model;

import java.io.Serializable;
import java.util.Objects;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Embeddable
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class FollowersId implements Serializable {

	@Column(name = "follower_id")
	private UUID followerId;

	@Column(name = "followed_id")
	private UUID followedId;

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (o == null || getClass() != o.getClass())
			return false;
		FollowersId that = (FollowersId) o;
		return Objects.equals(followerId, that.followerId) && Objects.equals(followedId, that.followedId);
	}

	@Override
	public int hashCode() {
		return Objects.hash(followerId, followedId);
	}
}