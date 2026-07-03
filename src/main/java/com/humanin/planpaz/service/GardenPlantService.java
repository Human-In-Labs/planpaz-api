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

	public List<GardenPlant> listarPorUsuario(UUID ownerId) {
		return gardenPlantRepository.findByOwnerId(ownerId);
	}

	public boolean editar(GardenPlant gardenPlant) {

		Optional<GardenPlant> optional = gardenPlantRepository.findById(gardenPlant.getId());

		if (optional.isEmpty()) {
			return false;
		}

		if (gardenPlantRepository.existsByOwnerIdAndNickNameIgnoreCaseAndIdNot(gardenPlant.getOwner().getId(),
				gardenPlant.getNickName(), gardenPlant.getId())) {
			return false;
		}

		GardenPlant planta = optional.get();

		planta.setNickName(gardenPlant.getNickName());
		planta.setLastWatering(gardenPlant.getLastWatering());
		planta.setStage(gardenPlant.getStage());

		gardenPlantRepository.save(planta);

		return true;
	}

	public boolean excluirPlanta(UUID id) {

		if (!gardenPlantRepository.existsById(id)) {
			return false;
		}

		gardenPlantRepository.deleteById(id);
		return true;
	}

	public boolean registrarRega(UUID id) {

		Optional<GardenPlant> optional = gardenPlantRepository.findById(id);

		if (optional.isEmpty()) {
			return false;
		}

		GardenPlant planta = optional.get();
		planta.setLastWatering(LocalDate.now());

		gardenPlantRepository.save(planta);

		return true;
	}

}
