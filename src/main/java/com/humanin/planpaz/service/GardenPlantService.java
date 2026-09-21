package com.humanin.planpaz.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.humanin.planpaz.infra.exception.BusinessException;
import com.humanin.planpaz.infra.exception.ResourceNotFoundException;
import com.humanin.planpaz.model.GardenPlant;
import com.humanin.planpaz.model.PlantStage;
import com.humanin.planpaz.model.Plant;
import com.humanin.planpaz.model.PlantStage;
import com.humanin.planpaz.repositories.GardenPlantRepository;
import com.humanin.planpaz.repositories.PlantRepository;
import com.humanin.planpaz.repositories.PlantStageRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class GardenPlantService {

	private final GardenPlantRepository gardenPlantRepository;
	private final PlantRepository plantRepository;
	private final PlantStageRepository plantStageRepository;
	private final AchievementService achievementService; // Injeção do serviço de conquistas

	// ADICIONAR
	@Transactional
	public GardenPlant adicionarPlanta(GardenPlant gardenPlant) {
		if (gardenPlant.getPlantedAt() == null) {
			gardenPlant.setPlantedAt(LocalDateTime.now());
		}
		if (gardenPlant.getLastWatering() == null) {
			gardenPlant.setLastWatering(LocalDate.now());
		}

		UUID ownerId = gardenPlant.getOwner().getId();

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

		GardenPlant saved = gardenPlantRepository.save(gardenPlant);

		try {
			verificarConquistasDeCultivo(ownerId);
		} catch (Exception e) {
			System.err.println("[ACHIEVEMENTS] Erro ao verificar conquistas após adicionar planta: " + e.getMessage());
		}

		return saved;
	}

	// LISTAR
	@Transactional(readOnly = true)
	public List<GardenPlant> listarPorUsuario(UUID ownerId) {
		return gardenPlantRepository.findByOwnerId(ownerId);
	}

	// BUSCAR POR ID E USUÁRIO
	@Transactional(readOnly = true)
	public GardenPlant buscarPorIdEUsuario(UUID id, UUID ownerId) {
		return gardenPlantRepository.findByIdAndOwnerId(id, ownerId)
				.orElseThrow(() -> new ResourceNotFoundException("Planta não encontrada para este usuário."));
	}

	@Transactional(readOnly = true)
	public Optional<GardenPlant> buscarPorId(UUID id) {
		return gardenPlantRepository.findById(id);
	}

	// EDITAR
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

		return saved;
	}

	// EXCLUIR
	public void excluirPlanta(UUID id, UUID ownerId) {
		GardenPlant planta = gardenPlantRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Planta não encontrada com ID: " + id));

		if (!planta.getOwner().getId().equals(ownerId)) {
			throw new BusinessException("Esta planta não pertence ao usuário autenticado.");
		}

		gardenPlantRepository.delete(planta);
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

}