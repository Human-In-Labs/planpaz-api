package com.humanin.planpaz.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.humanin.planpaz.dto.GardenRemindersResponseDTO;
import com.humanin.planpaz.dto.WateringReminderDTO;
import com.humanin.planpaz.dto.WeatherRemindersSyncDTO;
import com.humanin.planpaz.dto.WeatherResponseDTO;
import com.humanin.planpaz.infra.exception.WateringNotAllowedException;
import com.humanin.planpaz.model.GardenPlant;
import com.humanin.planpaz.model.PlantCareLog;
import com.humanin.planpaz.model.User;
import com.humanin.planpaz.model.enums.CareType;
import com.humanin.planpaz.model.enums.Room;
import com.humanin.planpaz.model.enums.WateringLevel;
import com.humanin.planpaz.repositories.GardenPlantRepository;
import com.humanin.planpaz.repositories.PlantCareLogRepository;
import com.humanin.planpaz.repositories.UserRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class WateringService {
	private final GardenPlantRepository gardenPlantRepository;
	private final GardenPlantService gardenPlantService;
	private final PlantCareLogRepository plantCareLogRepository;
	private final UserRepository userRepository;
	private final StreakService streakService;
	private final WeatherService weatherService;
	private final NotificationService notificationService;
	private final FirebasePushNotificationService firebasePushNotificationService;

	// ============================
	// CONCLUIR REGA DE PLANTA DO MEU JARDIM
	// ============================

	@Transactional
	public void registrarRega(UUID id, UUID ownerId) {
		GardenPlant planta = gardenPlantService.buscarPorIdEUsuario(id, ownerId);
		LocalDate hoje = LocalDate.now();

		// Validação da janela de 36 horas
		int intervaloDias = getDaysInterval(
				planta.getPlant() != null ? planta.getPlant().getWateringLevel() : null
		);

		LocalDate dataBase = planta.getLastWatering();
		if (dataBase == null) {
			dataBase = planta.getPlantedAt() != null 
					? planta.getPlantedAt().toLocalDate() 
					: LocalDate.now().minusDays(intervaloDias);
		}

		LocalDate proximaData = dataBase.plusDays(intervaloDias);
		LocalTime horarioRega = LocalTime.of(12, 0);
		if (planta.getOwner() != null && planta.getOwner().getWateringTime() != null) {
			horarioRega = planta.getOwner().getWateringTime();
		}

		LocalDateTime proximaDataHora = LocalDateTime.of(proximaData, horarioRega);
		LocalDateTime limiteFuturo = LocalDateTime.now().plusHours(36);

		if (limiteFuturo.isBefore(proximaDataHora)) {
			throw new WateringNotAllowedException("WATERING_TOO_EARLY",
					"Não é possível concluir lembretes com mais de 36 horas de antecedência.");
		}

		User owner = planta.getOwner();
		// Atualiza o lastWatering para a data do lembrete concluído (ou hoje se estava atrasado)
		planta.setLastWatering(proximaData.isAfter(hoje) ? proximaData : hoje);

		// Processa o Streak e EcoScore centralizado (+10 pts base de rega)
		int pontosGanhos = streakService.processCareActionAndGrantPoints(owner, planta, 10);

		// Registrar log de manejo
		PlantCareLog log = new PlantCareLog();
		log.setGardenPlant(planta);
		log.setUser(owner);
		log.setCareType(CareType.WATERING);
		log.setPointsEarned(pontosGanhos);
		plantCareLogRepository.save(log);

		gardenPlantRepository.save(planta);
		userRepository.save(owner);
	}

	// ============================
	// RETORNAR TODOS OS LEMBRETES DO JARDIM EM LOTE (OPT-02)
	// ============================

	@Transactional(readOnly = true)
	public GardenRemindersResponseDTO getAllGardenReminders(UUID ownerId, int countPerPlant) {
		List<GardenPlant> plants = gardenPlantRepository.findByOwnerId(ownerId);
		if (plants.isEmpty()) {
			return new GardenRemindersResponseDTO(0, List.of());
		}

		boolean arSeco = false;
		try {
			User owner = plants.get(0).getOwner();
			if (owner == null) {
				owner = userRepository.findById(ownerId).orElse(null);
			}
			WeatherResponseDTO clima = consultarClimaLocal(owner, null, null, null);
			if (clima != null && clima.getUmidade() != null && clima.getUmidade() < 40) {
				arSeco = true;
			}
		} catch (Exception ignored) {}

		int safeCount = Math.max(1, countPerPlant);
		List<WateringReminderDTO> allReminders = new ArrayList<>();

		for (GardenPlant plant : plants) {
			if (plant.getWateringNotification() == null || plant.getWateringNotification()) {
				allReminders.addAll(calculateNextWaterings(plant, safeCount, arSeco));
			}
		}

		return new GardenRemindersResponseDTO(plants.size(), allReminders);
	}

	// ============================
	// RETONAR AS PRÓXIMAS 4 REGAS DE UMA PLANTA DO MEU JARDIM
	// ============================

	public List<WateringReminderDTO> getNextWaterings(UUID id, UUID ownerId) {
		GardenPlant planta = gardenPlantService.buscarPorIdEUsuario(id, ownerId);
		boolean arSeco = false;
		try {
			WeatherResponseDTO clima = consultarClimaLocal(planta.getOwner(), null, null, null);
			if (clima != null && clima.getUmidade() != null && clima.getUmidade() < 40) {
				arSeco = true;
			}
		} catch (Exception ignored) {}
		return calculateNextWaterings(planta, 4, arSeco);
	}

	// ============================
	// RETORNAR APENAS A PRÓXIMA REGA
	// ============================

	public WateringReminderDTO getNextWatering(UUID id, UUID ownerId) {
		GardenPlant planta = gardenPlantService.buscarPorIdEUsuario(id, ownerId);
		boolean arSeco = false;
		try {
			WeatherResponseDTO clima = consultarClimaLocal(planta.getOwner(), null, null, null);
			if (clima != null && clima.getUmidade() != null && clima.getUmidade() < 40) {
				arSeco = true;
			}
		} catch (Exception ignored) {}
		List<WateringReminderDTO> regas = calculateNextWaterings(planta, 1, arSeco);
		return regas.isEmpty() ? null : regas.get(0);
	}

	// ========================================
	// Retorna uma lista de WateringReminderDTO com informações das próximas N regas
	// ========================================
	public List<WateringReminderDTO> calculateNextWaterings(GardenPlant planta, int quantidade) {
		return calculateNextWaterings(planta, quantidade, false);
	}

	public List<WateringReminderDTO> calculateNextWaterings(GardenPlant planta, int quantidade, boolean incluirOpcionalArSeco) {
		List<WateringReminderDTO> lembretes = new ArrayList<>();
		if (planta == null || quantidade <= 0) {
			return lembretes;
		}

		int intervaloDias = getDaysInterval(
				planta.getPlant() != null ? planta.getPlant().getWateringLevel() : null
		);

		// data do primeiro lembrete
		LocalDate dataBase = planta.getLastWatering();
		if (dataBase == null) {
			dataBase = planta.getPlantedAt() != null 
					? planta.getPlantedAt().toLocalDate() 
					: LocalDate.now().minusDays(intervaloDias);
		}

		LocalDate proximaData = dataBase.plusDays(intervaloDias);
		LocalDate hoje = LocalDate.now();

		String imagem = (planta.getImagePath() != null && !planta.getImagePath().isBlank())
				? planta.getImagePath()
				: (planta.getPlant() != null ? planta.getPlant().getImagePath() : null);

		String plantNickname = (planta.getNickname() != null && !planta.getNickname().isBlank())
				? planta.getNickname()
				: (planta.getPlant() != null && planta.getPlant().getName() != null ? planta.getPlant().getName() : "Planta");

		DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM");
		DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");

		// busca o horário definido pelo usuário, mas 12:00 como base
		String horario = "12:00";
		if (planta.getOwner() != null && planta.getOwner().getWateringTime() != null) {
			horario = planta.getOwner().getWateringTime().format(timeFormatter);
		}

		// RF24: Em caso de ar seco, adiciona lembrete de rega opcional para hoje se não houver rega hoje
		if (incluirOpcionalArSeco && proximaData.isAfter(hoje)) {
			lembretes.add(new WateringReminderDTO(
					0,
					planta.getId(),
					plantNickname,
					imagem,
					hoje.format(dateFormatter),
					horario,
					"opcional",
					true,
					"Rega opcional recomendada devido ao ar seco."
			));
		}

		// loop de adicionar lembretes regulares
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

			lembretes.add(new WateringReminderDTO(
					i,
					planta.getId(),
					plantNickname,
					imagem,
					dataLembrete.format(dateFormatter),
					horario,
					status,
					false,
					null
			));
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

	// ========================================================
	// RF24: ATUALIZAR LEMBRETES BASEADOS NO CLIMA LOCAL
	// ========================================================

	@Transactional
	public WeatherRemindersSyncDTO atualizarLembretesPorClima(User user, Double latitude, Double longitude, String cidade) {
		WeatherResponseDTO clima = consultarClimaLocal(user, latitude, longitude, cidade);
		if (clima == null) {
			return new WeatherRemindersSyncDTO(false, false, null, null,
					cidade != null ? cidade : "São Paulo", 0, 0,
					"Não foi possível obter os dados meteorológicos no momento.",
					"Mantenha o cronograma habitual de cuidados com suas plantas.");
		}

		boolean isChovendo = clima.isChovendo();
		Integer umidade = clima.getUmidade();
		Double temperatura = clima.getTemperatura();
		boolean isArSeco = umidade != null && umidade < 40;

		List<GardenPlant> plantasDoUsuario = gardenPlantRepository.findByOwnerId(user.getId());
		LocalDate hoje = LocalDate.now();

		int plantasExternasRegadasPelaChuva = 0;
		int lembretesOpcionaisAdicionados = 0;

		// 1. Em caso de chuva: para todas as plantas externas do usuário, se tiver lembrete para hoje, marcar como concluído
		if (isChovendo) {
			for (GardenPlant planta : plantasDoUsuario) {
				boolean isExterna = Boolean.TRUE.equals(planta.getDirectRain())
						|| Room.YARD.equals(planta.getRoom())
						|| Room.BALCONY.equals(planta.getRoom());

				if (isExterna) {
					List<WateringReminderDTO> proximas = calculateNextWaterings(planta, 1, false);
					boolean temLembreteHoje = !proximas.isEmpty() &&
							("hoje".equalsIgnoreCase(proximas.get(0).status()) || "atrasado".equalsIgnoreCase(proximas.get(0).status()));

					if (temLembreteHoje) {
						planta.setLastWatering(hoje);
						planta.setLastCareDate(hoje);

						int pontos = streakService.processCareActionAndGrantPoints(user, planta, 10);

						PlantCareLog logCuidado = new PlantCareLog();
						logCuidado.setGardenPlant(planta);
						logCuidado.setUser(user);
						logCuidado.setCareType(CareType.WATERING);
						logCuidado.setPointsEarned(pontos);
						plantCareLogRepository.save(logCuidado);

						gardenPlantRepository.save(planta);
						plantasExternasRegadasPelaChuva++;
					}
				}
			}

			if (plantasExternasRegadasPelaChuva > 0) {
				userRepository.save(user);
				String titulo = "Chuva na sua região! 🌧️";
				String mensagem = "Identificamos chuva local. A rega de " + plantasExternasRegadasPelaChuva
						+ " planta(s) externa(s) foi concluída automaticamente.";
				notificationService.createNotification(user, titulo, mensagem, "WEATHER_RAIN");
				firebasePushNotificationService.sendPushNotification(user, titulo, mensagem, "WEATHER_RAIN");
			}
		}

		// 2. Em caso de ar seco: para todas as plantas, contabiliza lembretes opcionais
		if (isArSeco) {
			for (GardenPlant planta : plantasDoUsuario) {
				List<WateringReminderDTO> proximas = calculateNextWaterings(planta, 1, false);
				boolean temLembreteHoje = !proximas.isEmpty() && "hoje".equalsIgnoreCase(proximas.get(0).status());
				if (!temLembreteHoje) {
					lembretesOpcionaisAdicionados++;
				}
			}

			if (lembretesOpcionaisAdicionados > 0) {
				String titulo = "Alerta de Ar Seco! ☀️";
				String mensagem = "Umidade em " + umidade + "%. Adicionamos lembretes de rega opcionais para reforçar a hidratação das suas plantas.";
				notificationService.createNotification(user, titulo, mensagem, "WEATHER_DRY");
				firebasePushNotificationService.sendPushNotification(user, titulo, mensagem, "WEATHER_DRY");
			}
		}

		// Monta saídas de instruções climáticas para a página inicial
		String nomeCidade = clima.getCidade() != null ? clima.getCidade() : (cidade != null ? cidade : "Sua região");
		String mensagemAviso;
		String instrucaoClimatica;

		if (isChovendo) {
			mensagemAviso = "Chuva detectada em " + nomeCidade + ".";
			instrucaoClimatica = plantasExternasRegadasPelaChuva > 0
					? "🌧️ Rega natural concluída: " + plantasExternasRegadasPelaChuva
							+ " planta(s) externa(s) foram regadas pela chuva. Verifique pratinhos para drenar excessos!"
					: "🌧️ Chuva na região: Plantas em áreas abertas recebem irrigação natural. Proteja vasos que acumulam muita água.";
		} else if (isArSeco) {
			mensagemAviso = "Alerta de Ar Seco (" + umidade + "% de umidade) em " + nomeCidade + ".";
			instrucaoClimatica = "☀️ Baixa umidade: Suas folhas transpiram com mais intensidade. Lembretes opcionais foram disponibilizados. Borrife água no fim da tarde.";
		} else {
			mensagemAviso = "Condições climáticas estáveis em " + nomeCidade + ".";
			instrucaoClimatica = "🌱 Clima equilibrado (Umidade: " + umidade + "%, Temp: "
					+ Math.round(temperatura != null ? temperatura : 22.0)
					+ "°C). Mantenha seu cronograma regular de rega.";
		}

		return new WeatherRemindersSyncDTO(
				isChovendo,
				isArSeco,
				umidade,
				temperatura,
				nomeCidade,
				plantasExternasRegadasPelaChuva,
				lembretesOpcionaisAdicionados,
				mensagemAviso,
				instrucaoClimatica
		);
	}

	@Transactional(readOnly = true)
	public WeatherRemindersSyncDTO obterInstrucoesClimaticas(User user, Double latitude, Double longitude, String cidade) {
		WeatherResponseDTO clima = consultarClimaLocal(user, latitude, longitude, cidade);
		if (clima == null) {
			return new WeatherRemindersSyncDTO(false, false, null, null,
					cidade != null ? cidade : "São Paulo", 0, 0,
					"Condições climáticas normais.", "Mantenha o cronograma regular de cuidados.");
		}

		boolean isChovendo = clima.isChovendo();
		Integer umidade = clima.getUmidade();
		Double temperatura = clima.getTemperatura();
		boolean isArSeco = umidade != null && umidade < 40;
		String nomeCidade = clima.getCidade() != null ? clima.getCidade() : "Sua região";

		String mensagemAviso;
		String instrucaoClimatica;

		if (isChovendo) {
			mensagemAviso = "Chuva detectada em " + nomeCidade + ".";
			instrucaoClimatica = "🌧️ Chuva na região: Lembretes de rega de plantas externas são concluídos automaticamente pela precipitação natural.";
		} else if (isArSeco) {
			mensagemAviso = "Alerta de Ar Seco (" + umidade + "%) em " + nomeCidade + ".";
			instrucaoClimatica = "☀️ Baixa umidade: O ar seco desidrata plantas rapidamente. Considere uma rega leve opcional e umidifique as folhas.";
		} else {
			mensagemAviso = "Condições favoráveis em " + nomeCidade + ".";
			instrucaoClimatica = "🌱 Clima favorável (Umidade: " + umidade + "%, Temp: "
					+ Math.round(temperatura != null ? temperatura : 22.0)
					+ "°C). Siga o cronograma padrão.";
		}

		return new WeatherRemindersSyncDTO(
				isChovendo,
				isArSeco,
				umidade,
				temperatura,
				nomeCidade,
				0,
				0,
				mensagemAviso,
				instrucaoClimatica
		);
	}

	private WeatherResponseDTO consultarClimaLocal(User user, Double latitude, Double longitude, String cidade) {
		try {
			if (latitude != null && longitude != null) {
				return weatherService.getCurrentWeather(latitude, longitude);
			} else if (cidade != null && !cidade.isBlank()) {
				return weatherService.getCurrentWeather(cidade);
			} else if (user != null && user.getLatitude() != null && user.getLongitude() != null) {
				return weatherService.getCurrentWeather(user.getLatitude(), user.getLongitude());
			} else if (user != null && user.getCityName() != null && !user.getCityName().isBlank()) {
				return weatherService.getCurrentWeather(user.getCityName());
			} else {
				return weatherService.getCurrentWeather("São Paulo");
			}
		} catch (Exception e) {
			log.warn("Erro ao consultar serviço de clima: {}", e.getMessage());
			return null;
		}
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
			return "Chuva detectada na região. A rega das plantas externas é concluída naturalmente!";
		}

		if (clima.getUmidade() != null && clima.getUmidade() > 80) {
			return "Umidade do ar alta (" + clima.getUmidade() + "%). Não é necessário regar hoje.";
		}

		if (clima.getTemperatura() != null && clima.getTemperatura() > 30.0 && clima.getUmidade() != null && clima.getUmidade() < 40) {
			return "Alerta de calor e ar seco! Recomendado regar hoje no final da tarde.";
		}

		return "Condições normais. Siga o cronograma padrão da planta.";
	}
}
