package com.humanin.planpaz.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.humanin.planpaz.dto.AchievementProgressDTO;
import com.humanin.planpaz.model.Achievement;
import com.humanin.planpaz.model.GardenPlant;
import com.humanin.planpaz.model.User;
import com.humanin.planpaz.model.UserAchievement;
import com.humanin.planpaz.repositories.AchievementRepository;
import com.humanin.planpaz.repositories.CommentRepository;
import com.humanin.planpaz.repositories.GardenPlantRepository;
import com.humanin.planpaz.repositories.PlantCareLogRepository;
import com.humanin.planpaz.repositories.PostRepository;
import com.humanin.planpaz.repositories.UserAchievementRepository;
import com.humanin.planpaz.repositories.UserRepository;

class AchievementServiceStreakTest {

	private AchievementRepository achievementRepository;
	private UserAchievementRepository userAchievementRepository;
	private UserRepository userRepository;
	private GardenPlantRepository gardenPlantRepository;
	private PlantCareLogRepository plantCareLogRepository;
	private PostRepository postRepository;
	private CommentRepository commentRepository;
	private StreakService streakService;

	private AchievementService achievementService;

	private User user;
	private UUID userId;

	@BeforeEach
	void setUp() {
		achievementRepository = mock(AchievementRepository.class);
		userAchievementRepository = mock(UserAchievementRepository.class);
		userRepository = mock(UserRepository.class);
		gardenPlantRepository = mock(GardenPlantRepository.class);
		plantCareLogRepository = mock(PlantCareLogRepository.class);
		postRepository = mock(PostRepository.class);
		commentRepository = mock(CommentRepository.class);
		streakService = mock(StreakService.class);

		achievementService = new AchievementService(
				achievementRepository,
				userAchievementRepository,
				userRepository,
				gardenPlantRepository,
				plantCareLogRepository,
				postRepository,
				commentRepository,
				streakService
		);

		userId = UUID.randomUUID();
		user = new User();
		user.setId(userId);
		user.setUsername("cultivador");
		user.setCreatedAt(LocalDateTime.now().minusDays(1)); // Conta criada ontem
		user.setStreakDays(1);

		when(userRepository.findById(userId)).thenReturn(Optional.of(user));
	}

	@Test
	@DisplayName("Planta com 1 ano de cultivo não concede progresso indevido nas conquistas de streak")
	void plantaComDataAntigaNaoConcedeProgressoEmConquistasDeStreak() {
		// Planta antiga com 365 dias de plantio
		GardenPlant plantaAntiga = new GardenPlant();
		plantaAntiga.setId(UUID.randomUUID());
		plantaAntiga.setOwner(user);
		plantaAntiga.setPlantedAt(LocalDateTime.now().minusYears(1));
		plantaAntiga.setStreakDays(1);

		when(gardenPlantRepository.findByOwnerId(userId)).thenReturn(List.of(plantaAntiga));
		when(streakService.getActiveStreak(user)).thenReturn(1); // Streak real é 1!

		List<AchievementProgressDTO> progressos = achievementService.obterConquistasProgressoDoUsuario(userId);

		// Verifica Mestre da Rega (exige 7 dias de streak)
		AchievementProgressDTO mestreDaRega = progressos.stream()
				.filter(a -> "Mestre da Rega".equals(a.getName()))
				.findFirst()
				.orElseThrow();

		assertEquals(1, mestreDaRega.getProgress(), "O progresso deve ser exatamente o streak ativo do usuário (1), não 365!");
		assertFalse(mestreDaRega.isUnlocked(), "Mestre da Rega NÃO deve estar desbloqueado.");

		// Verifica Constância Verde (exige 3 dias de streak)
		AchievementProgressDTO constanciaVerde = progressos.stream()
				.filter(a -> "Constância Verde".equals(a.getName()))
				.findFirst()
				.orElseThrow();

		assertEquals(1, constanciaVerde.getProgress());
		assertFalse(constanciaVerde.isUnlocked());
	}

	@Test
	@DisplayName("Sanitização remove conquista indevida concedida no passado a conta recente")
	void sanitizacaoRemoveConquistaIndevidaDeContaRecente() {
		Achievement mestreRegaAch = new Achievement("Mestre da Rega", "Manter 7 dias seguidos de Streak");
		UserAchievement uaIndevida = new UserAchievement(user, mestreRegaAch);

		// Conta criada ontem, sem histórico de 7 dias
		when(userAchievementRepository.findByUserId(userId)).thenReturn(List.of(uaIndevida));
		when(plantCareLogRepository.findByUserId(userId)).thenReturn(List.of());

		achievementService.sanitizarConquistasDeStreakIndevidas(userId);

		// Deve remover a conquista indevida!
		verify(userAchievementRepository, times(1)).delete(uaIndevida);
	}

	@Test
	@DisplayName("Sanitização preserva conquista legítima de usuário com histórico suficiente")
	void sanitizacaoPreservaConquistaLegitimaDeUsuarioAntigo() {
		user.setCreatedAt(LocalDateTime.now().minusDays(30)); // Conta com 30 dias de existência

		Achievement mestreRegaAch = new Achievement("Mestre da Rega", "Manter 7 dias seguidos de Streak");
		UserAchievement uaLegitima = new UserAchievement(user, mestreRegaAch);

		when(userAchievementRepository.findByUserId(userId)).thenReturn(List.of(uaLegitima));

		achievementService.sanitizarConquistasDeStreakIndevidas(userId);

		// Não deve deletar a conquista legítima
		verify(userAchievementRepository, never()).delete(any());
	}

	@Test
	@DisplayName("Chamada manual de teste não concede Mestre da Rega se o usuário tiver menos de 7 dias de streak")
	void chamadaManualNaoConcedeMestreDaRegaSemStreakSuficiente() {
		when(streakService.getActiveStreak(user)).thenReturn(2);

		UserAchievement result = achievementService.concederCuidarPlanta10Dias(userId);

		assertNull(result, "Não deve conceder a conquista pois o streak ativo é apenas 2.");
		verify(userAchievementRepository, never()).save(any());
	}
}
