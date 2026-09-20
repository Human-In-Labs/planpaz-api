package com.humanin.planpaz.dto;

public record LocationResponseDTO(
        String bairro,
        String cidade,
        String estado,
        String estadoCodigo,
        String pais,
        String paisCodigo,
        double latitude,
        double longitude
) {
}