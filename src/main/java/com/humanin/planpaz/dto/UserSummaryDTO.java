package com.humanin.planpaz.dto;

import java.util.UUID;

public record UserSummaryDTO(UUID id, String name, String username, String email, String avatarUrl) {
	public UserSummaryDTO(UUID id, String name, String username, String email) {
		this(id, name, username, email, null);
	}
}
