package com.humanin.planpaz.repositories;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.humanin.planpaz.model.PasswordResetToken;

public interface PasswordResetTokenRepository extends JpaRepository<PasswordResetToken, UUID> {

	Optional<PasswordResetToken> findByEmailAndCodeAndUsedFalse(String email, String code);

	Optional<PasswordResetToken> findTopByEmailAndUsedFalseOrderByExpiresAtDesc(String email);

	@Modifying
	@Query("DELETE FROM PasswordResetToken t WHERE t.email = :email")
	void deleteByEmail(@Param("email") String email);
}
