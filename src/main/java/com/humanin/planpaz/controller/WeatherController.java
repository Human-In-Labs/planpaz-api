package com.humanin.planpaz.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.humanin.planpaz.dto.ForecastResponseDTO;
import com.humanin.planpaz.dto.WeatherResponseDTO;
import com.humanin.planpaz.service.WeatherService;

@RestController
@RequestMapping("/api/clima")
public class WeatherController {

	private final WeatherService weatherService;

	public WeatherController(WeatherService weatherService) {
		this.weatherService = weatherService;
	}

	@GetMapping("/current")
	public ResponseEntity<WeatherResponseDTO> getCurrentWeather(
			@RequestParam(required = false) Double latitude,
			@RequestParam(required = false) Double longitude,
			@RequestParam(required = false) String cidade) {
		if (latitude != null && longitude != null) {
			return ResponseEntity.ok(weatherService.getCurrentWeather(latitude, longitude));
		} else if (cidade != null && !cidade.isBlank()) {
			return ResponseEntity.ok(weatherService.getCurrentWeather(cidade));
		}
		throw new IllegalArgumentException("Informe latitude e longitude ou o nome da cidade.");
	}

	@GetMapping("/forecast")
	public ResponseEntity<List<ForecastResponseDTO>> getNextDayForecast(
			@RequestParam(required = false) Double latitude,
			@RequestParam(required = false) Double longitude,
			@RequestParam(required = false) String cidade) {
		if (latitude != null && longitude != null) {
			return ResponseEntity.ok(weatherService.getNextDayForecast(latitude, longitude));
		} else if (cidade != null && !cidade.isBlank()) {
			return ResponseEntity.ok(weatherService.getNextDayForecast(cidade));
		}
		throw new IllegalArgumentException("Informe latitude e longitude ou o nome da cidade.");
	}

	@GetMapping
	public ResponseEntity<WeatherResponseDTO> testarClima(
			@RequestParam(required = false) Double latitude,
			@RequestParam(required = false) Double longitude,
			@RequestParam(required = false) String cidade) {
		return getCurrentWeather(latitude, longitude, cidade);
	}
}