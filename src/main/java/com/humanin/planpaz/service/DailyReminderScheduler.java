package com.humanin.planpaz.service;

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
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class DailyReminderScheduler {

	private final GardenPlantRepository gardenPlantRepository;
	private final WateringService wateringService;
	private final EmailService emailService;

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
