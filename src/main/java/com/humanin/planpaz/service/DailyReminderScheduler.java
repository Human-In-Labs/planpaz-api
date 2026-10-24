package com.humanin.planpaz.service;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.humanin.planpaz.dto.PlantWateringStatusDTO;
import com.humanin.planpaz.dto.WateringReminderDTO;
import com.humanin.planpaz.model.GardenPlant;
import com.humanin.planpaz.model.User;
import com.humanin.planpaz.repositories.GardenPlantRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Job diário que envia, para cada usuário com pelo menos uma planta com
 * notificação de rega ativada, um e-mail de resumo com o status de rega de
 * cada planta + a frase do dia. Roda todo dia às 9h (horário de Brasília).
 * Também executa de hora em hora o envio de notificações push de rega via FCM.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class DailyReminderScheduler {

	private static final ZoneId ZONE_BRASILIA = ZoneId.of("America/Sao_Paulo");

	private final GardenPlantRepository gardenPlantRepository;
	private final WateringService wateringService;
	private final EmailService emailService;
	private final FirebasePushNotificationService firebasePushNotificationService;
	private final com.humanin.planpaz.repositories.UserRepository userRepository;
	private final StreakService streakService;

	@Scheduled(cron = "0 5 0 * * *", zone = "America/Sao_Paulo")
	@Transactional
	public void verificarEQuebrarStreaksExpiradosAgendado() {
		log.info("Verificando streaks expirados por inatividade à meia-noite...");
		List<User> usuariosComStreak = userRepository.findAll().stream()
				.filter(u -> u.getStreakDays() != null && u.getStreakDays() > 0)
				.toList();

		int zerados = 0;
		for (User user : usuariosComStreak) {
			if (streakService.getActiveStreak(user) == 0) {
				user.setStreakDays(0);
				userRepository.save(user);
				zerados++;
			}
		}

		if (zerados > 0) {
			log.info("{} usuário(s) tiveram a sequência de ofensiva zerada por inatividade no dia anterior.", zerados);
		}
	}

	@Scheduled(cron = "0 0 8,14,20 * * *", zone = "America/Sao_Paulo")
	@Transactional
	public void atualizarLembretesPorClimaAgendado() {
		log.info("Iniciando verificação agendada de clima para atualização de lembretes (RF24)...");
		List<User> usuariosComLocal = userRepository.findAll().stream()
				.filter(u -> (u.getLatitude() != null && u.getLongitude() != null)
						|| (u.getCityName() != null && !u.getCityName().isBlank()))
				.toList();

		for (User user : usuariosComLocal) {
			try {
				wateringService.atualizarLembretesPorClima(user, user.getLatitude(), user.getLongitude(), user.getCityName());
			} catch (Exception e) {
				log.warn("Falha ao atualizar lembretes por clima para o usuário {}: {}", user.getUsername(), e.getMessage());
			}
		}
	}

	@Scheduled(cron = "0 0 * * * *", zone = "America/Sao_Paulo")
	@Transactional(readOnly = true)
	public void enviarNotificacoesPushDeRega() {
		int horaAtual = ZonedDateTime.now(ZONE_BRASILIA).getHour();
		log.info("Verificando lembretes push de rega para o horário {:02d}:00h...", horaAtual);

		List<GardenPlant> plantasNotificaveis = gardenPlantRepository.findByWateringNotificationTrue();

		Map<User, List<GardenPlant>> plantasPorUsuario = plantasNotificaveis.stream()
				.filter(p -> p.getOwner() != null)
				.collect(Collectors.groupingBy(GardenPlant::getOwner));

		int pushesDisparados = 0;
		for (Map.Entry<User, List<GardenPlant>> entry : plantasPorUsuario.entrySet()) {
			User user = entry.getKey();
			int horaRegaUsuario = user.getWateringTime() != null ? user.getWateringTime().getHour() : 12;

			if (horaRegaUsuario != horaAtual) {
				continue;
			}

			List<GardenPlant> plantasParaRegarHoje = entry.getValue().stream()
					.filter(planta -> {
						String status = statusDaPlanta(planta);
						return "hoje".equalsIgnoreCase(status) || "atrasado".equalsIgnoreCase(status);
					})
					.toList();

			if (plantasParaRegarHoje.isEmpty()) {
				continue;
			}

			String body;
			if (plantasParaRegarHoje.size() == 1) {
				GardenPlant unica = plantasParaRegarHoje.get(0);
				String nome = unica.getNickname() != null && !unica.getNickname().isBlank()
						? unica.getNickname()
						: (unica.getPlant() != null ? unica.getPlant().getName() : "sua planta");
				body = "Sua planta " + nome + " precisa ser regada hoje!";
			} else {
				String nomes = plantasParaRegarHoje.stream()
						.map(p -> p.getNickname() != null && !p.getNickname().isBlank()
								? p.getNickname()
								: (p.getPlant() != null ? p.getPlant().getName() : "Planta"))
						.limit(3)
						.collect(Collectors.joining(", "));
				body = "Você tem " + plantasParaRegarHoje.size() + " plantas precisando de rega hoje (" + nomes + ").";
			}

			firebasePushNotificationService.sendPushNotification(
					user,
					"Hora de regar! 🌱",
					body,
					"WATERING"
			);
			pushesDisparados++;
		}

		log.info("Verificação de push de rega ({:02d}:00h) finalizada. {} usuário(s) notificado(s).",
				horaAtual, pushesDisparados);
	}

	@Scheduled(cron = "0 0 9 * * *", zone = "America/Sao_Paulo")
	@Transactional(readOnly = true)
	public void enviarResumoDiarioDeRega() {
		log.info("Iniciando envio do resumo diário de rega (09h)...");

		List<GardenPlant> plantasNotificaveis = gardenPlantRepository.findByWateringNotificationTrue();

		Map<User, List<GardenPlant>> plantasPorUsuario = plantasNotificaveis.stream()
				.collect(Collectors.groupingBy(GardenPlant::getOwner));

		int enviados = 0;
		for (Map.Entry<User, List<GardenPlant>> entry : plantasPorUsuario.entrySet()) {
			User user = entry.getKey();
			List<GardenPlant> plantas = entry.getValue();

			try {
				List<PlantWateringStatusDTO> statusPlantas = plantas.stream()
						.map(planta -> new PlantWateringStatusDTO(planta.getNickname(), statusDaPlanta(planta)))
						.toList();

				emailService.enviarResumoDiario(user.getEmail(), user.getName(), statusPlantas);
				enviados++;
			} catch (Exception e) {
				// Uma falha de e-mail para um usuário não pode travar o envio para os demais
				log.error("Falha ao enviar resumo diário para {} ({}): {}", user.getUsername(), user.getEmail(),
						e.getMessage());
			}
		}

		log.info("Resumo diário de rega finalizado. {} e-mail(s) enviado(s) de {} usuário(s) elegível(is).", enviados,
				plantasPorUsuario.size());
	}

	private String statusDaPlanta(GardenPlant planta) {
		List<WateringReminderDTO> proximas = wateringService.calculateNextWaterings(planta, 1);
		return proximas.isEmpty() ? "pendente" : proximas.get(0).status();
	}
}
