package com.humanin.planpaz.repositories;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import com.humanin.planpaz.model.PlantStage;

public interface PlantStageRepository extends JpaRepository<PlantStage, UUID> {

	List<PlantStage> findByPlantIdOrderByOrderAsc(UUID plantId);
}
