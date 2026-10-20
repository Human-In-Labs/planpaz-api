package com.humanin.planpaz.repositories;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import com.humanin.planpaz.model.PlantCare;

public interface PlantCareRepository extends JpaRepository<PlantCare, UUID> {

	Optional<PlantCare> findByPlantId(UUID plantId);

	Optional<PlantCare> findByPlantNameIgnoreCase(String name);
}
