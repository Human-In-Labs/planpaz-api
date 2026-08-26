package com.humanin.planpaz.repositories;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.humanin.planpaz.model.GardenPlant;

public interface GardenPlantRepository extends JpaRepository<GardenPlant, UUID> {

	List<GardenPlant> findByOwnerId(UUID ownerId);

	boolean existsByOwnerIdAndNicknameIgnoreCase(UUID ownerId, String nickname);

	boolean existsByOwnerIdAndNicknameIgnoreCaseAndIdNot(UUID ownerId, String nickname, UUID id);

	Optional<GardenPlant> findByIdAndOwnerId(UUID id, UUID ownerId);
}