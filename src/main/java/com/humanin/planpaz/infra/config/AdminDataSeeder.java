package com.humanin.planpaz.infra.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import com.humanin.planpaz.model.Admin;
import com.humanin.planpaz.repositories.AdminRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class AdminDataSeeder implements CommandLineRunner {

	private final AdminRepository adminRepository;
	private final PasswordEncoder passwordEncoder;

	@Override
	public void run(String... args) throws Exception {
		if (!adminRepository.existsByEmail("admin@planpaz.com")) {
			Admin defaultAdmin = new Admin();
			defaultAdmin.setName("Administrador PlanPaz");
			defaultAdmin.setEmail("admin@planpaz.com");
			defaultAdmin.setPassword(passwordEncoder.encode("admin"));

			adminRepository.save(defaultAdmin);

			log.info("[ADMIN_SEEDER] Administrador inicial cadastrado com sucesso: admin@planpaz.com");
		}
	}
}
