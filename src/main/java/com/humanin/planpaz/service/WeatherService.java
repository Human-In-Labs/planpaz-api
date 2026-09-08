package com.humanin.planpaz.service;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;

import com.humanin.planpaz.dto.ForecastResponseDTO;
import com.humanin.planpaz.dto.OpenWeatherForecastResponse;
import com.humanin.planpaz.dto.OpenWeatherResponse;
import com.humanin.planpaz.dto.WeatherResponseDTO;
import com.humanin.planpaz.infra.exception.ResourceNotFoundException;

@Service
public class WeatherService {

	private final RestClient restClient;

	@Value("${weather.api.key}")
	private String apiKey;

	public WeatherService() {
		this.restClient = RestClient.builder().baseUrl("https://api.openweathermap.org/data/2.5").build();
	}

	// ==========================
	// TRAZ O CLIMA ATUAL DA REGIÃO
	// ==========================

	public WeatherResponseDTO getCurrentWeather(Double latitude, Double longitude) {
		try {
			OpenWeatherResponse response = this.restClient.get()
					.uri("/weather?lat={lat}&lon={lon}&units=metric&lang=pt_br&appid={apiKey}", latitude, longitude, apiKey)
					.retrieve()
					.body(OpenWeatherResponse.class);

			return mapToWeatherResponseDTO(response);
		} catch (HttpClientErrorException.NotFound e) {
			throw new ResourceNotFoundException("Condições climáticas não encontradas para as coordenadas informadas.");
		}
	}

	public WeatherResponseDTO getCurrentWeather(String cidade) {
		try {
			OpenWeatherResponse response = this.restClient.get()
					.uri("/weather?q={cidade}&units=metric&lang=pt_br&appid={apiKey}", cidade, apiKey)
					.retrieve()
					.body(OpenWeatherResponse.class);

			return mapToWeatherResponseDTO(response);
		} catch (HttpClientErrorException.NotFound e) {
			throw new ResourceNotFoundException("Cidade não encontrada: " + cidade);
		}
	}

	// ==========================
	// TRAZ O CLIMA DE 3 EM 3 HORAS (ESSE É O DISPONÍVEL PARA O PLANO GRÁTIS) PARA O PRÓXIMO DIA
	// ==========================

	public List<ForecastResponseDTO> getNextDayForecast(Double latitude, Double longitude) {
		try {
			OpenWeatherForecastResponse response = this.restClient.get()
					.uri("/forecast?lat={lat}&lon={lon}&cnt=8&units=metric&lang=pt_br&appid={apiKey}", latitude, longitude, apiKey)
					.retrieve()
					.body(OpenWeatherForecastResponse.class);

			return mapToForecastResponseDTOList(response);
		} catch (HttpClientErrorException.NotFound e) {
			throw new ResourceNotFoundException("Previsão meteorológica não encontrada para as coordenadas informadas.");
		}
	}

	// ==========================
	// OUTRA ASSINATURA PELO MESMO MÉTODO, É PREFERÍVEL USAR COM LATITUDE E LONGITUDE PORQUE A PESQUISA POR CIDADE NÃO É MAIS ATUALIZADA PELA API, APESAR DE FUNCIONAR
	// ==========================

	public List<ForecastResponseDTO> getNextDayForecast(String cidade) {
		try {
			OpenWeatherForecastResponse response = this.restClient.get()
					.uri("/forecast?q={cidade}&cnt=8&units=metric&lang=pt_br&appid={apiKey}", cidade, apiKey)
					.retrieve()
					.body(OpenWeatherForecastResponse.class);

			return mapToForecastResponseDTOList(response);
		} catch (HttpClientErrorException.NotFound e) {
			throw new ResourceNotFoundException("Cidade não encontrada: " + cidade);
		}
	}
	
	// ==========================
	// MÉTODOS PARA ADAPTAR A RESPOSTA DA API DE CLIMA
	// ==========================
	
