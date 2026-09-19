package com.humanin.planpaz.dto;

public record ApiResponseDTO(String message, boolean success) {
	public static ApiResponseDTO ok(String message) {
		return new ApiResponseDTO(message, true);
	}

	public static ApiResponseDTO error(String message) {
		return new ApiResponseDTO(message, false);
	}
}
