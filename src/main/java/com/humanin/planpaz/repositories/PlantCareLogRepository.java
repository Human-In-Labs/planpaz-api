package com.humanin.planpaz.repositories;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.humanin.planpaz.model.PlantCareLog;
import com.humanin.planpaz.model.enums.CareType;

@Repository
public interface PlantCareLogRepository extends JpaRepository<PlantCareLog, UUID> {
	List<PlantCareLog> findByGardenPlantIdOrderByPerformedAtDesc(UUID gardenPlantId);
	
	Optional<PlantCareLog> findFirstByGardenPlantIdAndCareTypeOrderByPerformedAtDesc(UUID gardenPlantId, CareType careType);

	List<PlantCareLog> findByUserId(UUID userId);
	
	long countByUserIdAndCareType(UUID userId, CareType careType);

	@Query("SELECT l.careType, COUNT(l) FROM PlantCareLog l WHERE l.user.id = :userId GROUP BY l.careType")
	List<Object[]> countByUserIdGroupByCareType(@Param("userId") UUID userId);

	@Query("SELECT COUNT(DISTINCT CAST(l.performedAt AS LocalDate)) FROM PlantCareLog l WHERE l.user.id = :userId")
	long countDistinctCareDaysByUserId(@Param("userId") UUID userId);

	void deleteByGardenPlantId(UUID gardenPlantId);
}
