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

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/plants")
@RequiredArgsConstructor
public class PlantController {
	private final PlantService plantService;

	@GetMapping
	public ResponseEntity<List<Plant>> listar() {
		return ResponseEntity.ok(plantService.listarPlantas());
	}

	@PostMapping
	public ResponseEntity<String> adicionar(@RequestBody Plant plant) {
		boolean created = plantService.adicionarPlanta(plant);

		if (!created) {
			return ResponseEntity.badRequest().body("Já existe uma planta com esse nome.");
		}
		return ResponseEntity.ok("Planta criada com sucesso.");
	}

	@PutMapping("/{id}")
	public ResponseEntity<String> editar(@PathVariable UUID id, @RequestBody Plant plant) {
		plant.setId(id);
		boolean updated = plantService.editarPlanta(plant);

		if (!updated) {
			return ResponseEntity.badRequest()
					.body("[ERRO]: Erro ao atualizar: nome duplicado ou planta não encontrada.");
		}
		return ResponseEntity.ok("Planta atualizada com sucesso.");
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<String> deletar(@PathVariable UUID id) {
 		boolean deleted = plantService.excluirPlanta(id);

	    if (!deleted) {
	        return ResponseEntity.notFound().build();
	    }
	    return ResponseEntity.ok("Planta deletada com sucesso.");
	}
	
	
	private final com.humanin.planpaz.repositories.PlantStageRepository plantStageRepository;

	@GetMapping("/{id}")
	public ResponseEntity<Plant> buscarPorId(@PathVariable UUID id) {
	    Plant plant = plantService.buscarPorId(id);
	    return ResponseEntity.ok(plant);
	}

	@GetMapping("/{id}/stages")
	public ResponseEntity<List<com.humanin.planpaz.model.PlantStage>> buscarEstagiosPorPlanta(@PathVariable UUID id) {
		return ResponseEntity.ok(plantStageRepository.findByPlantIdOrderByOrderAsc(id));
	}
}
