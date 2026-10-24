package com.humanin.planpaz.dto;

import java.util.UUID;

import com.humanin.planpaz.model.Admin;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdminResponseDTO {

	private UUID id;
	private String name;
	private String email;
	private String createdAt;

	public static AdminResponseDTO fromEntity(Admin admin) {
		if (admin == null) {
			return null;
		}
		return AdminResponseDTO.builder()
				.id(admin.getId())
				.name(admin.getName())
				.email(admin.getEmail())
				.createdAt(admin.getCreatedAt() != null ? admin.getCreatedAt().toString() : null)
				.build();
	}
}
