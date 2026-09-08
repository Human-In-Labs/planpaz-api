package com.humanin.planpaz.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record WeatherResponseDTO(
    String descricao,
    Double temperatura,
    Double sensacaoTermica,
    Double tempMin,
    Double tempMax,
    Integer umidade,
    Double probabilidadeChuva,
    Boolean chovendo,
    String icone,
    String cidade,
    String dataHora
) {
    public Double getTemperatura() {
        return temperatura != null ? temperatura : 0.0;
    }

    public Double getSensacaoTermica() {
        return sensacaoTermica != null ? sensacaoTermica : 0.0;
    }

    public Double getTempMin() {
        return tempMin != null ? tempMin : 0.0;
    }

    public Double getTempMax() {
        return tempMax != null ? tempMax : 0.0;
    }

    public Integer getUmidade() {
        return umidade != null ? umidade : 0;
    }

    public String getDescricao() {
        return descricao != null ? descricao : "";
    }

    public Boolean isChovendo() {
        return Boolean.TRUE.equals(chovendo);
    }

    public Double getProbabilidadeChuva() {
        return probabilidadeChuva;
    }

    public String getIcone() {
        return icone;
    }

    public String getCidade() {
        return cidade;
    }

    public String getDataHora() {
        return dataHora;
    }
}