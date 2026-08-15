package com.humanin.planpaz.controller;

import java.util.List;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
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

@RestController
@RequestMapping("/plants")

public class PlantController {
	@Autowired
	private PlantService plantService;

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
	
	
	@GetMapping("/{id}")
	public ResponseEntity<Plant> buscarPorId(@PathVariable UUID id) {

	    Plant plant = plantService.buscarPorId(id);

	    return ResponseEntity.ok(plant);
	}
}
