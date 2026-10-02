package com.humanin.planpaz.dto;

import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AdminLoginResponseDTO {

	private String token;
	private UUID adminId;
	private String name;
	private String email;
}
