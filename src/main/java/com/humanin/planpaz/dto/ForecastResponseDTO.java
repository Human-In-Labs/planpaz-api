package com.humanin.planpaz.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record ForecastResponseDTO(
    String dataHora,
    String horario,
    String descricao,
    Double temperatura,
    Double sensacaoTermica,
    Double tempMin,
    Double tempMax,
    Integer umidade,
    Double probabilidadeChuva,
    Boolean chovendo,
    String icone
) {}
