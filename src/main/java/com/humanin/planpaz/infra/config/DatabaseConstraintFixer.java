package com.humanin.planpaz.infra.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@Order(1)
@RequiredArgsConstructor
public class DatabaseConstraintFixer implements CommandLineRunner {

	private final JdbcTemplate jdbcTemplate;

	@Override
	public void run(String... args) {
		try {
			// Remove a restrição legada criada pelo Hibernate que impedia novos valores do enum Room (ex: BALCONY, DINING_ROOM)
			jdbcTemplate.execute("ALTER TABLE public.garden_plant DROP CONSTRAINT IF EXISTS garden_plant_room_check");
			log.info("[DB_FIXER] Restrição 'garden_plant_room_check' atualizada com sucesso.");
		} catch (Exception e) {
			log.warn("[DB_FIXER] Aviso ao atualizar restrição de room na tabela garden_plant: {}", e.getMessage());
		}
	}
}
