package com.humanin.planpaz.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.humanin.planpaz.model.User;

public interface UserRepository extends JpaRepository<User, String> {
	Optional<User> findByEmail(String email);
}
