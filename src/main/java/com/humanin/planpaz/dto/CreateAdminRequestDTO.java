package com.humanin.planpaz.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateAdminRequestDTO {

	@NotBlank(message = "O nome é obrigatório.")
	private String name;

	@NotBlank(message = "O e-mail é obrigatório.")
	@Email(message = "Forneça um e-mail válido.")
	private String email;

	@NotBlank(message = "A senha é obrigatória.")
	@Size(min = 4, message = "A senha deve conter pelo menos 4 caracteres.")
	private String password;
}
