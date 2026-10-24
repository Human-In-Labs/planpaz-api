package com.humanin.planpaz.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * DTO para cadastro inicial (registro normal) de usuário com validação de dados.
 */
public record RegisterRequestDTO(
    @NotBlank(message = "O nome é obrigatório.")
    @Size(max = 100, message = "O nome deve ter no máximo 100 caracteres.")
    String name,

    @NotBlank(message = "O nome de usuário é obrigatório.")
    @Size(min = 3, max = 30, message = "O nome de usuário deve ter entre 3 e 30 caracteres.")
    String username,

    @NotBlank(message = "O e-mail é obrigatório.")
    @Email(message = "O e-mail informado é inválido.")
    String email,

    @NotBlank(message = "A senha é obrigatória.")
    @Size(min = 6, message = "A senha deve ter no mínimo 6 caracteres.")
    String password
) {}
