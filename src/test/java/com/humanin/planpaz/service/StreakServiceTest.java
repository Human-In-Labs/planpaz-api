package com.humanin.planpaz.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.humanin.planpaz.infra.exception.BusinessException;
import com.humanin.planpaz.model.GardenPlant;
import com.humanin.planpaz.model.User;
import com.humanin.planpaz.repositories.UserRepository;

class StreakServiceTest {

	private UserRepository userRepository;
	private AchievementService achievementService;
	private StreakService streakService;

	private User user;
	private GardenPlant planta;

	@BeforeEach
	void setUp() {
		userRepository = mock(UserRepository.class);
		achievementService = mock(AchievementService.class);
		streakService = new StreakService(userRepository, achievementService);

		user = new User();
		user.setId(UUID.randomUUID());
		user.setUsername("cultivador_teste");
		user.setStreakDays(0);
		user.setLastCareDate(null);
		user.setEcoscore(0);

		planta = new GardenPlant();
		planta.setId(UUID.randomUUID());
		planta.setOwner(user);
		planta.setNickname("Monstera");
		planta.setStreakDays(0);
		planta.setLastCareDate(null);
	}

	@Test
	@DisplayName("1. Cadastro de planta com 1 ano de cultivo não aumenta a ofensiva além do valor devido (inicia em 1)")
	void cadastroDePlantaComDataAntigaNaoAumentaOfensivaAlemDoDevido() {
		// Planta com 1 ano de cultivo
		planta.setPlantedAt(LocalDateTime.now().minusYears(1));

		streakService.processCareActionAndGrantPoints(user, planta, 110);

		assertEquals(1, user.getStreakDays(), "A ofensiva inicial do usuário deve ser 1, independentemente da idade da planta.");
		assertEquals(LocalDate.now(StreakService.ZONE_BRASILIA), user.getLastCareDate());
		verify(userRepository).save(user);
	}

	@Test
	@DisplayName("2. Cadastro de planta com data antiga não concede a conquista de 7 dias")
	void cadastroDePlantaComDataAntigaNaoConcedeConquistaDe7Dias() {
		planta.setPlantedAt(LocalDateTime.now().minusYears(1));

		streakService.processCareActionAndGrantPoints(user, planta, 110);

		assertEquals(1, user.getStreakDays());
		verify(achievementService, never()).concederConquista(eq(user.getId()), eq("Mestre da Rega"), any());
		verify(achievementService, never()).concederConquista(eq(user.getId()), eq("Constância Verde"), any());
		verify(achievementService, never()).concederConquista(eq(user.getId()), eq("Guardião Botânico"), any());
	}

	@Test
	@DisplayName("3. Várias interações com plantas no mesmo dia contam como apenas um dia")
	void variasInteracoesComPlantasNoMesmoDiaContamComoApenasUmDia() {
		// 1ª interação do dia (cadastro da 1ª planta)
		streakService.processCareActionAndGrantPoints(user, planta, 110);
		assertEquals(1, user.getStreakDays());

		// 2ª interação no mesmo dia (rega)
		streakService.processCareActionAndGrantPoints(user, planta, 10);
		assertEquals(1, user.getStreakDays(), "Segunda interação no mesmo dia não deve incrementar a ofensiva.");

		// 3ª interação no mesmo dia (poda)
		streakService.processCareActionAndGrantPoints(user, planta, 15);
		assertEquals(1, user.getStreakDays(), "Terceira interação no mesmo dia deve manter a ofensiva inalterada.");

		// 4ª interação no mesmo dia (edição)
		streakService.processCareActionAndGrantPoints(user, planta, 0);
		assertEquals(1, user.getStreakDays(), "Edição no mesmo dia deve manter a ofensiva inalterada.");
	}

	@Test
	@DisplayName("4. Interações válidas em 7 dias consecutivos concedem a conquista de 7 dias uma única vez")
	void interacoesValidasEm7DiasConsecutivosConcedemConquistaDe7DiasUmaUnicaVez() {
		LocalDate hoje = LocalDate.now(StreakService.ZONE_BRASILIA);

		// Simulamos que o usuário acumulou 6 dias consecutivos e a última atividade foi ontem
		user.setStreakDays(6);
		user.setLastCareDate(hoje.minusDays(1));

		// Interação do 7º dia hoje
		streakService.processCareActionAndGrantPoints(user, planta, 10);

		assertEquals(7, user.getStreakDays(), "A ofensiva deve atingir 7 dias consecutivos.");
		verify(achievementService, times(1)).concederConquista(eq(user.getId()), eq("Mestre da Rega"), any());

		// Segunda interação no mesmo dia 7 não deve conceder a conquista novamente nem duplicar
		streakService.processCareActionAndGrantPoints(user, planta, 15);
		assertEquals(7, user.getStreakDays());
		verify(achievementService, times(1)).concederConquista(eq(user.getId()), eq("Mestre da Rega"), any());
	}

