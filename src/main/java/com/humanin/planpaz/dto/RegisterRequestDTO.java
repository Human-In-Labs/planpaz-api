package com.humanin.planpaz.dto;

/**
 * DTO para cadastro inicial (registro normal) de usuário.
 */
public record RegisterRequestDTO(
    String name,
    String username,
    String email,
    String password
) {}
