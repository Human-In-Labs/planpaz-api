package com.humanin.planpaz.service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.humanin.planpaz.dto.WateringReminderDTO;
import com.humanin.planpaz.dto.WeatherResponseDTO;
import com.humanin.planpaz.infra.exception.BusinessException;
import com.humanin.planpaz.model.GardenPlant;
import com.humanin.planpaz.model.PlantCareLog;
import com.humanin.planpaz.model.User;
import com.humanin.planpaz.model.enums.CareType;
import com.humanin.planpaz.model.enums.WateringLevel;
import com.humanin.planpaz.repositories.GardenPlantRepository;
import com.humanin.planpaz.repositories.PlantCareLogRepository;
import com.humanin.planpaz.repositories.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class WateringService {
	private final GardenPlantRepository gardenPlantRepository;
	private final GardenPlantService gardenPlantService;
	private final AchievementService achievementService;
	private final PlantCareLogRepository plantCareLogRepository;
	private final UserRepository userRepository;

	// ============================
	// CONCLUIR REGA DE PLANTA DO MEU JARDIM
	// ============================

	@Transactional
	public void registrarRega(UUID id, UUID ownerId) {
		GardenPlant planta = gardenPlantService.buscarPorIdEUsuario(id, ownerId);
		LocalDate hoje = LocalDate.now();

		// Validação de rega no máximo 1x ao dia por planta
		if (planta.getLastWatering() != null && planta.getLastWatering().isEqual(hoje)) {
			throw new BusinessException("Você já regou esta planta hoje! Só é possível regá-la 1 vez ao dia.");
		}

		// Atualiza o Streak de cuidados da planta
		int streakActual = planta.getStreakDays() != null ? planta.getStreakDays() : 0;
		LocalDate lastCare = planta.getLastCareDate();

		if (lastCare != null) {
			if (lastCare.equals(hoje.minusDays(1))) {
				streakActual += 1;
			} else if (!lastCare.equals(hoje)) {
				streakActual = 1;
			}
		} else {
			streakActual = 1;
		}

		planta.setStreakDays(streakActual);
		planta.setLastCareDate(hoje);
		planta.setLastWatering(hoje);

		// Cálculo de pontuação: +10 pts por rega + (+2 pts x dias de streak)
		int pontosGanhos = 10 + (2 * streakActual);

		int plantEcoscore = planta.getEcoscore() != null ? planta.getEcoscore() : 0;
		planta.setEcoscore(plantEcoscore + pontosGanhos);

		User owner = planta.getOwner();
		int userEcoscore = owner.getEcoscore() != null ? owner.getEcoscore() : 0;
		owner.setEcoscore(userEcoscore + pontosGanhos);

		// Registrar log de manejo
		PlantCareLog log = new PlantCareLog();
		log.setGardenPlant(planta);
		log.setUser(owner);
		log.setCareType(CareType.WATERING);
		log.setPointsEarned(pontosGanhos);
		plantCareLogRepository.save(log);

		gardenPlantRepository.save(planta);
		userRepository.save(owner);

		// Verificação automática de conquistas
		verificarConquistasDeSequencia(ownerId, planta);
	}

	// ============================
	// RETONAR AS PRÓXIMAS 4 REGAS DE UMA PLANTA DO MEU JARDIM
	// ============================

	public List<WateringReminderDTO> getNextWaterings(UUID id, UUID ownerId) {
		GardenPlant planta = gardenPlantService.buscarPorIdEUsuario(id, ownerId);
		return calculateNextWaterings(planta, 4);
	}

	// ============================
	// RETORNAR APENAS A PRÓXIMA REGA
	// ============================

	public WateringReminderDTO getNextWatering(UUID id, UUID ownerId) {
		GardenPlant planta = gardenPlantService.buscarPorIdEUsuario(id, ownerId);
		List<WateringReminderDTO> regas = calculateNextWaterings(planta, 1);
		return regas.isEmpty() ? null : regas.get(0);
	}

	// ========================================
	// Retorna uma lista de WateringReminderDTO com informações das próximas N regas
	// ========================================
	public List<WateringReminderDTO> calculateNextWaterings(GardenPlant planta, int quantidade) {
		List<WateringReminderDTO> lembretes = new ArrayList<>();
		if (planta == null || quantidade <= 0) {
			return lembretes;
		}

		int intervaloDias = getDaysInterval(planta.getPlant() != null ? planta.getPlant().getWateringLevel() : null);

		// data do primeiro lembrete
		LocalDate dataBase = planta.getLastWatering();
		if (dataBase == null) {
			dataBase = planta.getPlantedAt() != null ? planta.getPlantedAt().toLocalDate()
					: LocalDate.now().minusDays(intervaloDias);
		}

		LocalDate proximaData = dataBase.plusDays(intervaloDias);
		LocalDate hoje = LocalDate.now();

		String imagem = (planta.getImagePath() != null && !planta.getImagePath().isBlank()) ? planta.getImagePath()
				: (planta.getPlant() != null ? planta.getPlant().getImagePath() : null);

		DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM");
		DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");

		// busca o horário definido pelo usuário, mas 12:00 como base
		String horario = "12:00";
		if (planta.getOwner() != null && planta.getOwner().getWateringTime() != null) {
			horario = planta.getOwner().getWateringTime().format(timeFormatter);
		}

		// loop de adicionar lembrete
		for (int i = 1; i <= quantidade; i++) {
			LocalDate dataLembrete = proximaData.plusDays((long) (i - 1) * intervaloDias);

			String status;
			if (dataLembrete.isBefore(hoje)) {
				status = "atrasado";
			} else if (dataLembrete.isEqual(hoje)) {
				status = "hoje";
			} else {
				status = "pendente";
			}

			lembretes.add(new WateringReminderDTO(i, planta.getId(), planta.getNickname(), imagem,
					dataLembrete.format(dateFormatter), horario, status));
		}

		return lembretes;
	}

	private int getDaysInterval(WateringLevel level) {
		if (level == null) {
			return 3;
		}
		return switch (level) {
		case DAILY -> 1;
		case FREQUENT -> 3;
		case WEEKLY -> 7;
		case SPORADIC -> 14;
		};
	}

	public String analisarClima(GardenPlant planta, WeatherResponseDTO clima) {
		if (planta == null) {
			throw new IllegalArgumentException("Planta não pode ser nula.");
		}
		if (clima == null) {
			throw new IllegalArgumentException("Dados meteorológicos não podem ser nulos.");
		}

		if (clima.isChovendo()) {
			planta.setLastWatering(LocalDate.now());
			return "Chuva detectada na região. A rega foi adiada para amanhã!";
		}

		if (clima.getUmidade() > 80) {
			return "Umidade do ar alta (" + clima.getUmidade() + "%). Não é necessário regar hoje.";
		}

		if (clima.getTemperatura() > 30.0 && clima.getUmidade() < 40) {
			return "Alerta de calor e ar seco! Recomendado regar hoje no final da tarde.";
		}

		return "Condições normais. Siga o cronograma padrão da planta.";
	}

	private void verificarConquistasDeSequencia(UUID ownerId, GardenPlant planta) {
		if (planta.getPlantedAt() != null) {
			long diasDeCultivo = ChronoUnit.DAYS.between(planta.getPlantedAt().toLocalDate(), LocalDate.now());

			if (diasDeCultivo >= 3) {
				achievementService.concederCuidarPlanta3Dias(ownerId);
			}
			if (diasDeCultivo >= 5) {
				achievementService.concederCuidarPlanta5Dias(ownerId);
			}
			if (diasDeCultivo >= 10) {
				achievementService.concederCuidarPlanta10Dias(ownerId);
			}
		}
	}
}
