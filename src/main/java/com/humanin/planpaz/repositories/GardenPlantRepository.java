package com.humanin.planpaz.repositories;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.humanin.planpaz.model.GardenPlant;

public interface GardenPlantRepository extends JpaRepository<GardenPlant, UUID> {

	@EntityGraph(attributePaths = { "plant", "plant.care", "stage", "owner" })
	List<GardenPlant> findByOwnerId(UUID ownerId);

	boolean existsByOwnerIdAndNicknameIgnoreCase(UUID ownerId, String nickname);

	boolean existsByOwnerIdAndNicknameIgnoreCaseAndIdNot(UUID ownerId, String nickname, UUID id);

	@EntityGraph(attributePaths = { "plant", "plant.care", "stage", "owner" })
	Optional<GardenPlant> findByIdAndOwnerId(UUID id, UUID ownerId);
	
	long countByOwnerId(UUID ownerId);

	// Usado pelo job diário de e-mail e pelo job horário de push: só notifica quem manteve o checkbox ativado
	@EntityGraph(attributePaths = { "owner", "plant", "plant.care", "stage" })
	List<GardenPlant> findByWateringNotificationTrue();

	@Query("SELECT gp FROM GardenPlant gp JOIN FETCH gp.owner o JOIN FETCH gp.plant p LEFT JOIN FETCH p.care WHERE o.id IN (SELECT f.followed.id FROM Follow f WHERE f.follower.id = :userId) AND o.id <> :userId ORDER BY gp.plantedAt DESC")
	List<GardenPlant> findRecentByFollowedUsers(@Param("userId") UUID userId, Pageable pageable);
}
