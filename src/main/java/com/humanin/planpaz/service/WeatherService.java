package com.humanin.planpaz.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import com.humanin.planpaz.dto.WeatherResponseDTO;

@Service
public class WeatherService {

	private final RestClient restClient;

	@Value("${weather.api.key}")
	private String apiKey;

	public WeatherService() {
		this.restClient = RestClient.builder().baseUrl("https://api.openweathermap.org/data/2.5").build();
	}

	public WeatherResponseDTO buscarClimaPorCidade(String cidade) {
		WeatherResponseDTO response = this.restClient.get()
				.uri("/weather?q={cidade}&units=metric&lang=pt_br&appid={apiKey}", cidade, apiKey).retrieve()
				.body(WeatherResponseDTO.class);

		if (response == null || response.getMain() == null) {
			throw new RuntimeException("Não foi possível obter os dados do clima.");
		}

		return response;
	}
}