package com.humanin.planpaz.service;

import java.time.LocalDate;
import java.time.ZoneId;

import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.humanin.planpaz.infra.exception.BusinessException;
import com.humanin.planpaz.model.GardenPlant;
import com.humanin.planpaz.model.User;
import com.humanin.planpaz.repositories.UserRepository;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class StreakService {

	public static final ZoneId ZONE_BRASILIA = ZoneId.of("America/Sao_Paulo");

	private final UserRepository userRepository;
	private final AchievementService achievementService;

	public StreakService(UserRepository userRepository, @Lazy AchievementService achievementService) {
		this.userRepository = userRepository;
		this.achievementService = achievementService;
	}

	/**
	 * Retorna a ofensiva ativa atual do usuário baseada exclusivamente em interações reais com o jardim.
	 * Se a última atividade foi antes de ontem, a sequência foi quebrada e o método retorna 0.
	 */
	public int getActiveStreak(User user) {
		if (user == null || user.getStreakDays() == null || user.getStreakDays() <= 0) {
			return 0;
		}

		LocalDate lastCare = user.getLastCareDate();
		if (lastCare == null) {
			return 0;
		}

		LocalDate hoje = LocalDate.now(ZONE_BRASILIA);
		LocalDate ontem = hoje.minusDays(1);

		// Se a última interação válida no jardim foi hoje ou ontem, a sequência está ativa
		if (lastCare.equals(hoje) || lastCare.equals(ontem)) {
			return user.getStreakDays();
		}

		// Se a última interação foi anterior a ontem, a ofensiva expirou
		return 0;
	}

	/**
	 * Processa uma interação válida do usuário com seu próprio jardim (cadastrar planta,
	 * editar informações de planta ou registrar cuidados como regar, podar e adubar).
	 *
	 * Regras:
	 * 1. Não soma dias retroativos com base em idade da planta ou datas antigas de plantio.
	 * 2. Várias interações no mesmo dia contam como apenas um dia (ofensiva inalterada).
	 * 3. Interação em dia consecutivo a ontem incrementa em 1.
	 * 4. Retorno após um ou mais dias sem atividade reinicia a ofensiva em 1.
	 * 5. Conquistas de streak (3, 7 e 30 dias) são concedidas somente após o alcance real da sequência.
	 *
	 * @param user Usuário proprietário
	 * @param planta Planta associada à interação (deve pertencer ao usuário)
	 * @param actionBasePoints Pontuação base da ação (0 para edição, 10 rega, 15 poda, 25 adubação, 110 cadastro)
	 * @return Total de pontos concedidos (pontos base + bônus de streak aplicável)
	 */
	@Transactional
	public int processCareActionAndGrantPoints(User user, GardenPlant planta, int actionBasePoints) {
		if (user == null) {
			return 0;
		}

		// Validação estrita: interações com plantas de outro usuário não contam para a ofensiva
		if (planta != null && planta.getOwner() != null && !planta.getOwner().getId().equals(user.getId())) {
			throw new BusinessException("Interações com plantas de outro usuário não são permitidas e não contam para a ofensiva.");
		}

		LocalDate hoje = LocalDate.now(ZONE_BRASILIA);
		LocalDate ontem = hoje.minusDays(1);
		LocalDate lastCare = user.getLastCareDate();

		int currentStreak = getActiveStreak(user);
		boolean isFirstInteractionOfDay = (lastCare == null || !lastCare.equals(hoje));

		int newStreak;
		if (isFirstInteractionOfDay) {
			if (lastCare != null && lastCare.equals(ontem)) {
				// Interação em dia consecutivo: incrementa em 1
				newStreak = currentStreak + 1;
			} else {
				// Primeira interação válida de todas OU retorno após 1+ dias ausente: inicia em 1
				newStreak = 1;
			}
		} else {
			// Novas interações no mesmo dia: mantêm a ofensiva inalterada sem somar novamente
			newStreak = currentStreak > 0 ? currentStreak : 1;
		}

		user.setStreakDays(newStreak);
		user.setLastCareDate(hoje);

		if (planta != null) {
			planta.setStreakDays(newStreak);
			planta.setLastCareDate(hoje);
		}

		int totalPoints = actionBasePoints;
		int streakBonus = 0;

		// Concede bônus de streak somente na primeira interação válida de um dia consecutivo
		if (isFirstInteractionOfDay && newStreak > 1) {
			streakBonus += 3;

			if (newStreak == 7) {
				streakBonus += 20;
				log.info("Usuário {} atingiu marco de 7 dias de streak (+20 pts!)", user.getUsername());
			} else if (newStreak == 14) {
				streakBonus += 40;
				log.info("Usuário {} atingiu marco de 14 dias de streak (+40 pts!)", user.getUsername());
			} else if (newStreak % 30 == 0) {
				streakBonus += 100;
				log.info("Usuário {} atingiu marco mensal de {} dias de streak (+100 pts!)", user.getUsername(), newStreak);
			}
		}

		totalPoints += streakBonus;

		int userEcoscore = user.getEcoscore() != null ? user.getEcoscore() : 0;
		user.setEcoscore(userEcoscore + totalPoints);

		if (planta != null && actionBasePoints > 0) {
			int plantEcoscore = planta.getEcoscore() != null ? planta.getEcoscore() : 0;
			planta.setEcoscore(plantEcoscore + totalPoints);
		}

		// Desbloqueio seguro de conquistas de ofensiva baseado estritamente na ofensiva real alcançada
		if (newStreak >= 3) {
			achievementService.concederConquista(user.getId(), "Constância Verde", "Manter 3 dias seguidos de Streak");
		}
		if (newStreak >= 7) {
			achievementService.concederConquista(user.getId(), "Mestre da Rega", "Manter 7 dias seguidos de Streak");
		}
		if (newStreak >= 30) {
			achievementService.concederConquista(user.getId(), "Guardião Botânico", "Manter 30 dias seguidos de Streak");
		}

		userRepository.save(user);

		return totalPoints;
	}
}
