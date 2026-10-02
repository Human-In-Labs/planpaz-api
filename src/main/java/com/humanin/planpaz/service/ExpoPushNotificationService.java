package com.humanin.planpaz.service;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class ExpoPushNotificationService {

	private static final String EXPO_PUSH_URL = "https://exp.host/--/api/v2/push/send";
	private final ObjectMapper objectMapper;
	private final HttpClient httpClient = HttpClient.newBuilder()
			.connectTimeout(Duration.ofSeconds(5))
			.build();

	/**
	 * Envia uma notificação push para o token informado (Expo Push Token ou FCM Token).
	 */
	public void sendPushNotification(String pushToken, String title, String body) {
		if (pushToken == null || pushToken.isBlank()) {
			log.debug("Token de push nulo ou vazio. Notificação não enviada.");
			return;
		}

		try {
			var payload = java.util.Map.of(
					"to", pushToken.trim(),
					"title", title,
					"body", body,
					"sound", "default"
			);

			String jsonBody = objectMapper.writeValueAsString(payload);

			HttpRequest request = HttpRequest.newBuilder()
					.uri(URI.create(EXPO_PUSH_URL))
					.header("Content-Type", "application/json")
					.header("Accept", "application/json")
					.POST(HttpRequest.BodyPublishers.ofString(jsonBody))
					.timeout(Duration.ofSeconds(10))
					.build();

			httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString())
					.thenAccept(response -> {
						if (response.statusCode() == 200) {
							log.info("Push notification enviada com sucesso para token {}", pushToken);
						} else {
							log.warn("Erro ao enviar push notification. HTTP Code {}: {}", response.statusCode(), response.body());
						}
					})
					.exceptionally(ex -> {
						log.error("Exceção ao enviar push notification para {}: {}", pushToken, ex.getMessage());
						return null;
					});

		} catch (Exception e) {
			log.error("Erro ao preparar notificação push: {}", e.getMessage());
		}
	}
}
