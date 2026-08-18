package com.humanin.planpaz.controller;

import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.humanin.planpaz.dto.ClimaResponseDTO;
import com.humanin.planpaz.dto.StatusRegaDTO;
import com.humanin.planpaz.model.GardenPlant;
import com.humanin.planpaz.model.User;
import com.humanin.planpaz.service.ClimaService;
import com.humanin.planpaz.service.EmailService;
import com.humanin.planpaz.service.GardenPlantService;
import com.humanin.planpaz.service.RegaService;

@RestController
@RequestMapping({ "/garden-plants", "/api/plantas" })
public class GardenPlantController {

	@Autowired
	private GardenPlantService gardenPlantService;

	@Autowired
	private ClimaService climaService;

	@Autowired
	private RegaService agendamentoRegaService;

	@Autowired
	private EmailService emailService;

	private User getAuthenticatedUser(Authentication authentication) {
		return (User) authentication.getPrincipal();
	}

	// =========================
	// ADICIONAR
	// =========================

	@PostMapping
	public ResponseEntity<String> adicionar(@RequestBody GardenPlant gardenPlant, Authentication authentication) {
		User user = getAuthenticatedUser(authentication);
		gardenPlant.setOwner(user);

		boolean created = gardenPlantService.adicionarPlanta(gardenPlant);

		if (!created) {
			return ResponseEntity.badRequest().body("Já existe uma planta com esse apelido.");
		}

		return ResponseEntity.ok("Planta adicionada ao jardim.");
	}

	// =========================
	// LISTAR
	// =========================

	@GetMapping
	public ResponseEntity<List<GardenPlant>> listar(Authentication authentication) {
		User user = getAuthenticatedUser(authentication);
		List<GardenPlant> plantas = gardenPlantService.listarPorUsuario(user.getId());

		return ResponseEntity.ok(plantas);
	}

	// =========================
	// EDITAR
	// =========================

	@PutMapping("/{id}")
	public ResponseEntity<String> editar(@PathVariable UUID id, @RequestBody GardenPlant gardenPlant,
			Authentication authentication) {
		User user = getAuthenticatedUser(authentication);

		boolean updated = gardenPlantService.editar(id, user.getId(), gardenPlant);

		if (!updated) {
			return ResponseEntity.badRequest().body("Erro ao atualizar a planta.");
		}

		return ResponseEntity.ok("Planta atualizada com sucesso.");
	}

	// =========================
	// EXCLUIR
	// =========================

	@DeleteMapping("/{id}")
	public ResponseEntity<String> excluir(@PathVariable UUID id, Authentication authentication) {
		User user = getAuthenticatedUser(authentication);

		boolean deleted = gardenPlantService.excluirPlanta(id, user.getId());

		if (!deleted) {
			return ResponseEntity.badRequest().body("Planta não encontrada.");
		}

		return ResponseEntity.ok("Planta excluída com sucesso.");
	}

	// =========================
	// REGAR
	// =========================

	@PostMapping("/{id}/watering")
	public ResponseEntity<String> regar(@PathVariable UUID id, Authentication authentication) {
		User user = getAuthenticatedUser(authentication);

		boolean watered = gardenPlantService.registrarRega(id, user.getId());

		if (!watered) {
			return ResponseEntity.badRequest().body("Planta não encontrada.");
		}

		return ResponseEntity.ok("Planta regada com sucesso.");
	}

	// =========================
	// CONSULTAR STATUS DE REGA + CLIMA + NOTIFICAÇÃO
	// =========================

	@GetMapping("/{id}/status-rega")
	public ResponseEntity<?> verificarStatusRega(@PathVariable UUID id, @RequestParam String cidade,
			Authentication authentication) {

		User user = getAuthenticatedUser(authentication);

		// Busca a planta garantindo pertencimento ao usuário autenticado
		GardenPlant planta = gardenPlantService.buscarPorIdEUsuario(id, user.getId());
		if (planta == null) {
			return ResponseEntity.badRequest().body("Planta não encontrada para este usuário.");
		}

		// Busca o clima da cidade informada
		ClimaResponseDTO clima = climaService.buscarClimaPorCidade(cidade);

		// Processa a recomendação com base nas regras do sistema
		String recomendacao = agendamentoRegaService.calcularProximaRega(planta, clima);

		// Dispara o e-mail de notificação para o usuário autenticado
		emailService.enviarAlertaRega(user.getEmail(), planta.getNickName(), recomendacao);

		StatusRegaDTO resposta = new StatusRegaDTO(planta.getId(), planta.getNickName(), cidade, clima.getTemperatura(),
				clima.getUmidade(), clima.isChovendo(), recomendacao);

		return ResponseEntity.ok(resposta);
	}
}