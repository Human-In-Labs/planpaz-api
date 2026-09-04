package com.humanin.planpaz.service;

import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.humanin.planpaz.model.Achievement;
import com.humanin.planpaz.model.User;
import com.humanin.planpaz.repositories.AchievementRepository;
import com.humanin.planpaz.repositories.UserRepository;

@Service
public class AchievementService {

	@Autowired
	private AchievementRepository achievementRepository;

	@Autowired
	private UserRepository userRepository;

	// Método genérico para conceder/salvar uma conquista
	public Achievement concederConquista(UUID userId, String descricao) {
		// 1. Verifica se a conquista já foi concedida ao usuário
		boolean jaPossui = achievementRepository.existsByUserIdAndDescription(userId, descricao);
		if (jaPossui) {
			return null; // Ou retorne a conquista existente
		}

		User user = userRepository.findById(userId)
				.orElseThrow(() -> new RuntimeException("Usuário não encontrado com o ID: " + userId));

		Achievement achievement = new Achievement();
		achievement.setDescription(descricao);
		achievement.setUser(user);

		return achievementRepository.save(achievement);
	}

	// CONQUISTAS DE CULTIVO DE PLANTAS

	// Cultivar 3 plantas

	public Achievement concederCultivar3Plantas(UUID userId) {
		return concederConquista(userId, "🌿 Aprendiz de Jardinagem: Cultivou 3 plantas no seu jardim!");
	}

	// Cultivar 5 plantas

	public Achievement concederCultivar5Plantas(UUID userId) {
		return concederConquista(userId, "🌿 Jardineiro Dedicado: Cultivou 5 plantas no seu jardim!");
	}

	// Cultivar 10 plantas
	public Achievement concederCultivar10Plantas(UUID userId) {
		return concederConquista(userId, "🌳 Mestre Botânico: Cultivou 10 plantas no seu jardim!");
	}

	// CONQUISTAS DE SEQUÊNCIA DE CUIDADOS

	// Cuidar de uma planta por 3 dias
	public Achievement concederCuidarPlanta3Dias(UUID userId) {
		return concederConquista(userId, "🌱 Constância Inicial: Cuidou de uma planta por 3 dias seguidos!");
	}

	// Cuidar de uma planta por 5 dias
	public Achievement concederCuidarPlanta5Dias(UUID userId) {
		return concederConquista(userId, "🪴 Hábito Verde: Cuidou de uma planta por 5 dias seguidos!");
	}

	// Cuidar de uma planta por 10 dias
	public Achievement concederCuidarPlanta10Dias(UUID userId) {
		return concederConquista(userId, "⭐ Cuidador de Elite: Cuidou de uma planta por 10 dias seguidos!");
	}

	// CONQUISTAS DE ESTÁGIOS DA PLANTA

	// Alcançar o estágio de crescimento
	public Achievement concederEstagioCrescimento(UUID userId) {
		return concederConquista(userId, "🍃 Brotando Fortes: Uma de suas plantas alcançou o estágio de crescimento!");
	}

	// Alcançar o estágio de colheita ou floração
	public Achievement concederEstagioColheitaOuFloracao(UUID userId) {
		return concederConquista(userId,
				"🌸 Florescer e Colher: Uma de suas plantas alcançou a fase de floração/colheita!");
	}

	// CONQUISTAS DE ACESSO AO APP

	// Entrar diariamente no app por 5 dias
	public Achievement concederAcessoDiario5Dias(UUID userId) {
		return concederConquista(userId, "📱 Presença Diária: Entrou no aplicativo por 5 dias consecutivos!");
	}

	// CONSULTA

	// Buscar todas as conquistas do usuário
	public List<Achievement> obterConquistasDoUsuario(UUID userId) {
		return achievementRepository.findByUserId(userId);
	}

	// Como chamar no Controller ou em outros Services:
	// Sempre que o usuário fizer alguma ação no sistema (ex: criar conta ou regar
	// uma planta), basta injetar o AchievementService e dar a conquista:
	// Exemplo ao criar o usuário:
	// achievementService.grantWelcomeAchievement(user.getId());
}
