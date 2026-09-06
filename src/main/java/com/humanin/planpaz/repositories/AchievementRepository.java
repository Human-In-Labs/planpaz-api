package com.humanin.planpaz.repositories;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.humanin.planpaz.model.Achievement;

public interface AchievementRepository extends JpaRepository<Achievement, UUID> {

	Optional<Achievement> findByName(String name);

	boolean existsByName(String name);
}