package com.humanin.planpaz.service;

import java.time.LocalDate;

import org.springframework.stereotype.Service;

import com.humanin.planpaz.model.GardenPlant;
import com.humanin.planpaz.model.User;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class StreakService {

	/**
	 * Processa o streak e EcoScore do usuário no estilo Duolingo.
	 * 
	 * @param user Usuário proprietário
	 * @param planta Planta que recebeu o cuidado
	 * @param actionBasePoints Pontuação base da ação (Regar +10, Podar +15, Adubar +25)
	 * @return Total de pontos concedidos (pontos da ação + bônus de streak)
	 */
	public int processCareActionAndGrantPoints(User user, GardenPlant planta, int actionBasePoints) {
		LocalDate hoje = LocalDate.now();
		LocalDate ontem = hoje.minusDays(1);
		LocalDate lastCare = user.getLastCareDate();

		int currentStreak = user.getStreakDays() != null ? user.getStreakDays() : 0;
		boolean isFirstCareOfDay = (lastCare == null || !lastCare.equals(hoje));

		int newStreak;
		if (isFirstCareOfDay) {
			if (lastCare != null && lastCare.equals(ontem)) {
				newStreak = currentStreak + 1;
			} else {
				// Primeiro cuidado de todos ou ofensiva quebrada (2+ dias sem cuidado)
				newStreak = 1;
			}
		} else {
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

		if (isFirstCareOfDay) {
			// Bônus fixo de +3 pts por manter a ofensiva no 1º cuidado do dia
			streakBonus += 3;

			// Bônus por marcos específicos atingidos (Valores fixos não cumulativos)
			if (newStreak == 7) {
				streakBonus += 20;
				log.info("Usuário {} atingiu marco de 7 dias de streak (+20 pts!)", user.getUsername());
			} else if (newStreak == 14) {
				streakBonus += 40;
				log.info("Usuário {} atingiu marco de 14 dias de streak (+40 pts!)", user.getUsername());
			} else if (newStreak > 0 && newStreak % 30 == 0) {
				streakBonus += 100;
				log.info("Usuário {} atingiu marco mensal de {} dias de streak (+100 pts!)", user.getUsername(), newStreak);
			}
		}

		totalPoints += streakBonus;

		int userEcoscore = user.getEcoscore() != null ? user.getEcoscore() : 0;
		user.setEcoscore(userEcoscore + totalPoints);

		if (planta != null) {
			int plantEcoscore = planta.getEcoscore() != null ? planta.getEcoscore() : 0;
			planta.setEcoscore(plantEcoscore + totalPoints);
		}

		return totalPoints;
	}
}
