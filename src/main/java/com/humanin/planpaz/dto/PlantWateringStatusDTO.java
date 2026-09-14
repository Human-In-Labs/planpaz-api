package com.humanin.planpaz.dto;

/**
 * Representa o status de rega de uma planta para compor o e-mail
 * de resumo diário enviado às 9h.
 */
public record PlantWateringStatusDTO(String nickname, String status) {
}
