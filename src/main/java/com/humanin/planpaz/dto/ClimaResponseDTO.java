package com.humanin.planpaz.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ClimaResponseDTO {
	private Main main;
	private Weather[] weather;

	public static class Main {
		private double temp;

		@JsonProperty("feels_like")
		private double feelsLike;

		@JsonProperty("temp_min")
		private double tempMin;

		@JsonProperty("temp_max")
		private double tempMax;

		private int humidity;

		// Getters e Setters
		public double getTemp() {
			return temp;
		}

		public void setTemp(double temp) {
			this.temp = temp;
		}

		public double getFeelsLike() {
			return feelsLike;
		}

		public void setFeelsLike(double feelsLike) {
			this.feelsLike = feelsLike;
		}

		public double getTempMin() {
			return tempMin;
		}

		public void setTempMin(double tempMin) {
			this.tempMin = tempMin;
		}

		public double getTempMax() {
			return tempMax;
		}

		public void setTempMax(double tempMax) {
			this.tempMax = tempMax;
		}

		public int getHumidity() {
			return humidity;
		}

		public void setHumidity(int humidity) {
			this.humidity = humidity;
		}
	}

	public static class Weather {
		private String main; // Ex: "Rain", "Clear"
		private String description; // Ex: "céu limpo", "chuva leve"

		// Getters e Setters
		public String getMain() {
			return main;
		}

		public void setMain(String main) {
			this.main = main;
		}

		public String getDescription() {
			return description;
		}

		public void setDescription(String description) {
			this.description = description;
		}
	}

	// Getters
	public Main getMain() {
		return main;
	}

	public void setMain(Main main) {
		this.main = main;
	}

	public Weather[] getWeather() {
		return weather;
	}

	public void setWeather(Weather[] weather) {
		this.weather = weather;
	}

	// Métodos utilitários formatados para o JSON de saída
	public double getTemperatura() {
		return main != null ? main.getTemp() : 0.0;
	}

	public double getSensacaoTermica() {
		return main != null ? main.getFeelsLike() : 0.0;
	}

	public double getTempMin() {
		return main != null ? main.getTempMin() : 0.0;
	}

	public double getTempMax() {
		return main != null ? main.getTempMax() : 0.0;
	}

	public int getUmidade() {
		return main != null ? main.getHumidity() : 0;
	}

	public String getDescricao() {
		if (weather != null && weather.length > 0) {
			return weather[0].getDescription();
		}
		return "";
	}

	public boolean isChovendo() {
		if (weather != null && weather.length > 0) {
			String climaGeral = weather[0].getMain();
			return "Rain".equalsIgnoreCase(climaGeral) || "Drizzle".equalsIgnoreCase(climaGeral);
		}
		return false;
	}
}