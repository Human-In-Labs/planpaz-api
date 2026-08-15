package com.humanin.planpaz.controller;

import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import com.humanin.planpaz.model.GardenPlant;
import com.humanin.planpaz.model.User;
import com.humanin.planpaz.service.GardenPlantService;

@RestController
@RequestMapping("/garden-plants")
public class GardenPlantController {

    @Autowired
    private GardenPlantService gardenPlantService;

    /*
     * Pega diretamente o usuário que foi colocado
     * no Authentication pelo SecurityFilter.
     */
    private User getAuthenticatedUser(Authentication authentication) {

        return (User) authentication.getPrincipal();
    }

    // ADICIONAR

    @PostMapping
    public ResponseEntity<String> adicionar(
            @RequestBody GardenPlant gardenPlant,
            Authentication authentication) {

        User user = getAuthenticatedUser(authentication);

        gardenPlant.setOwner(user);

        boolean created =
                gardenPlantService.adicionarPlanta(gardenPlant);

        if (!created) {
            return ResponseEntity
                    .badRequest()
                    .body("Já existe uma planta com esse apelido.");
        }

        return ResponseEntity
                .ok("Planta adicionada ao jardim.");
    }

    // LISTAR

    @GetMapping
    public ResponseEntity<List<GardenPlant>> listar(
            Authentication authentication) {

        User user = getAuthenticatedUser(authentication);

        List<GardenPlant> plantas =
                gardenPlantService.listarPorUsuario(user.getId());

        return ResponseEntity.ok(plantas);
    }

    // =========================
    // EDITAR
    // =========================

    @PutMapping("/{id}")
    public ResponseEntity<String> editar(
            @PathVariable UUID id,
            @RequestBody GardenPlant gardenPlant,
            Authentication authentication) {

        User user = getAuthenticatedUser(authentication);

        boolean updated =
                gardenPlantService.editar(
                        id,
                        user.getId(),
                        gardenPlant
                );

        if (!updated) {
            return ResponseEntity
                    .badRequest()
                    .body("Erro ao atualizar a planta.");
        }

        return ResponseEntity
                .ok("Planta atualizada com sucesso.");
    }

    // =========================
    // EXCLUIR
    // =========================

    @DeleteMapping("/{id}")
    public ResponseEntity<String> excluir(
            @PathVariable UUID id,
            Authentication authentication) {

        User user = getAuthenticatedUser(authentication);

        boolean deleted =
                gardenPlantService.excluirPlanta(
                        id,
                        user.getId()
                );

        if (!deleted) {
            return ResponseEntity
                    .badRequest()
                    .body("Planta não encontrada.");
        }

        return ResponseEntity
                .ok("Planta excluída com sucesso.");
    }

    // =========================
    // REGAR
    // =========================

    @PostMapping("/{id}/watering")
    public ResponseEntity<String> regar(
            @PathVariable UUID id,
            Authentication authentication) {

        User user = getAuthenticatedUser(authentication);

        boolean watered =
                gardenPlantService.registrarRega(
                        id,
                        user.getId()
                );

        if (!watered) {
            return ResponseEntity
                    .badRequest()
                    .body("Planta não encontrada.");
        }

        return ResponseEntity
                .ok("Planta regada com sucesso.");
    }
}