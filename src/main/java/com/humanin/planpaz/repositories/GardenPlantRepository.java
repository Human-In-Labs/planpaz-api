package com.humanin.planpaz.repositories;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.humanin.planpaz.model.GardenPlant;

public interface GardenPlantRepository extends JpaRepository<GardenPlant, UUID> {

	List<GardenPlant> findByOwnerId(UUID ownerId);

	boolean existsByOwnerIdAndNickNameIgnoreCase(UUID ownerId, String nickName);

	boolean existsByOwnerIdAndNickNameIgnoreCaseAndIdNot(UUID ownerId, String nickName, UUID id);
}