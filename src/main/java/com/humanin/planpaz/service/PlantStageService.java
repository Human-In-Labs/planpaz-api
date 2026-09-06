package com.humanin.planpaz.service;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.humanin.planpaz.model.PlantStage;
import com.humanin.planpaz.repositories.PlantStageRepository;

import lombok.RequiredArgsConstructor;

@Service 
@RequiredArgsConstructor 
public class PlantStageService {   
    private final PlantStageRepository plantStageRepository;

    public List<PlantStage> getStages(UUID plantId) {
        return plantStageRepository.findByPlantIdOrderByOrderAsc(plantId);
    }
}
