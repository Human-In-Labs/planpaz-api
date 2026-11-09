package com.humanin.planpaz.service;

import java.util.List;
import java.util.UUID;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import com.humanin.planpaz.config.CacheConfig;
import com.humanin.planpaz.dto.PlantStageResponseDTO;
import com.humanin.planpaz.model.PlantStage;
import com.humanin.planpaz.repositories.PlantStageRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PlantStageService {
    private final PlantStageRepository plantStageRepository;

    @Cacheable(value = CacheConfig.PLANT_STAGES_CACHE, key = "'entities:' + #plantId", unless = "#result == null || #result.isEmpty()")
    public List<PlantStage> getStages(UUID plantId) {
        return plantStageRepository.findByPlantIdOrderByOrderAsc(plantId);
    }

    @Cacheable(value = CacheConfig.PLANT_STAGES_CACHE, key = "'dtos:' + #plantId", unless = "#result == null || #result.isEmpty()")
    public List<PlantStageResponseDTO> getStageDTOs(UUID plantId) {
        return plantStageRepository.findByPlantIdOrderByOrderAsc(plantId)
                .stream()
                .map(PlantStageResponseDTO::fromEntity)
                .toList();
    }
}
