package com.humanin.planpaz.repositories;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import com.humanin.planpaz.model.Plant;

public interface PlantRepository extends JpaRepository<Plant, UUID> {

	@Override
	@EntityGraph(attributePaths = { "care" })
	List<Plant> findAll();

	@Override
	@EntityGraph(attributePaths = { "care" })
	Optional<Plant> findById(UUID id);

	List<Plant> findByNameContaining(String name);

	List<Plant> findByNameIgnoreCase(String name);

	List<Plant> findByNameStartingWith(String name);

	Plant findByName(String name); // para fazer querys

	boolean existsByNameIgnoreCase(String name);

	boolean existsByNameIgnoreCaseAndIdNot(String name, UUID id);
}
