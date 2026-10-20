package com.humanin.planpaz.service;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.humanin.planpaz.dto.PlantCareResponseDTO;
import com.humanin.planpaz.infra.exception.ResourceNotFoundException;
import com.humanin.planpaz.model.Plant;
import com.humanin.planpaz.model.PlantCare;
import com.humanin.planpaz.repositories.PlantCareRepository;
import com.humanin.planpaz.repositories.PlantRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PlantCareService {

	private final PlantCareRepository plantCareRepository;
	private final PlantRepository plantRepository;

	@Transactional(readOnly = true)
	public PlantCareResponseDTO buscarPorPlantId(UUID plantId) {
		return plantCareRepository.findByPlantId(plantId)
				.map(PlantCareResponseDTO::fromEntity)
				.orElseThrow(() -> new ResourceNotFoundException("Cuidados não encontrados para esta planta."));
	}

	@Transactional(readOnly = true)
	public PlantCareResponseDTO buscarPorNomePlanta(String plantName) {
		return plantCareRepository.findByPlantNameIgnoreCase(plantName)
				.map(PlantCareResponseDTO::fromEntity)
				.orElse(null);
	}

	@Transactional(readOnly = true)
	public List<PlantCareResponseDTO> listarTodos() {
		return plantCareRepository.findAll().stream()
				.map(PlantCareResponseDTO::fromEntity)
				.toList();
	}

	@Transactional
	public PlantCare salvarOuAtualizar(UUID plantId, PlantCare care) {
		Plant plant = plantRepository.findById(plantId)
				.orElseThrow(() -> new ResourceNotFoundException("Planta não encontrada."));

		PlantCare existing = plantCareRepository.findByPlantId(plantId).orElse(null);
		if (existing != null) {
			existing.setCultivationMethod(care.getCultivationMethod());
			existing.setMaterials(care.getMaterials());
			existing.setPropagationInstructions(care.getPropagationInstructions());
			existing.setSoil(care.getSoil());
			existing.setClimate(care.getClimate());
			existing.setLighting(care.getLighting());
			existing.setWatering(care.getWatering());
			existing.setPruning(care.getPruning());
			existing.setHarvesting(care.getHarvesting());
			existing.setDifficulty(care.getDifficulty());
			existing.setToxicityAlert(care.getToxicityAlert());
			existing.setHazardsAlert(care.getHazardsAlert());
			existing.setInvasiveAlert(care.getInvasiveAlert());
			existing.setTransplantNotes(care.getTransplantNotes());
			existing.setContainerSpecs(care.getContainerSpecs());
			existing.setGeneralTips(care.getGeneralTips());
			return plantCareRepository.save(existing);
		}

		care.setPlant(plant);
		return plantCareRepository.save(care);
	}
}
