package com.humanin.planpaz.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.humanin.planpaz.dto.ForecastResponseDTO;
import com.humanin.planpaz.dto.WeatherRemindersSyncDTO;
import com.humanin.planpaz.dto.WeatherResponseDTO;
import com.humanin.planpaz.infra.exception.BusinessException;
import com.humanin.planpaz.model.User;
import com.humanin.planpaz.service.WateringService;
import com.humanin.planpaz.service.WeatherService;

@RestController
@RequestMapping("/api/clima")
public class WeatherController {

	private final WeatherService weatherService;
	private final WateringService wateringService;

	public WeatherController(WeatherService weatherService, WateringService wateringService) {
		this.weatherService = weatherService;
		this.wateringService = wateringService;
	}

	private User getAuthenticatedUser(Authentication authentication) {
		if (authentication != null && authentication.getPrincipal() instanceof User user) {
			return user;
		}
		return null;
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

	// ========================================================
	// RF24: SINCRONIZAÇÃO DE CLIMA E ATUALIZAÇÃO DE LEMBRETES
	// ========================================================

	@PostMapping("/sync-reminders")
	public ResponseEntity<WeatherRemindersSyncDTO> syncWeatherReminders(
			@RequestParam(required = false) Double latitude,
			@RequestParam(required = false) Double longitude,
			@RequestParam(required = false) String cidade,
			Authentication authentication) {
		User user = getAuthenticatedUser(authentication);
		if (user == null) {
			throw new BusinessException("Acesso negado: Usuário não autenticado.");
		}

		WeatherRemindersSyncDTO resultado = wateringService.atualizarLembretesPorClima(user, latitude, longitude, cidade);
		return ResponseEntity.ok(resultado);
	}

	@GetMapping("/instructions")
	public ResponseEntity<WeatherRemindersSyncDTO> getWeatherInstructions(
			@RequestParam(required = false) Double latitude,
			@RequestParam(required = false) Double longitude,
			@RequestParam(required = false) String cidade,
			Authentication authentication) {
		User user = getAuthenticatedUser(authentication);
		if (user == null) {
			throw new BusinessException("Acesso negado: Usuário não autenticado.");
		}

		WeatherRemindersSyncDTO resultado = wateringService.obterInstrucoesClimaticas(user, latitude, longitude, cidade);
		return ResponseEntity.ok(resultado);
	}
}