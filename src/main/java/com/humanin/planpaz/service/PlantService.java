package com.humanin.planpaz.service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.humanin.planpaz.model.Plant;
import com.humanin.planpaz.repositories.PlantRepository;

@Service
public class PlantService {

	@Autowired
	public PlantRepository plantRepository;

	public boolean adicionarPlanta(Plant planta) {

		if (plantRepository.existsByNameIgnoreCase(planta.getName())) {
			return false;
		}

		plantRepository.save(planta);
		return true;
	}

	public List<Plant> listarPlantas() {
		return plantRepository.findAll();
	}

	public boolean editarPlanta(Plant planta) {
//optional pq pode existir uma planta ou nenhuma
		Optional<Plant> plantaOptional = plantRepository.findById(planta.getId());

		if (plantaOptional.isEmpty()) {
			return false;
		}

		if (plantRepository.existsByNameIgnoreCaseAndIdNot(planta.getName(), planta.getId())) {
			return false;
		}

		Plant novaPlanta = plantaOptional.get();

		novaPlanta.setName(planta.getName());
		novaPlanta.setDescription(planta.getDescription());
		novaPlanta.setImagePath(planta.getImagePath());
		novaPlanta.setLuminosityLevel(planta.getLuminosityLevel());
		novaPlanta.setScientificName(planta.getScientificName());
		novaPlanta.setSize(planta.getSize());
		novaPlanta.setTemperatureLevel(planta.getTemperatureLevel());
		novaPlanta.setType(planta.getType());
		novaPlanta.setWateringLevel(planta.getWateringLevel());

		plantRepository.save(novaPlanta);

		return true;
	}

	public boolean excluirPlanta(UUID idPlanta) {

		if (!plantRepository.existsById(idPlanta)) {
			return false;
		}

		plantRepository.deleteById(idPlanta);
		return true;
	}

	public Plant buscarPorId(UUID id) {

	    return plantRepository.findById(id)
	            .orElseThrow(() ->
	                new RuntimeException("Planta não encontrada.")
	            );
	}

}
