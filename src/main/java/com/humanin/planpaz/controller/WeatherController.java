package com.humanin.planpaz.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.humanin.planpaz.dto.WeatherResponseDTO;
import com.humanin.planpaz.service.WeatherService;

@RestController
@RequestMapping("/api/clima")
public class WeatherController {

	private final WeatherService climaService;

	public WeatherController(WeatherService climaService) {
		this.climaService = climaService;
	}

	@GetMapping
	public ResponseEntity<WeatherResponseDTO> testarClima(@RequestParam String cidade) {
		WeatherResponseDTO clima = climaService.buscarClimaPorCidade(cidade);
		return ResponseEntity.ok(clima);
	}
}