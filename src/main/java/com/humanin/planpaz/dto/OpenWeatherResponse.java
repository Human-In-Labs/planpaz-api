package com.humanin.planpaz.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public record OpenWeatherResponse(
        Long dt,
        String name,
        MainData main,
        List<WeatherData> weather,
        Rain rain,
        Double pop,
        Integer timezone
) {

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record MainData(
            Double temp,
            @JsonProperty("feels_like") Double feelsLike,
            @JsonProperty("temp_min") Double tempMin,
            @JsonProperty("temp_max") Double tempMax,
            Integer humidity
    ) {
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record WeatherData(
            Integer id,
            String main,
            String description,
            String icon
    ) {
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Rain(
            @JsonProperty("1h") Double oneHour,
            @JsonProperty("3h") Double threeHours
    ) {
    }
}