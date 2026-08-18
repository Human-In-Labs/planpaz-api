package com.humanin.planpaz.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import com.humanin.planpaz.dto.ClimaResponseDTO;

@Service
public class ClimaService {

	private final RestClient restClient;

	@Value("${weather.api.key}")
	private String apiKey;

	public ClimaService() {
		this.restClient = RestClient.builder().baseUrl("https://api.openweathermap.org/data/2.5").build();
	}

	public ClimaResponseDTO buscarClimaPorCidade(String cidade) {
		ClimaResponseDTO response = this.restClient.get()
				.uri("/weather?q={cidade}&units=metric&lang=pt_br&appid={apiKey}", cidade, apiKey).retrieve()
				.body(ClimaResponseDTO.class);

		if (response == null || response.getMain() == null) {
			throw new RuntimeException("Não foi possível obter os dados do clima.");
		}

		return response;
	}
}