package com.humanin.planpaz.service;

import java.util.List;
import java.util.UUID;
import org.springframework.stereotype.Service;
import com.humanin.planpaz.infra.exception.BusinessException;
import com.humanin.planpaz.infra.exception.ResourceNotFoundException;
import com.humanin.planpaz.model.Plant;
import com.humanin.planpaz.repositories.PlantRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PlantService {
	public final PlantRepository plantRepository;

	public Plant adicionarPlanta(Plant planta) {
		if (plantRepository.existsByNameIgnoreCase(planta.getName())) {
			throw new BusinessException("Já existe uma planta com esse nome.");
		}

		return plantRepository.save(planta);
	}

	public List<Plant> listarPlantas() {
		return plantRepository.findAll();
	}

	public Plant editarPlanta(Plant planta) {
		Plant novaPlanta = plantRepository.findById(planta.getId())
				.orElseThrow(() -> new ResourceNotFoundException("Planta não encontrada com ID: " + planta.getId()));

		if (plantRepository.existsByNameIgnoreCaseAndIdNot(planta.getName(), planta.getId())) {
			throw new BusinessException("Já existe uma planta com esse nome.");
		}

		novaPlanta.setName(planta.getName());
		novaPlanta.setDescription(planta.getDescription());
		novaPlanta.setImagePath(planta.getImagePath());
		novaPlanta.setLuminosityLevel(planta.getLuminosityLevel());
		novaPlanta.setScientificName(planta.getScientificName());
		novaPlanta.setSize(planta.getSize());
		novaPlanta.setTemperatureLevel(planta.getTemperatureLevel());
		novaPlanta.setType(planta.getType());
		novaPlanta.setWateringLevel(planta.getWateringLevel());

		return plantRepository.save(novaPlanta);
	}

	public void excluirPlanta(UUID idPlanta) {
		if (!plantRepository.existsById(idPlanta)) {
			throw new ResourceNotFoundException("Planta não encontrada com ID: " + idPlanta);
		}

		plantRepository.deleteById(idPlanta);
	}

	public Plant buscarPorId(UUID id) {
		return plantRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Planta não encontrada com ID: " + id));
	}
}
