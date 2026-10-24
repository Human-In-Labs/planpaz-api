package com.humanin.planpaz.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
  
import com.humanin.planpaz.dto.GardenPlantCreateDTO;
import com.humanin.planpaz.dto.GardenPlantResponseDTO;
import com.humanin.planpaz.dto.GardenPlantUpdateDTO;
import com.humanin.planpaz.dto.PlantStageResponseDTO;
import com.humanin.planpaz.dto.PlantStatsDTO;
import com.humanin.planpaz.dto.StageSuggestionDTO;
import com.humanin.planpaz.infra.exception.BusinessException;
import com.humanin.planpaz.infra.exception.ResourceNotFoundException;
import com.humanin.planpaz.model.Follow;
import com.humanin.planpaz.model.GardenPlant;
import com.humanin.planpaz.model.Plant;
import com.humanin.planpaz.model.PlantCareLog;
import com.humanin.planpaz.model.PlantStage;
import com.humanin.planpaz.model.User;
import com.humanin.planpaz.model.enums.CareType;
import com.humanin.planpaz.model.enums.Size;
import com.humanin.planpaz.repositories.FollowRepository;
import com.humanin.planpaz.repositories.GardenPlantRepository;
import com.humanin.planpaz.repositories.PlantCareLogRepository;
import com.humanin.planpaz.repositories.PlantRepository;
import com.humanin.planpaz.repositories.PlantStageRepository;
import com.humanin.planpaz.repositories.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class GardenPlantService {

	private static final int FERTILIZE_COOLDOWN_DAYS = 14;
	private static final int PRUNE_COOLDOWN_DAYS = 30;

	private final GardenPlantRepository gardenPlantRepository;
	private final PlantRepository plantRepository;
	private final PlantStageRepository plantStageRepository;
	private final AchievementService achievementService;
	private final PlantCareLogRepository plantCareLogRepository;
	private final UserRepository userRepository;
	private final StreakService streakService;
	private final FollowRepository followRepository;
	private final FirebasePushNotificationService firebasePushNotificationService;

	// ADICIONAR COM DTO
	@Transactional
	public GardenPlantResponseDTO adicionarPlanta(GardenPlantCreateDTO dto, UUID ownerId) {
		if (ownerId == null) {
			throw new BusinessException("Proprietário da planta não informado.");
		}

		User realOwner = userRepository.findById(ownerId).orElseThrow(
				() -> new ResourceNotFoundException("Usuário proprietário não encontrado."));

		UUID plantId = dto.getResolvedPlantId();
		if (plantId == null) {
			throw new BusinessException("Espécie de planta não informada.");
		}
		Plant realPlant = plantRepository.findById(plantId).orElseThrow(
				() -> new ResourceNotFoundException("Espécie de planta não encontrada com o ID informado."));

		PlantStage realStage = null;
		UUID stageId = dto.getResolvedStageId();
		if (stageId != null) {
			realStage = plantStageRepository.findById(stageId).orElse(null);
		}

		if (gardenPlantRepository.existsByOwnerIdAndNicknameIgnoreCase(ownerId, dto.nickname())) {
			throw new BusinessException("Já existe uma planta com esse apelido.");
		}

		GardenPlant gardenPlant = new GardenPlant();
		gardenPlant.setOwner(realOwner);
		gardenPlant.setPlant(realPlant);
		gardenPlant.setStage(realStage);
		gardenPlant.setNickname(dto.nickname());
		gardenPlant.setRoom(dto.room());
		gardenPlant.setDirectRain(dto.directRain() != null ? dto.directRain() : false);
		gardenPlant.setWateringNotification(dto.wateringNotification() != null ? dto.wateringNotification() : true);
		gardenPlant.setImagePath(dto.imagePath());
		gardenPlant.setPlantedAt(dto.plantedAt() != null ? dto.plantedAt() : LocalDateTime.now());

		LocalDate hoje = LocalDate.now(StreakService.ZONE_BRASILIA);
		gardenPlant.setLastWatering(hoje);
		gardenPlant.setLastCareDate(hoje);

		int pontosIniciais = 110;
		gardenPlant.setEcoscore(pontosIniciais);

		GardenPlant saved = gardenPlantRepository.save(gardenPlant);

		// Registra a interação com o próprio jardim e atualiza a ofensiva do usuário
		streakService.processCareActionAndGrantPoints(realOwner, saved, pontosIniciais);

		try {
			PlantCareLog log = new PlantCareLog();
			log.setGardenPlant(saved);
			log.setUser(saved.getOwner());
			log.setCareType(CareType.WATERING);
			log.setPointsEarned(10);
			plantCareLogRepository.save(log);
		} catch (Exception e) {
			System.err.println("[LOG] Erro ao registrar log de rega inicial: " + e.getMessage());
		}

		try {
			verificarConquistasDeCultivo(ownerId);
		} catch (Exception e) {
			System.err.println("[ACHIEVEMENTS] Erro ao verificar conquistas após adicionar planta: " + e.getMessage());
		}

		try {
			notificarSeguidoresNovaPlanta(realOwner, saved);
		} catch (Exception e) {
			System.err.println("[PUSH] Erro ao notificar seguidores sobre nova planta: " + e.getMessage());
		}

		return GardenPlantResponseDTO.fromEntity(saved);
	}

	// ADICIONAR (LEGADO / ENTIDADE)
	@Transactional
	public GardenPlant adicionarPlanta(GardenPlant gardenPlant) {
		if (gardenPlant.getOwner() == null || gardenPlant.getOwner().getId() == null) {
			throw new BusinessException("Proprietário da planta não informado.");
		}

		UUID ownerId = gardenPlant.getOwner().getId();
		User realOwner = userRepository.findById(ownerId).orElseThrow(
				() -> new ResourceNotFoundException("Usuário proprietário não encontrado."));
		gardenPlant.setOwner(realOwner);

		if (gardenPlant.getPlant() != null && gardenPlant.getPlant().getId() != null) {
			Plant realPlant = plantRepository.findById(gardenPlant.getPlant().getId()).orElseThrow(
					() -> new ResourceNotFoundException("Espécie de planta não encontrada com o ID informado."));
			gardenPlant.setPlant(realPlant);
		}

		if (gardenPlant.getStage() != null && gardenPlant.getStage().getId() != null) {
			PlantStage realStage = plantStageRepository.findById(gardenPlant.getStage().getId()).orElse(null);
			gardenPlant.setStage(realStage);
		} else {
			gardenPlant.setStage(null);
		}

		if (gardenPlantRepository.existsByOwnerIdAndNicknameIgnoreCase(ownerId, gardenPlant.getNickname())) {
			throw new BusinessException("Já existe uma planta com esse apelido.");
		}

		if (gardenPlant.getPlantedAt() == null) {
			gardenPlant.setPlantedAt(LocalDateTime.now());
		}

		LocalDate hoje = LocalDate.now(StreakService.ZONE_BRASILIA);
		gardenPlant.setLastWatering(hoje);
		gardenPlant.setLastCareDate(hoje);

		int pontosIniciais = 110;
		gardenPlant.setEcoscore(pontosIniciais);

		GardenPlant saved = gardenPlantRepository.save(gardenPlant);

		// Registra a interação com o próprio jardim e atualiza a ofensiva do usuário
		streakService.processCareActionAndGrantPoints(realOwner, saved, pontosIniciais);

		try {
			PlantCareLog log = new PlantCareLog();
			log.setGardenPlant(saved);
			log.setUser(saved.getOwner());
			log.setCareType(CareType.WATERING);
			log.setPointsEarned(10);
			plantCareLogRepository.save(log);
		} catch (Exception e) {
			System.err.println("[LOG] Erro ao registrar log de rega inicial: " + e.getMessage());
		}

		try {
			verificarConquistasDeCultivo(ownerId);
		} catch (Exception e) {
			System.err.println("[ACHIEVEMENTS] Erro ao verificar conquistas após adicionar planta: " + e.getMessage());
		}

		try {
			notificarSeguidoresNovaPlanta(realOwner, saved);
		} catch (Exception e) {
			System.err.println("[PUSH] Erro ao notificar seguidores sobre nova planta: " + e.getMessage());
		}

		return saved;
	}

	// LISTAR COM DTO
	@Transactional(readOnly = true)
	public List<GardenPlantResponseDTO> listarPorUsuarioDTO(UUID ownerId) {
		return gardenPlantRepository.findByOwnerId(ownerId)
				.stream()
				.map(GardenPlantResponseDTO::fromEntity)
				.toList();
	}

	// LISTAR (ENTIDADE)
	@Transactional(readOnly = true)
	public List<GardenPlant> listarPorUsuario(UUID ownerId) {
		return gardenPlantRepository.findByOwnerId(ownerId);
	}

	// BUSCAR POR ID E USUÁRIO (DTO)
	@Transactional(readOnly = true)
	public GardenPlantResponseDTO buscarDTOPorIdEUsuario(UUID id, UUID ownerId) {
		GardenPlant gp = buscarPorIdEUsuario(id, ownerId);
		return GardenPlantResponseDTO.fromEntity(gp);
	}

	// BUSCAR POR ID E USUÁRIO (ENTIDADE)
	@Transactional(readOnly = true)
	public GardenPlant buscarPorIdEUsuario(UUID id, UUID ownerId) {
		return gardenPlantRepository.findByIdAndOwnerId(id, ownerId)
				.orElseThrow(() -> new ResourceNotFoundException("Planta não encontrada para este usuário."));
	}

	@Transactional(readOnly = true)
	public Optional<GardenPlant> buscarPorId(UUID id) {
		return gardenPlantRepository.findById(id);
	}

	// EDITAR COM DTO
	@Transactional
	public GardenPlantResponseDTO editarPlanta(UUID id, UUID ownerId, GardenPlantUpdateDTO dto) {
		GardenPlant planta = gardenPlantRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Planta não encontrada com ID: " + id));

		if (!planta.getOwner().getId().equals(ownerId)) {
			throw new BusinessException("Esta planta não pertence ao usuário autenticado.");
		}

		if (dto.nickname() != null && !dto.nickname().isBlank()) {
			if (gardenPlantRepository.existsByOwnerIdAndNicknameIgnoreCaseAndIdNot(ownerId, dto.nickname(), id)) {
				throw new BusinessException("Já existe uma planta com esse apelido.");
			}
			planta.setNickname(dto.nickname());
		}

		UUID stageId = dto.getResolvedStageId();
		if (stageId != null) {
			PlantStage realStage = plantStageRepository.findById(stageId).orElse(null);
			planta.setStage(realStage);
		}

		if (dto.room() != null) {
			planta.setRoom(dto.room());
		}
		if (dto.directRain() != null) {
			planta.setDirectRain(dto.directRain());
		}
		if (dto.wateringNotification() != null) {
			planta.setWateringNotification(dto.wateringNotification());
		}
		if (dto.imagePath() != null) {
			planta.setImagePath(dto.imagePath());
		}
		if (dto.lastWatering() != null) {
			planta.setLastWatering(dto.lastWatering());
		}

		GardenPlant saved = gardenPlantRepository.save(planta);
		verificarConquistasDeEstagio(ownerId, planta.getStage());

		// Registra a edição da planta como interação válida com o próprio jardim
		streakService.processCareActionAndGrantPoints(planta.getOwner(), saved, 0);

		return GardenPlantResponseDTO.fromEntity(saved);
	}

	// EDITAR (LEGADO / ENTIDADE)
	@Transactional
	public GardenPlant editarPlanta(UUID id, UUID ownerId, GardenPlant gardenPlant) {
		GardenPlant planta = gardenPlantRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Planta não encontrada com ID: " + id));

		if (!planta.getOwner().getId().equals(ownerId)) {
			throw new BusinessException("Esta planta não pertence ao usuário autenticado.");
		}

		if (gardenPlantRepository.existsByOwnerIdAndNicknameIgnoreCaseAndIdNot(ownerId, gardenPlant.getNickname(),
				id)) {
			throw new BusinessException("Já existe uma planta com esse apelido.");
		}

		if (gardenPlant.getNickname() != null) {
			planta.setNickname(gardenPlant.getNickname());
		}
		if (gardenPlant.getStage() != null) {
			planta.setStage(gardenPlant.getStage());
		}
		if (gardenPlant.getRoom() != null) {
			planta.setRoom(gardenPlant.getRoom());
		}
		if (gardenPlant.getDirectRain() != null) {
			planta.setDirectRain(gardenPlant.getDirectRain());
		}
		if (gardenPlant.getWateringNotification() != null) {
			planta.setWateringNotification(gardenPlant.getWateringNotification());
		}
		if (gardenPlant.getImagePath() != null) {
			planta.setImagePath(gardenPlant.getImagePath());
		}

		if (gardenPlant.getLastWatering() != null) {
			planta.setLastWatering(gardenPlant.getLastWatering());
		}

		GardenPlant saved = gardenPlantRepository.save(planta);

		// VERIFICAÇÃO AUTOMÁTICA DE CONQUISTAS (Estágio da Planta)
		verificarConquistasDeEstagio(ownerId, planta.getStage());

		// Registra a edição da planta como interação válida com o próprio jardim
		streakService.processCareActionAndGrantPoints(planta.getOwner(), saved, 0);

		return saved;
	}

	// EXCLUIR
	@Transactional
	public void excluirPlanta(UUID id, UUID ownerId) {
		GardenPlant planta = gardenPlantRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Planta não encontrada com ID: " + id));

		if (!planta.getOwner().getId().equals(ownerId)) {
			throw new BusinessException("Esta planta não pertence ao usuário autenticado.");
		}

		plantCareLogRepository.deleteByGardenPlantId(id);
		gardenPlantRepository.delete(planta);
	}

	// ==========================================
	// ADUBAR PLANTA (+25 ECOSCORE COM COOLDOWN)
	// ==========================================
	@Transactional
	public void adubarPlanta(UUID id, UUID ownerId) {
		GardenPlant planta = buscarPorIdEUsuario(id, ownerId);
		LocalDate hoje = LocalDate.now();

		if (planta.getLastFertilizing() != null) {
			long diasDesdeUltima = ChronoUnit.DAYS.between(planta.getLastFertilizing(), hoje);
			if (diasDesdeUltima < FERTILIZE_COOLDOWN_DAYS) {
				long diasRestantes = FERTILIZE_COOLDOWN_DAYS - diasDesdeUltima;
				throw new BusinessException("Essa planta já foi adubada recentemente. Você poderá adubá-la novamente em " + diasRestantes + " dia(s).");
			}
		}

		planta.setLastFertilizing(hoje);
		User owner = planta.getOwner();

		// Processa streak e EcoScore (+25 pts base)
		int pontosGanhos = streakService.processCareActionAndGrantPoints(owner, planta, 25);

		PlantCareLog log = new PlantCareLog();
		log.setGardenPlant(planta);
		log.setUser(owner);
		log.setCareType(CareType.FERTILIZING);
		log.setPointsEarned(pontosGanhos);
		plantCareLogRepository.save(log);

		gardenPlantRepository.save(planta);
		userRepository.save(owner);
	}

	// ==========================================
	// PODAR PLANTA (+15 ECOSCORE COM COOLDOWN)
	// ==========================================
	@Transactional
	public void podarPlanta(UUID id, UUID ownerId) {
		GardenPlant planta = buscarPorIdEUsuario(id, ownerId);
		LocalDate hoje = LocalDate.now();

		if (planta.getLastPruning() != null) {
			long diasDesdeUltima = ChronoUnit.DAYS.between(planta.getLastPruning(), hoje);
			if (diasDesdeUltima < PRUNE_COOLDOWN_DAYS) {
				long diasRestantes = PRUNE_COOLDOWN_DAYS - diasDesdeUltima;
				throw new BusinessException("✂️ Essa planta foi podada recentemente. Aguarde mais " + diasRestantes + " dia(s) para realizar uma nova poda.");
			}
		}

		planta.setLastPruning(hoje);
		User owner = planta.getOwner();

		// Processa streak e EcoScore (+15 pts base)
		int pontosGanhos = streakService.processCareActionAndGrantPoints(owner, planta, 15);

		PlantCareLog log = new PlantCareLog();
		log.setGardenPlant(planta);
		log.setUser(owner);
		log.setCareType(CareType.PRUNING);
		log.setPointsEarned(pontosGanhos);
		plantCareLogRepository.save(log);

		gardenPlantRepository.save(planta);
		userRepository.save(owner);
	}

	// ==========================================
	// ESTATÍSTICAS DA PLANTA (CO₂ + ECOSCORE)
	// ==========================================
	@Transactional(readOnly = true)
	public PlantStatsDTO getPlantStats(UUID id, UUID ownerId) {
		GardenPlant planta = buscarPorIdEUsuario(id, ownerId);

		long cultivationDays = 0;
		if (planta.getPlantedAt() != null) {
			cultivationDays = Math.max(0, ChronoUnit.DAYS.between(planta.getPlantedAt().toLocalDate(), LocalDate.now()));
		}

		// Cálculo do Sequestro de CO₂ proporcional ao porte e dias de cultivo
		double co2PerYear = 50.0; // Pequena / Média (padrão)
		if (planta.getPlant() != null && planta.getPlant().getSize() == Size.LARGE) {
			co2PerYear = 1300.0; // Grande: 1,3kg/ano (1300g)
		}

		double dailyCo2Rate = co2PerYear / 365.0;
		double rawCo2Grams = cultivationDays * dailyCo2Rate;
		double co2Grams = BigDecimal.valueOf(rawCo2Grams).setScale(1, RoundingMode.HALF_UP).doubleValue();

		int ecoScore = planta.getEcoscore() != null ? planta.getEcoscore() : 0;
		int streakDays = planta.getStreakDays() != null ? planta.getStreakDays() : 0;

		return new PlantStatsDTO(
				planta.getId(),
				co2Grams,
				ecoScore,
				cultivationDays,
				streakDays,
				planta.getLastWatering(),
				planta.getLastFertilizing(),
				planta.getLastPruning()
		);
	}

	// ==========================================
	// SUGESTÃO DE MUDANÇA DE ESTÁGIO
	// ==========================================
	@Transactional(readOnly = true)
	public Optional<StageSuggestionDTO> getStageSuggestion(UUID id, UUID ownerId) {
		GardenPlant planta = buscarPorIdEUsuario(id, ownerId);
		PlantStage estagioAtual = planta.getStage();

		if (planta.getPlantedAt() == null || estagioAtual == null || planta.getPlant() == null) {
			return Optional.empty();
		}

		long cultivationDays = Math.max(0, ChronoUnit.DAYS.between(planta.getPlantedAt().toLocalDate(), LocalDate.now()));
		List<PlantStage> estagios = plantStageRepository.findByPlantIdOrderByOrderAsc(planta.getPlant().getId());

		int indiceIdeal = calcularIndiceEstagioIdeal(estagios, cultivationDays);
		int indiceAtual = indiceDoEstagio(estagios, estagioAtual.getId());

		// Só sugere avanço: a planta pode ter sido cadastrada já em estágio avançado
		if (indiceIdeal < 0 || indiceAtual < 0 || indiceIdeal <= indiceAtual) {
			return Optional.empty();
		}

		return Optional.of(new StageSuggestionDTO(
				planta.getId(),
				cultivationDays,
				PlantStageResponseDTO.fromEntity(estagioAtual),
				PlantStageResponseDTO.fromEntity(estagios.get(indiceIdeal))
		));
	}

	// Cada estágio cobre [início, início + days). Estágio com days nulo é tratado como terminal.
	static int calcularIndiceEstagioIdeal(List<PlantStage> estagios, long diasDecorridos) {
		int indiceIdeal = -1;
		long acumulado = 0;
		for (int i = 0; i < estagios.size(); i++) {
			indiceIdeal = i;
			Integer dias = estagios.get(i).getDays();
			if (dias == null) {
				break;
			}
			acumulado += dias;
			if (diasDecorridos < acumulado) {
				break;
			}
		}
		return indiceIdeal;
	}

	private static int indiceDoEstagio(List<PlantStage> estagios, UUID stageId) {
		for (int i = 0; i < estagios.size(); i++) {
			if (estagios.get(i).getId().equals(stageId)) {
				return i;
			}
		}
		return -1;
	}

	// MÉTODOS PRIVADOS PARA REGRAS DE CONQUISTAS

	private void verificarConquistasDeCultivo(UUID ownerId) {
		long totalPlantas = gardenPlantRepository.countByOwnerId(ownerId);

		if (totalPlantas >= 3) {
			achievementService.concederCultivar3Plantas(ownerId);
		}

		if (totalPlantas >= 5) {
			achievementService.concederCultivar5Plantas(ownerId);
		}
		if (totalPlantas >= 10) {
			achievementService.concederCultivar10Plantas(ownerId);
		}
	}

	private void verificarConquistasDeEstagio(UUID ownerId, PlantStage estagio) {
		if (estagio != null && estagio.getOrder() != null) {
			int ordem = estagio.getOrder();
			if (ordem == 2) {
				achievementService.concederEstagioCrescimento(ownerId);
			} else if (ordem >= 3) {
				achievementService.concederEstagioColheitaOuFloracao(ownerId);
			}
		}
	}

	private void notificarSeguidoresNovaPlanta(User owner, GardenPlant planta) {
		List<Follow> seguidores = followRepository.findByFollowed(owner);
		if (seguidores.isEmpty()) {
			return;
		}

		String nomePlanta = (planta.getNickname() != null && !planta.getNickname().isBlank())
				? planta.getNickname()
				: (planta.getPlant() != null && planta.getPlant().getName() != null
						? planta.getPlant().getName()
						: "uma nova planta");

		String titulo = "Nova planta no jardim! 🌱";
		String mensagem = owner.getName() + " (@" + owner.getUsername() + ") começou a cultivar " + nomePlanta + ".";

		for (Follow follow : seguidores) {
			if (follow.getFollower() != null && !follow.getFollower().getId().equals(owner.getId())) {
				firebasePushNotificationService.sendPushNotification(
						follow.getFollower(),
						titulo,
						mensagem,
						"NEW_PLANT"
				);
			}
		}
	}
}