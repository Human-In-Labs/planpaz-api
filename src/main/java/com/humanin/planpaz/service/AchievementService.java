package com.humanin.planpaz.service;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.humanin.planpaz.config.CacheConfig;
import com.humanin.planpaz.dto.AchievementProgressDTO;
import com.humanin.planpaz.infra.exception.ResourceNotFoundException;
import com.humanin.planpaz.model.Achievement;
import com.humanin.planpaz.model.GardenPlant;
import com.humanin.planpaz.model.User;
import com.humanin.planpaz.model.UserAchievement;
import com.humanin.planpaz.model.enums.CareType;
import com.humanin.planpaz.model.enums.Size;
import com.humanin.planpaz.repositories.AchievementRepository;
import com.humanin.planpaz.repositories.CommentRepository;
import com.humanin.planpaz.repositories.GardenPlantRepository;
import com.humanin.planpaz.repositories.PlantCareLogRepository;
import com.humanin.planpaz.repositories.PostRepository;
import com.humanin.planpaz.repositories.UserAchievementRepository;
import com.humanin.planpaz.repositories.UserRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class AchievementService {

	private final AchievementRepository achievementRepository;
	private final UserAchievementRepository userAchievementRepository;
	private final UserRepository userRepository;
	private final GardenPlantRepository gardenPlantRepository;
	private final PlantCareLogRepository plantCareLogRepository;
	private final PostRepository postRepository;
	private final CommentRepository commentRepository;
	private final StreakService streakService;

	public record AchievementDef(
			String name,
			String description,
			String icon,
			int maxProgress
	) {}

	public static final List<AchievementDef> CATALOG = List.of(
			// 1. Boas-vindas & Nível Inicial
			new AchievementDef("Boas-vindas Verde", "Criar a conta no aplicativo", "plant", 1),
			new AchievementDef("Primeira Rega", "Regar uma planta pela primeira vez", "drop", 1),
			new AchievementDef("Nutricionista Vegetal", "Adubar uma planta pela primeira vez", "sparkles", 1),
			new AchievementDef("Cabeleireiro Botânico", "Podar uma planta pela primeira vez", "scissors", 1),
			new AchievementDef("Primeira Semente", "Cadastrar a 1ª planta no app", "plant", 1),

			// 2. Gestão do Jardim (Quantidade de Plantas)
			new AchievementDef("Mão Verde", "Cadastrar 3 plantas no seu jardim", "plant", 3),
			new AchievementDef("Floresta Urbana", "Cadastrar 5 plantas no seu jardim", "leaf", 5),
			new AchievementDef("Jardim Botânico", "Cadastrar 10 plantas no seu jardim", "tree", 10),
			new AchievementDef("Colecionador de Folhas", "Cadastrar 15 plantas no seu jardim", "tree", 15),
			new AchievementDef("Santuário Verde", "Cadastrar 20 plantas no seu jardim", "tree", 20),
			new AchievementDef("Mestre do Ecossistema", "Cadastrar 30 plantas no seu jardim", "tree", 30),
			new AchievementDef("Mata Atlântica Particular", "Cadastrar 40 plantas no seu jardim", "tree", 40),
			new AchievementDef("Amazônia em Casa", "Cadastrar 50 plantas no seu jardim", "tree", 50),

			// 3. Comunidade & Redes Sociais
			new AchievementDef("Voz da Comunidade", "Criar 1 post na comunidade", "chatText", 1),
			new AchievementDef("Influenciador Botânico", "Criar 5 posts na comunidade", "star", 5),
			new AchievementDef("Avaliador Ecológico", "Escrever 5 comentários em posts", "chatCircleDots", 5),
			new AchievementDef("Membro Ativo", "Escrever 20 comentários em posts", "chatCircleDots", 20),

			// 4. Streaks & Cuidados Consecutivos
			new AchievementDef("Constância Verde", "Manter 3 dias seguidos de Streak", "calendarDots", 3),
			new AchievementDef("Mestre da Rega", "Manter 7 dias seguidos de Streak", "fire", 7),
			new AchievementDef("Guardião Botânico", "Manter 30 dias seguidos de Streak", "trophy", 30),

			// 5. EcoScore & Impacto de Carbono
			new AchievementDef("Jardineiro Dedicado", "Acumular 100 pontos de EcoScore", "medal", 100),
			new AchievementDef("Eco Mestre", "Acumular 500 pontos de EcoScore", "crown", 500),
			new AchievementDef("Protetor da Natureza", "Retirar 500g de CO₂", "globe", 500),
			new AchievementDef("Pulmão Verde", "Retirar 2000g (2kg) de CO₂", "globe", 2000),
			new AchievementDef("Guardião do Planeta", "Retirar 5000g (5kg) de CO₂", "planet", 5000)
	);

	@EventListener(ApplicationReadyEvent.class)
	@Transactional
	public void initializeCatalog() {
		for (AchievementDef def : CATALOG) {
			if (!achievementRepository.existsByName(def.name())) {
				achievementRepository.save(new Achievement(def.name(), def.description()));
			}
		}
	}

	@Transactional
	public List<AchievementProgressDTO> obterConquistasProgressoDoUsuario(UUID userId) {
		sanitizarConquistasDeStreakIndevidas(userId);

		User user = userRepository.findById(userId)
				.orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado com ID: " + userId));

		List<UserAchievement> userAchievements = userAchievementRepository.findByUserId(userId);
		Map<String, UserAchievement> userAchievementsByName = userAchievements.stream()
				.collect(Collectors.toMap(ua -> ua.getAchievement().getName(), Function.identity(), (a, b) -> a));

		// Métricas do usuário
		List<GardenPlant> plants = gardenPlantRepository.findByOwnerId(userId);
		long totalPlants = plants.size();

		Map<CareType, Long> careCounts = new EnumMap<>(CareType.class);
		List<Object[]> groupedCare = plantCareLogRepository.countByUserIdGroupByCareType(userId);
		if (groupedCare != null && !groupedCare.isEmpty()) {
			for (Object[] row : groupedCare) {
				if (row != null && row.length >= 2 && row[0] instanceof CareType type && row[1] instanceof Number count) {
					careCounts.put(type, count.longValue());
				}
			}
		}

		long waterings = careCounts.getOrDefault(CareType.WATERING, 0L);
		long fertilizings = careCounts.getOrDefault(CareType.FERTILIZING, 0L);
		long prunings = careCounts.getOrDefault(CareType.PRUNING, 0L);

		long totalPosts = postRepository.countByAuthorId(userId);
		long totalComments = commentRepository.countByAuthorId(userId);

		// Apenas a ofensiva consecutiva real e ativa do usuário no jardim é considerada
		int userStreak = streakService.getActiveStreak(user);

		int ecoscore = user.getEcoscore() != null ? user.getEcoscore() : 0;

		double co2Grams = 0.0;
		for (GardenPlant planta : plants) {
			long cultivationDays = 0;
			if (planta.getPlantedAt() != null) {
				cultivationDays = Math.max(0, ChronoUnit.DAYS.between(planta.getPlantedAt().toLocalDate(), LocalDate.now()));
			}
			double co2PerYear = (planta.getPlant() != null && planta.getPlant().getSize() == Size.LARGE) ? 1300.0 : 50.0;
			co2Grams += cultivationDays * (co2PerYear / 365.0);
		}
		int totalCo2Grams = (int) Math.round(co2Grams);

		List<AchievementProgressDTO> result = new ArrayList<>();

		for (AchievementDef def : CATALOG) {
			UserAchievement ua = userAchievementsByName.get(def.name());
			boolean isUnlocked = ua != null;

			int currentMetric = switch (def.name()) {
				case "Boas-vindas Verde" -> 1;
				case "Primeira Rega" -> (int) waterings;
				case "Nutricionista Vegetal" -> (int) fertilizings;
				case "Cabeleireiro Botânico" -> (int) prunings;
				case "Primeira Semente", "Mão Verde", "Floresta Urbana", "Jardim Botânico",
				     "Colecionador de Folhas", "Santuário Verde", "Mestre do Ecossistema",
				     "Mata Atlântica Particular", "Amazônia em Casa" -> (int) totalPlants;
				case "Voz da Comunidade", "Influenciador Botânico" -> (int) totalPosts;
				case "Avaliador Ecológico", "Membro Ativo" -> (int) totalComments;
				case "Constância Verde", "Mestre da Rega", "Guardião Botânico" -> userStreak;
				case "Jardineiro Dedicado", "Eco Mestre" -> ecoscore;
				case "Protetor da Natureza", "Pulmão Verde", "Guardião do Planeta" -> totalCo2Grams;
				default -> 0;
			};

			int progress = isUnlocked ? def.maxProgress() : Math.min(currentMetric, def.maxProgress());

			UUID achievementId = ua != null ? ua.getAchievement().getId() : UUID.nameUUIDFromBytes(def.name().getBytes());

			result.add(AchievementProgressDTO.builder()
					.id(achievementId)
					.name(def.name())
					.description(def.description())
					.icon(def.icon())
					.unlocked(isUnlocked)
					.unlockedAt(ua != null ? ua.getUnlockedAt() : null)
					.progress(progress)
					.maxProgress(def.maxProgress())
					.build());
		}

		return result;
	}

	/**
	 * Calcula o progresso uma única vez, concede quaisquer conquistas recém-atingidas in-place
	 * e retorna a lista completa atualizada (evitando dupla execução no controller).
	 */
	@Transactional
	public List<AchievementProgressDTO> syncAndGetProgress(UUID userId) {
		List<AchievementProgressDTO> progressList = obterConquistasProgressoDoUsuario(userId);
		List<AchievementProgressDTO> updatedList = new ArrayList<>(progressList.size());

		for (AchievementProgressDTO dto : progressList) {
			if (!dto.isUnlocked() && dto.getProgress() >= dto.getMaxProgress()) {
				UserAchievement newUa = concederConquista(userId, dto.getName(), dto.getDescription());
				if (newUa != null) {
					log.info("Conquista '{}' desbloqueada para o usuário ID {}", dto.getName(), userId);
					updatedList.add(AchievementProgressDTO.builder()
							.id(newUa.getAchievement().getId())
							.name(dto.getName())
							.description(dto.getDescription())
							.icon(dto.getIcon())
							.unlocked(true)
							.unlockedAt(newUa.getUnlockedAt())
							.progress(dto.getMaxProgress())
							.maxProgress(dto.getMaxProgress())
							.build());
					continue;
				}
			}
			updatedList.add(dto);
		}

		return updatedList;
	}

	@Transactional
	public List<AchievementProgressDTO> obterConquistasDesbloqueadasDoUsuario(UUID userId) {
		return syncAndGetProgress(userId).stream()
				.filter(AchievementProgressDTO::isUnlocked)
				.toList();
	}

	@Transactional
	public List<AchievementProgressDTO> checkAndGrantAll(UUID userId) {
		List<AchievementProgressDTO> progressList = obterConquistasProgressoDoUsuario(userId);
		List<AchievementProgressDTO> newlyGranted = new ArrayList<>();

		for (AchievementProgressDTO dto : progressList) {
			if (!dto.isUnlocked() && dto.getProgress() >= dto.getMaxProgress()) {
				UserAchievement newUa = concederConquista(userId, dto.getName(), dto.getDescription());
				if (newUa != null) {
					log.info("Conquista '{}' desbloqueada para o usuário ID {}", dto.getName(), userId);
					newlyGranted.add(AchievementProgressDTO.builder()
							.id(newUa.getAchievement().getId())
							.name(dto.getName())
							.description(dto.getDescription())
							.icon(dto.getIcon())
							.unlocked(true)
							.unlockedAt(newUa.getUnlockedAt())
							.progress(dto.getMaxProgress())
							.maxProgress(dto.getMaxProgress())
							.build());
				}
			}
		}

		return newlyGranted;
	}

	@Transactional
	public AchievementProgressDTO checkAndGrant(UUID userId) {
		List<AchievementProgressDTO> list = checkAndGrantAll(userId);
		return list.isEmpty() ? null : list.get(0);
	}

	@Transactional
	public UserAchievement concederConquista(UUID userId, String nome, String descricao) {
		Achievement achievement = achievementRepository.findByName(nome)
				.orElseGet(() -> achievementRepository.save(new Achievement(nome, descricao)));

		if (userAchievementRepository.existsByUserIdAndAchievementId(userId, achievement.getId())) {
			return null;
		}

		User user = userRepository.findById(userId)
				.orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado com o ID: " + userId));

		UserAchievement userAchievement = new UserAchievement(user, achievement);
		return userAchievementRepository.save(userAchievement);
	}

	public UserAchievement concederCultivar3Plantas(UUID userId) {
		return concederConquista(userId, "Mão Verde", "Cadastrar 3 plantas no seu jardim");
	}

	public UserAchievement concederCultivar5Plantas(UUID userId) {
		return concederConquista(userId, "Floresta Urbana", "Cadastrar 5 plantas no seu jardim");
	}

	public UserAchievement concederCultivar10Plantas(UUID userId) {
		return concederConquista(userId, "Jardim Botânico", "Cadastrar 10 plantas no seu jardim");
	}

	public UserAchievement concederCuidarPlanta3Dias(UUID userId) {
		User user = userRepository.findById(userId).orElse(null);
		if (user != null && streakService.getActiveStreak(user) >= 3) {
			return concederConquista(userId, "Constância Verde", "Manter 3 dias seguidos de Streak");
		}
		return null;
	}

	public UserAchievement concederCuidarPlanta5Dias(UUID userId) {
		User user = userRepository.findById(userId).orElse(null);
		if (user != null && streakService.getActiveStreak(user) >= 3) {
			return concederConquista(userId, "Constância Verde", "Manter 3 dias seguidos de Streak");
		}
		return null;
	}

	public UserAchievement concederCuidarPlanta10Dias(UUID userId) {
		User user = userRepository.findById(userId).orElse(null);
		if (user != null && streakService.getActiveStreak(user) >= 7) {
			return concederConquista(userId, "Mestre da Rega", "Manter 7 dias seguidos de Streak");
		}
		return null;
	}

	/**
	 * Sanitiza com segurança concessões antigas indevidas de conquistas de streak geradas
	 * pelo bug que considerava o tempo de cultivo (plantedAt) da planta como ofensiva.
	 * Não afeta conquistas de usuários legítimos com histórico compatível.
	 */
	@Transactional
	public void sanitizarConquistasDeStreakIndevidas(UUID userId) {
		User user = userRepository.findById(userId).orElse(null);
		if (user == null) {
			return;
		}

		List<UserAchievement> userAchievements = userAchievementRepository.findByUserId(userId);
		if (userAchievements.isEmpty()) {
			return;
		}

		long diasDesdeCriacao = user.getCreatedAt() != null
				? Math.max(1, ChronoUnit.DAYS.between(user.getCreatedAt().toLocalDate(), LocalDate.now(StreakService.ZONE_BRASILIA)) + 1)
				: 1;

		long diasDistintosDeCuidado = plantCareLogRepository.countDistinctCareDaysByUserId(userId);

		long maxDiasPossiveis = Math.max(diasDesdeCriacao, diasDistintosDeCuidado);

		for (UserAchievement ua : userAchievements) {
			String achName = ua.getAchievement().getName();
			if ("Mestre da Rega".equals(achName) && maxDiasPossiveis < 7) {
				log.warn("Removendo conquista indevida 'Mestre da Rega' do usuário {} (dias possíveis: {})",
						user.getUsername(), maxDiasPossiveis);
				userAchievementRepository.delete(ua);
			} else if ("Guardião Botânico".equals(achName) && maxDiasPossiveis < 30) {
				log.warn("Removendo conquista indevida 'Guardião Botânico' do usuário {} (dias possíveis: {})",
						user.getUsername(), maxDiasPossiveis);
				userAchievementRepository.delete(ua);
			} else if ("Constância Verde".equals(achName) && maxDiasPossiveis < 3) {
				log.warn("Removendo conquista indevida 'Constância Verde' do usuário {} (dias possíveis: {})",
						user.getUsername(), maxDiasPossiveis);
				userAchievementRepository.delete(ua);
			}
		}
	}

	public UserAchievement concederEstagioCrescimento(UUID userId) {
		return checkAndGrant(userId) != null ? null : null;
	}

	public UserAchievement concederEstagioColheitaOuFloracao(UUID userId) {
		return checkAndGrant(userId) != null ? null : null;
	}

	@Transactional(readOnly = true)
	public List<UserAchievement> obterConquistasDoUsuario(UUID userId) {
		return userAchievementRepository.findByUserId(userId);
	}

	@Transactional(readOnly = true)
	@Cacheable(value = CacheConfig.ACHIEVEMENTS_DEF_CACHE, key = "'all'", unless = "#result == null || #result.isEmpty()")
	public List<Achievement> listarTodas() {
		return achievementRepository.findAll();
	}
}
