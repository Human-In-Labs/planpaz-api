package com.humanin.planpaz.service;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.humanin.planpaz.infra.exception.ResourceNotFoundException;
import com.humanin.planpaz.model.Achievement;
import com.humanin.planpaz.model.User;
import com.humanin.planpaz.model.UserAchievement;
import com.humanin.planpaz.repositories.AchievementRepository;
import com.humanin.planpaz.repositories.UserAchievementRepository;
import com.humanin.planpaz.repositories.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AchievementService {

	private final AchievementRepository achievementRepository;
	private final UserAchievementRepository userAchievementRepository;
	private final UserRepository userRepository;

	// Método genérico para conceder/salvar uma conquista
	@Transactional
	public UserAchievement concederConquista(UUID userId, String nome, String descricao) {
		// 1. Busca ou cria a conquista no catálogo
		Achievement achievement = achievementRepository.findByName(nome)
				.orElseGet(() -> achievementRepository.save(new Achievement(nome, descricao)));

		// 2. Verifica se a conquista já foi concedida ao usuário
		if (userAchievementRepository.existsByUserIdAndAchievementId(userId, achievement.getId())) {
			return null;
		}

		User user = userRepository.findById(userId)
				.orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado com o ID: " + userId));

		UserAchievement userAchievement = new UserAchievement(user, achievement);
		return userAchievementRepository.save(userAchievement);
	}

	// CONQUISTAS DE CULTIVO DE PLANTAS

	// Cultivar 3 plantas
	public UserAchievement concederCultivar3Plantas(UUID userId) {
		return concederConquista(userId, "Aprendiz de Jardinagem", "Cultivou 3 plantas no seu jardim!");
	}

	// Cultivar 5 plantas
	public UserAchievement concederCultivar5Plantas(UUID userId) {
		return concederConquista(userId, "Jardineiro Dedicado", "Cultivou 5 plantas no seu jardim!");
	}

	// Cultivar 10 plantas
	public UserAchievement concederCultivar10Plantas(UUID userId) {
		return concederConquista(userId, "Mestre Botânico", "Cultivou 10 plantas no seu jardim!");
	}

	// CONQUISTAS DE SEQUÊNCIA DE CUIDADOS

	// Cuidar de uma planta por 3 dias
	public UserAchievement concederCuidarPlanta3Dias(UUID userId) {
		return concederConquista(userId, "Constância Inicial", "Cuidou de uma planta por 3 dias seguidos!");
	}

	// Cuidar de uma planta por 5 dias
	public UserAchievement concederCuidarPlanta5Dias(UUID userId) {
		return concederConquista(userId, "Hábito Verde", "Cuidou de uma planta por 5 dias seguidos!");
	}

	// Cuidar de uma planta por 10 dias
	public UserAchievement concederCuidarPlanta10Dias(UUID userId) {
		return concederConquista(userId, "Cuidador de Elite", "Cuidou de uma planta por 10 dias seguidos!");
	}

	// CONQUISTAS DE ESTÁGIOS DA PLANTA

	// Alcançar o estágio de crescimento
	public UserAchievement concederEstagioCrescimento(UUID userId) {
		return concederConquista(userId, "Brotando Fortes", "Uma de suas plantas alcançou o estágio de crescimento!");
	}

	// Alcançar o estágio de colheita ou floração
	public UserAchievement concederEstagioColheitaOuFloracao(UUID userId) {
		return concederConquista(userId, "Florescer e Colher",
				"Uma de suas plantas alcançou a fase de floração/colheita!");
	}

	// CONQUISTAS DE ACESSO AO APP

	// Entrar diariamente no app por 5 dias
	public UserAchievement concederAcessoDiario5Dias(UUID userId) {
		return concederConquista(userId, "Presença Diária", "Entrou no aplicativo por 5 dias consecutivos!");
	}

	// CONSULTA

	// Buscar todas as conquistas do usuário
	@Transactional(readOnly = true)
	public List<UserAchievement> obterConquistasDoUsuario(UUID userId) {
		return userAchievementRepository.findByUserId(userId);
	}

	// Buscar catálogo completo de conquistas
	@Transactional(readOnly = true)
	public List<Achievement> listarTodas() {
		return achievementRepository.findAll();
	}
}

