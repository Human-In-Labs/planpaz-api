package com.humanin.planpaz.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record VerifyCodeRequestDTO(
		@NotBlank(message = "O e-mail é obrigatório.")
		@Email(message = "Formato de e-mail inválido.")
		String email,

		@NotBlank(message = "O código é obrigatório.")
		String code
) {}
