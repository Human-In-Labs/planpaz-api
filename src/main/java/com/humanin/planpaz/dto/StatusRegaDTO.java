package com.humanin.planpaz.dto;

import java.util.UUID;

public record StatusRegaDTO(
    UUID plantaId, 
    String nomePlanta, 
    String cidade, 
    Double temperatura, 
    Integer umidade,
    Boolean chovendo, 
    String recomendacao
) {
    // Em Java Records, os getters (plantaId(), nomePlanta(), etc.) 
    // já são gerados automaticamente pelo compilador.
}