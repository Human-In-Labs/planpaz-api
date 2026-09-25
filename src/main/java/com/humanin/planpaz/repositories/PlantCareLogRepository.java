package com.humanin.planpaz.repositories;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.humanin.planpaz.model.PlantCareLog;
import com.humanin.planpaz.model.enums.CareType;

@Repository
public interface PlantCareLogRepository extends JpaRepository<PlantCareLog, UUID> {
	List<PlantCareLog> findByGardenPlantIdOrderByPerformedAtDesc(UUID gardenPlantId);

	Optional<PlantCareLog> findFirstByGardenPlantIdAndCareTypeOrderByPerformedAtDesc(UUID gardenPlantId,
			CareType careType);

	List<PlantCareLog> findByUserId(UUID userId);

	long countByUserIdAndCareType(UUID userId, CareType careType);

	void deleteByGardenPlantId(UUID gardenPlantId);
}