	private WeatherResponseDTO mapToWeatherResponseDTO(OpenWeatherResponse response) {
		if (response == null || response.main() == null) {
			throw new ResourceNotFoundException("Dados meteorológicos não encontrados.");
		}

		String descricao = "";
		String icone = null;
		boolean isChovendo = false;

		if (response.weather() != null && !response.weather().isEmpty()) {
			OpenWeatherResponse.WeatherData weatherData = response.weather().get(0);
			descricao = weatherData.description() != null ? weatherData.description() : "";
			icone = weatherData.icon();
			isChovendo = response.weather().stream().anyMatch(w -> {
				String main = w.main();
				return "Rain".equalsIgnoreCase(main) || "Drizzle".equalsIgnoreCase(main) || "Thunderstorm".equalsIgnoreCase(main);
			});
		}

		if (!isChovendo && response.rain() != null) {
			if ((response.rain().oneHour() != null && response.rain().oneHour() > 0)
					|| (response.rain().threeHours() != null && response.rain().threeHours() > 0)) {
				isChovendo = true;
			}
		}

		Double probabilidadeChuva = null;
		if (response.pop() != null) {
			probabilidadeChuva = Math.round(response.pop() * 100.0 * 10.0) / 10.0;
		} else if (isChovendo) {
			probabilidadeChuva = 100.0;
		}

		String dataHora = response.dt() != null
				? Instant.ofEpochSecond(response.dt()).atZone(ZoneId.systemDefault()).format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))
				: LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

		return new WeatherResponseDTO(
				descricao,
				response.main().temp(),
				response.main().feelsLike(),
				response.main().tempMin(),
				response.main().tempMax(),
				response.main().humidity(),
				probabilidadeChuva,
				isChovendo,
				icone,
				response.name(),
				dataHora
		);
	}

	private List<ForecastResponseDTO> mapToForecastResponseDTOList(OpenWeatherForecastResponse response) {
		if (response == null || response.list() == null || response.list().isEmpty()) {
			throw new ResourceNotFoundException("Previsão meteorológica não encontrada.");
		}

		return response.list().stream().map(item -> {
			String descricao = "";
			String icone = null;
			boolean isChovendo = false;

			if (item.weather() != null && !item.weather().isEmpty()) {
				OpenWeatherForecastResponse.WeatherData weatherData = item.weather().get(0);
				descricao = weatherData.description() != null ? weatherData.description() : "";
				icone = weatherData.icon();
				isChovendo = item.weather().stream().anyMatch(w -> {
					String main = w.main();
					return "Rain".equalsIgnoreCase(main) || "Drizzle".equalsIgnoreCase(main) || "Thunderstorm".equalsIgnoreCase(main);
				});
			}

			if (!isChovendo && item.rain() != null && item.rain().threeHours() != null && item.rain().threeHours() > 0) {
				isChovendo = true;
			}

			Double probabilidadeChuva = item.pop() != null ? Math.round(item.pop() * 100.0 * 10.0) / 10.0 : 0.0;

			String dataHora = item.dtTxt();
			String horario = "";
			if (dataHora != null && dataHora.length() >= 16) {
				horario = dataHora.substring(11, 16);
			} else if (item.dt() != null) {
				LocalDateTime ldt = Instant.ofEpochSecond(item.dt()).atZone(ZoneId.systemDefault()).toLocalDateTime();
				dataHora = ldt.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
				horario = ldt.format(DateTimeFormatter.ofPattern("HH:mm"));
			}

			Double temp = item.main() != null ? item.main().temp() : null;
			Double feelsLike = item.main() != null ? item.main().feelsLike() : null;
			Double tempMin = item.main() != null ? item.main().tempMin() : null;
			Double tempMax = item.main() != null ? item.main().tempMax() : null;
			Integer humidity = item.main() != null ? item.main().humidity() : null;

			return new ForecastResponseDTO(
					dataHora,
					horario,
					descricao,
					temp,
					feelsLike,
					tempMin,
					tempMax,
					humidity,
					probabilidadeChuva,
					isChovendo,
					icone
			);
		}).toList();
	}
}