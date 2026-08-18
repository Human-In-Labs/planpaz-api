package com.humanin.planpaz.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

public record OpenWeatherResponse(MainData main, List<WeatherData> weather) {
	public record MainData(Double temp, @JsonProperty("feels_like") Double feelsLike,
			@JsonProperty("temp_min") Double tempMin, @JsonProperty("temp_max") Double tempMax, Integer humidity) {
	}

	public record WeatherData(String main, String description) {
	}
}