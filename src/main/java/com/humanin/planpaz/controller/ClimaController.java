package com.humanin.planpaz.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.humanin.planpaz.dto.ClimaResponseDTO;
import com.humanin.planpaz.service.ClimaService;

@RestController
@RequestMapping("/api/clima")
public class ClimaController {

	private final ClimaService climaService;

	public ClimaController(ClimaService climaService) {
		this.climaService = climaService;
	}

	@GetMapping
	public ResponseEntity<ClimaResponseDTO> testarClima(@RequestParam String cidade) {
		ClimaResponseDTO clima = climaService.buscarClimaPorCidade(cidade);
		return ResponseEntity.ok(clima);
	}
}