package com.humanin.planpaz.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public record OpenWeatherForecastResponse(
    String cod,
    Integer message,
    Integer cnt,
    List<ForecastItem> list,
    City city
) {
    @JsonIgnoreProperties(ignoreUnknown = true)
    public record ForecastItem(
        Long dt,
        MainData main,
        List<WeatherData> weather,
        Double pop,
        Rain rain,
        @JsonProperty("dt_txt") String dtTxt
    ) {}

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record MainData(
        Double temp,
        @JsonProperty("feels_like") Double feelsLike,
        @JsonProperty("temp_min") Double tempMin,
        @JsonProperty("temp_max") Double tempMax,
        Integer humidity
    ) {}

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record WeatherData(
        Integer id,
        String main,
        String description,
        String icon
    ) {}

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Rain(
        @JsonProperty("3h") Double threeHours
    ) {}

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record City(
        Long id,
        String name,
        String country
    ) {}
}
