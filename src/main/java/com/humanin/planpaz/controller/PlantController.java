package com.humanin.planpaz.controller;

import java.util.List;
import java.util.UUID;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.humanin.planpaz.model.Plant;
import com.humanin.planpaz.service.PlantService;
import com.humanin.planpaz.service.PlantStageService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/species")
@RequiredArgsConstructor
public class PlantController {
	private final PlantService plantService;
	private final PlantStageService plantStageService;

	// ==========================
	// LISTA TODAS AS ESPÉCIES DO BANCO
	// ==========================

	@GetMapping
	public ResponseEntity<List<Plant>> listSpecies() {
		return ResponseEntity.ok(plantService.listarPlantas());
	}

	// ==========================
	// ADICIONA UMA ESPÉCIE NO BANCO
	// ==========================

	@PostMapping
	public ResponseEntity<String> addSpecie(@RequestBody Plant plant) {
		plantService.adicionarPlanta(plant);
		return ResponseEntity.ok("Planta criada com sucesso.");
	}

	// ==========================
	// EDITA UMA ESPÉCIE DO BANCO
	// ==========================

	@PutMapping("/{id}")
	public ResponseEntity<String> editSpecie(@PathVariable UUID id, @RequestBody Plant plant) {
		plant.setId(id);
		plantService.editarPlanta(plant);
		return ResponseEntity.ok("Planta atualizada com sucesso.");
	}

	// ==========================
	// DELETA UMA ESPÉCIE DO BANCO
	// ==========================

	@DeleteMapping("/{id}")
	public ResponseEntity<String> deleteSpecie(@PathVariable UUID id) {
		plantService.excluirPlanta(id);
		return ResponseEntity.ok("Planta deletada com sucesso.");
	}
	
	// ==========================
	// RETORNA UMA ÚNICA ESPÉCIE DO BANCO
	// ==========================

	@GetMapping("/{id}")
	public ResponseEntity<Plant> getSpecie(@PathVariable UUID id) {
		return ResponseEntity.ok(plantService.buscarPorId(id));
	}

	// ==========================
	// RETORNA OS ESTÁGIOS DE UMA ESPÉCIE ESPECÍFICA DO BANCO
	// ==========================

	@GetMapping("/{id}/stages")
	public ResponseEntity<List<com.humanin.planpaz.model.PlantStage>> getSpecieStages(@PathVariable UUID id) {
		return ResponseEntity.ok(plantStageService.getStages(id));
	}
}
