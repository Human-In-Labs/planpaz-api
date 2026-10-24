package com.humanin.planpaz.dto;

public record WeatherRemindersSyncDTO(
    boolean isChovendo,
    boolean isArSeco,
    Integer umidade,
    Double temperatura,
    String cidade,
    int plantasExternasRegadasPelaChuva,
    int lembretesOpcionaisAdicionados,
    String mensagemAviso,
    String instrucaoClimatica
) {}