	@Test
	@DisplayName("5. Se faltar um dia de atividade, o sistema não considera esse dia e reinicia a ofensiva em 1")
	void seFaltarUmDiaDeAtividadeSistemaNaoConsideraDiaComoCumpridoEReinicia() {
		LocalDate hoje = LocalDate.now(StreakService.ZONE_BRASILIA);

		// Usuário tinha 5 dias de streak, mas a última atividade foi há 2 dias (ontem faltou!)
		user.setStreakDays(5);
		user.setLastCareDate(hoje.minusDays(2));

		// Antes de interagir hoje, a ofensiva ativa expirou para 0
		assertEquals(0, streakService.getActiveStreak(user), "Usuário sem atividade ontem teve sua ofensiva expirada.");

		// Ao retornar hoje e interagir
		streakService.processCareActionAndGrantPoints(user, planta, 10);

		assertEquals(1, user.getStreakDays(), "Após quebra de sequência, a ofensiva deve reiniciar estritamente em 1.");
		assertEquals(hoje, user.getLastCareDate());
	}

	@Test
	@DisplayName("6. Interações com plantas de outro usuário não contam para a ofensiva do usuário atual")
	void interacoesComPlantasDeOutroUsuarioNaoContamParaOfensivaDoUsuarioAtual() {
		User outroUsuario = new User();
		outroUsuario.setId(UUID.randomUUID());
		outroUsuario.setUsername("outro_usuario");

		GardenPlant plantaAlheia = new GardenPlant();
		plantaAlheia.setId(UUID.randomUUID());
		plantaAlheia.setOwner(outroUsuario);

		assertThrows(BusinessException.class, () -> {
			streakService.processCareActionAndGrantPoints(user, plantaAlheia, 10);
		}, "Deve lançar BusinessException ao tentar registrar ação em planta de outro usuário.");

		assertEquals(0, user.getStreakDays(), "Ofensiva do usuário atual não deve ser alterada.");
	}

	@Test
	@DisplayName("7. O contador permanece correto e consistente após repetir requisições no mesmo dia")
	void contadorPermaneceCorretoAposRepetirRequisicao() {
		LocalDate hoje = LocalDate.now(StreakService.ZONE_BRASILIA);
		user.setStreakDays(3);
		user.setLastCareDate(hoje);

		// Chamadas repetidas do frontend (ex: reloads, múltiplos componentes na home)
		int streak1 = streakService.getActiveStreak(user);
		int streak2 = streakService.getActiveStreak(user);
		int streak3 = streakService.getActiveStreak(user);

		assertEquals(3, streak1);
		assertEquals(3, streak2);
		assertEquals(3, streak3);

		// Mais uma ação no mesmo dia
		streakService.processCareActionAndGrantPoints(user, planta, 10);
		assertEquals(3, user.getStreakDays(), "Ação no mesmo dia mantém o contador consistente.");
	}

	@Test
	@DisplayName("8. Desbloqueio de 3 dias concede Constância Verde e não Mestre da Rega antecipadamente")
	void desbloqueioDe3DiasConcedeConstanciaVerdeENaoMestreDaRega() {
		LocalDate hoje = LocalDate.now(StreakService.ZONE_BRASILIA);
		user.setStreakDays(2);
		user.setLastCareDate(hoje.minusDays(1));

		streakService.processCareActionAndGrantPoints(user, planta, 10);

		assertEquals(3, user.getStreakDays());
		verify(achievementService, times(1)).concederConquista(eq(user.getId()), eq("Constância Verde"), any());
		verify(achievementService, never()).concederConquista(eq(user.getId()), eq("Mestre da Rega"), any());
		verify(achievementService, never()).concederConquista(eq(user.getId()), eq("Guardião Botânico"), any());
	}
}
