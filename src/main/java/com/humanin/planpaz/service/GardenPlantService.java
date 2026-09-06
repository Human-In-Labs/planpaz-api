package com.humanin.planpaz.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.humanin.planpaz.model.GardenPlant;
import com.humanin.planpaz.model.PlantStage;
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
				.orElseThrow(() -> new RuntimeException("Planta não encontrada com ID: " + gardenPlantId));

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
		gardenPlant.setPlantedAt(LocalDateTime.now());
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

	public List<GardenPlant> listarPlantasDoUsuario(UUID ownerId) {
		return listarPorUsuario(ownerId);
	}

	// BUSCAR POR ID E USUÁRIO
	public GardenPlant buscarPorIdEUsuario(UUID id, UUID ownerId) {
		return gardenPlantRepository.findByIdAndOwnerId(id, ownerId).orElse(null);
	}

	public Optional<GardenPlant> buscarPorId(UUID id) {
		return gardenPlantRepository.findById(id);
	}

	// EDITAR
	@Transactional
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

	@Transactional
	public boolean atualizarPlanta(UUID id, UUID ownerId, GardenPlant gardenPlant) {
		return editar(id, ownerId, gardenPlant);
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
			if (ordem == 2) { // Estágio 2 = Crescimento
				achievementService.concederEstagioCrescimento(ownerId);
			} else if (ordem >= 3) { // Estágio 3 = Colheita/Floração
				achievementService.concederEstagioColheitaOuFloracao(ownerId);
			}
		}
	}

	private void verificarConquistasDeSequencia(UUID ownerId, GardenPlant planta) {
		if (planta.getPlantedAt() != null) {
			long diasDeCultivo = ChronoUnit.DAYS.between(planta.getPlantedAt().toLocalDate(), LocalDate.now());

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