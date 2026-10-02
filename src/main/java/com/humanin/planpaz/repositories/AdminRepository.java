package com.humanin.planpaz.repositories;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.humanin.planpaz.model.Admin;

@Repository
public interface AdminRepository extends JpaRepository<Admin, UUID> {
	Optional<Admin> findByEmail(String email);

	boolean existsByEmail(String email);
}
