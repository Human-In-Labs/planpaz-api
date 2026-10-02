package com.humanin.planpaz.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ResetPasswordRequestDTO(
		@NotBlank(message = "O e-mail é obrigatório.")
		@Email(message = "Formato de e-mail inválido.")
		String email,

		@NotBlank(message = "O código é obrigatório.")
		String code,

		@NotBlank(message = "A nova senha é obrigatória.")
		@Size(min = 6, message = "A nova senha deve conter no mínimo 6 caracteres.")
		String newPassword
) {}
