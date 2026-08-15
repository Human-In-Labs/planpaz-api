package com.humanin.planpaz.service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.humanin.planpaz.model.GardenPlant;
import com.humanin.planpaz.repositories.GardenPlantRepository;

@Service
public class GardenPlantService {

	@Autowired
	private GardenPlantRepository gardenPlantRepository;

	// ADICIONAR

	public boolean adicionarPlanta(GardenPlant gardenPlant) {
		
		gardenPlant.setPlantedAt(LocalDate.now());
		gardenPlant.setLastWatering(LocalDate.now());

		if (gardenPlantRepository.existsByOwnerIdAndNickNameIgnoreCase(gardenPlant.getOwner().getId(),
				gardenPlant.getNickName())) {

			return false;
		}

		gardenPlantRepository.save(gardenPlant);

		return true;
	}

	// LISTAR

	public List<GardenPlant> listarPorUsuario(UUID ownerId) {
 
		return gardenPlantRepository.findByOwnerId(ownerId);
	}

	// EDITAR

	public boolean editar(UUID id, UUID ownerId, GardenPlant gardenPlant) {

		Optional<GardenPlant> optional = gardenPlantRepository.findById(id);

		if (optional.isEmpty()) {
			return false;
		}

		GardenPlant planta = optional.get();

		// Impede editar planta de outro usuário
		if (!planta.getOwner().getId().equals(ownerId)) {
			return false;
		}

		// Verifica apelido duplicado
		if (gardenPlantRepository.existsByOwnerIdAndNickNameIgnoreCaseAndIdNot(ownerId, gardenPlant.getNickName(),
				id)) {

			return false;
		}

		planta.setNickName(gardenPlant.getNickName());
		planta.setStage(gardenPlant.getStage());

		if (gardenPlant.getLastWatering() != null) {
			planta.setLastWatering(gardenPlant.getLastWatering());
		}

		gardenPlantRepository.save(planta);

		return true;
	}

	// EXCLUIR

	public boolean excluirPlanta(UUID id, UUID ownerId) {

		Optional<GardenPlant> optional = gardenPlantRepository.findById(id);

		if (optional.isEmpty()) {
			return false;
		}

		GardenPlant planta = optional.get();

		// Não permite excluir planta de outro usuário
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

		// Não permite regar planta de outro usuário
		if (!planta.getOwner().getId().equals(ownerId)) {
			return false;
		}

		planta.setLastWatering(LocalDate.now());

		gardenPlantRepository.save(planta);

		return true;
	}
}