package com.humanin.planpaz.dto;

public record ResponseDTO (String name, String username, String token, String message) {

	// Mantém compatibilidade com quem já construía sem a mensagem
	public ResponseDTO(String name, String username, String token) {
		this(name, username, token, null);
	}
}
