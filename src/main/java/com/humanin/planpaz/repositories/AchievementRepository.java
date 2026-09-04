package com.humanin.planpaz.repositories;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.humanin.planpaz.model.Achievement;

@Repository
public interface AchievementRepository extends JpaRepository<Achievement, UUID> {

	// Busca todas as conquistas pertencentes a um ID de usuário específico
	List<Achievement> findByUserId(UUID userId);

	boolean existsByUserIdAndDescription(UUID userId, String descricao);
}