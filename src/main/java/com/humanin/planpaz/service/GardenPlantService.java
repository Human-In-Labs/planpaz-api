package com.humanin.planpaz.service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.humanin.planpaz.model.GardenPlant;
import com.humanin.planpaz.repositories.GardenPlantRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class GardenPlantService {

	private final GardenPlantRepository gardenPlantRepository;
	private final EmailService emailService;
	private final AchievementService achievementService; // Injeção do serviço de conquistas

	// EMAIL
	public String verificarEEnviarStatusRega(UUID gardenPlantId, String cidade) {
		GardenPlant gardenPlant = gardenPlantRepository.findById(gardenPlantId)
				.orElseThrow(() -> new RuntimeException("Planta da horta não encontrada com o ID: " + gardenPlantId));

		String statusRega = calcularStatusRega(gardenPlant, cidade);

		String emailUsuario = gardenPlant.getOwner().getEmail();
		String nomePlanta = gardenPlant.getNickname();

		emailService.enviarAlertaRega(emailUsuario, nomePlanta, statusRega);

		return statusRega;
	}

	private String calcularStatusRega(GardenPlant gardenPlant, String cidade) {
		return "Sua planta precisa ser regada hoje por conta do clima seco em " + cidade + "!";
	}

	// ADICIONAR
	@Transactional
	public boolean adicionarPlanta(GardenPlant gardenPlant) {
		gardenPlant.setPlantedAt(LocalDate.now());
		gardenPlant.setLastWatering(LocalDate.now());

		UUID ownerId = gardenPlant.getOwner().getId();

		if (gardenPlantRepository.existsByOwnerIdAndNicknameIgnoreCase(ownerId, gardenPlant.getNickname())) {
			return false;
		}

		gardenPlantRepository.save(gardenPlant);

		// VERIFICAÇÃO AUTOMÁTICA DE CONQUISTAS (Quantidade de Plantas)
		verificarConquistasDeCultivo(ownerId);

		return true;
	}

	// LISTAR
	public List<GardenPlant> listarPorUsuario(UUID ownerId) {
		return gardenPlantRepository.findByOwnerId(ownerId);
	}

	// BUSCAR POR ID E USUÁRIO
	public GardenPlant buscarPorIdEUsuario(UUID id, UUID ownerId) {
		return gardenPlantRepository.findByIdAndOwnerId(id, ownerId).orElse(null);
	}

	// EDITAR
	public boolean editar(UUID id, UUID ownerId, GardenPlant gardenPlant) {
		Optional<GardenPlant> optional = gardenPlantRepository.findById(id);

		if (optional.isEmpty()) {
			return false;
		}

		GardenPlant planta = optional.get();

		if (!planta.getOwner().getId().equals(ownerId)) {
			return false;
		}

		if (gardenPlantRepository.existsByOwnerIdAndNicknameIgnoreCaseAndIdNot(ownerId, gardenPlant.getNickname(),
				id)) {
			return false;
		}

		planta.setNickname(gardenPlant.getNickname());
		planta.setStage(gardenPlant.getStage());

		if (gardenPlant.getLastWatering() != null) {
			planta.setLastWatering(gardenPlant.getLastWatering());
		}

		gardenPlantRepository.save(planta);

		// VERIFICAÇÃO AUTOMÁTICA DE CONQUISTAS (Estágio da Planta)
		verificarConquistasDeEstagio(ownerId, planta.getStage());

		return true;
	}

	// EXCLUIR
	public boolean excluirPlanta(UUID id, UUID ownerId) {
		Optional<GardenPlant> optional = gardenPlantRepository.findById(id);

		if (optional.isEmpty()) {
			return false;
		}

		GardenPlant planta = optional.get();

		if (!planta.getOwner().getId().equals(ownerId)) {
			return false;
		}

		gardenPlantRepository.delete(planta);

		return true;
	}

	// REGAR
	public boolean registrarRega(UUID id, UUID ownerId) {
		Optional<GardenPlant> optional = gardenPlantRepository.findById(id);

		if (optional.isEmpty()) {
			return false;
		}

		GardenPlant planta = optional.get();

		if (!planta.getOwner().getId().equals(ownerId)) {
			return false;
		}

		planta.setLastWatering(LocalDate.now());
		gardenPlantRepository.save(planta);

		// VERIFICAÇÃO AUTOMÁTICA DE CONQUISTAS (Sequência de Cuidados)
		verificarConquistasDeSequencia(ownerId, planta);

		return true;
	}

	// REGRAS DE VERIFICAÇÃO DE CONQUISTAS

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

	private void verificarConquistasDeEstagio(UUID ownerId, Integer estagio) {
		if (estagio != null) {
			if (estagio == 2) { // Exemplo: Estágio 2 = Crescimento
				achievementService.concederEstagioCrescimento(ownerId);
			} else if (estagio >= 3) { // Exemplo: Estágio 3 = Colheita/Floração
				achievementService.concederEstagioColheitaOuFloracao(ownerId);
			}
		}
	}

	private void verificarConquistasDeSequencia(UUID ownerId, GardenPlant planta) {
		if (planta.getPlantedAt() != null) {
			long diasDeCultivo = java.time.temporal.ChronoUnit.DAYS.between(planta.getPlantedAt(), LocalDate.now());

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