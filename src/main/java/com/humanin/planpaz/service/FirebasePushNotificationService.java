package com.humanin.planpaz.service;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.time.LocalDateTime;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.messaging.AndroidConfig;
import com.google.firebase.messaging.AndroidNotification;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.MessagingErrorCode;
import com.google.firebase.messaging.Notification;
import com.humanin.planpaz.model.User;
import com.humanin.planpaz.repositories.UserRepository;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class FirebasePushNotificationService {

	private final UserRepository userRepository;

	@Value("${firebase.credentials.path:firebase-service-account.json}")
	private String credentialsPath;

	@Value("${firebase.credentials.base64:}")
	private String credentialsBase64;

	@Value("${firebase.credentials.json:}")
	private String credentialsJson;

	@PostConstruct
	public void init() {
		try {
			if (!FirebaseApp.getApps().isEmpty()) {
				return;
			}

			InputStream serviceAccount = resolveCredentialsStream();
			if (serviceAccount == null) {
				log.warn("[FIREBASE] Arquivo de credenciais '{}' não encontrado. "
						+ "Coloque o JSON da Service Account na raiz do projeto planpaz-api (ou configure FIREBASE_CREDENTIALS_PATH) para ativar o envio de Push Notifications.",
						credentialsPath);
				return;
			}

			try (InputStream is = serviceAccount) {
				FirebaseOptions options = FirebaseOptions.builder()
						.setCredentials(GoogleCredentials.fromStream(is))
						.build();
				FirebaseApp.initializeApp(options);
				log.info("[FIREBASE] Firebase Admin SDK inicializado com sucesso.");
			}
		} catch (Exception e) {
			log.error("[FIREBASE] Erro ao inicializar Firebase Admin SDK: {}", e.getMessage());
		}
	}

	private InputStream resolveCredentialsStream() {
		try {
			if (credentialsBase64 != null && !credentialsBase64.isBlank()) {
				log.info("[FIREBASE] Carregando credenciais a partir da variável FIREBASE_CREDENTIALS_BASE64.");
				byte[] decoded = Base64.getDecoder().decode(credentialsBase64.trim());
				return new ByteArrayInputStream(decoded);
			}
			if (credentialsJson != null && !credentialsJson.isBlank()) {
				log.info("[FIREBASE] Carregando credenciais a partir da variável FIREBASE_CREDENTIALS_JSON.");
				return new ByteArrayInputStream(credentialsJson.trim().getBytes(StandardCharsets.UTF_8));
			}
			File file = new File(credentialsPath);
			if (file.exists() && file.isFile()) {
				return new FileInputStream(file);
			}
			ClassPathResource resource = new ClassPathResource(credentialsPath);
			if (resource.exists()) {
				return resource.getInputStream();
			}
		} catch (Exception e) {
			log.error("[FIREBASE] Não foi possível carregar credenciais: {}", e.getMessage());
		}
		return null;
	}

	/**
	 * Envia uma notificação push via Firebase Cloud Messaging (FCM) sem persistir no banco.
	 * Os metadados também são enviados no bloco data para armazenamento local no dispositivo.
	 */
	public void sendPushNotification(User recipient, String title, String body, String type) {
		if (recipient == null) {
			return;
		}
		String fcmToken = recipient.getFcmToken();
		if (fcmToken == null || fcmToken.isBlank()) {
			log.debug("[FIREBASE] Usuário {} não possui fcmToken cadastrado. Push ignorado.", recipient.getUsername());
			return;
		}
		if (FirebaseApp.getApps().isEmpty()) {
			log.debug("[FIREBASE] FirebaseApp não inicializado. Push '{}' para {} não enviado.", title, recipient.getUsername());
			return;
		}

		final String cleanToken = fcmToken.trim();
		final UUID recipientId = recipient.getId();
		final String safeType = type != null ? type : "notification";

		CompletableFuture.runAsync(() -> {
			try {
				Message message = Message.builder()
						.setToken(cleanToken)
						.setNotification(Notification.builder()
								.setTitle(title)
								.setBody(body)
								.build())
						.setAndroidConfig(AndroidConfig.builder()
								.setPriority(AndroidConfig.Priority.HIGH)
								.setNotification(AndroidNotification.builder()
										.setChannelId("planpaz_default_channel")
										.setSound("default")
										.build())
								.build())
						.putData("id", UUID.randomUUID().toString())
						.putData("title", title)
						.putData("body", body)
						.putData("type", safeType)
						.putData("createdAt", LocalDateTime.now().toString())
						.build();

				String responseId = FirebaseMessaging.getInstance().send(message);
				log.info("[FIREBASE] Push enviado para {} (msgId: {})", recipient.getUsername(), responseId);
			} catch (FirebaseMessagingException e) {
				MessagingErrorCode code = e.getMessagingErrorCode();
				log.warn("[FIREBASE] Falha ao enviar push para {} (código {}): {}", recipient.getUsername(), code, e.getMessage());
				if (code == MessagingErrorCode.UNREGISTERED || code == MessagingErrorCode.INVALID_ARGUMENT) {
					limparTokenInvalido(recipientId);
				}
			} catch (Exception e) {
				log.error("[FIREBASE] Erro inesperado ao enviar push para {}: {}", recipient.getUsername(), e.getMessage());
			}
		});
	}

	private void limparTokenInvalido(UUID userId) {
		if (userId == null) {
			return;
		}
		try {
			userRepository.findById(userId).ifPresent(user -> {
				user.setFcmToken(null);
				userRepository.save(user);
				log.info("[FIREBASE] fcmToken inválido removido do usuário {}", user.getUsername());
			});
		} catch (Exception e) {
			log.warn("[FIREBASE] Não foi possível limpar fcmToken inválido do usuário {}: {}", userId, e.getMessage());
		}
	}
}
