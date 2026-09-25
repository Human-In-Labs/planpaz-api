package com.humanin.planpaz.controller;

import java.util.List;
import java.util.UUID;

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

import com.humanin.planpaz.dto.ApiResponseDTO;
import com.humanin.planpaz.dto.PlantStatsDTO;
import com.humanin.planpaz.dto.WateringReminderDTO;
import com.humanin.planpaz.dto.WateringStatusDTO;
import com.humanin.planpaz.dto.WeatherResponseDTO;
import com.humanin.planpaz.model.GardenPlant;
import com.humanin.planpaz.model.User;
import com.humanin.planpaz.service.GardenPlantService;
import com.humanin.planpaz.service.WateringService;
import com.humanin.planpaz.service.WeatherService;

import lombok.RequiredArgsConstructor;

import com.humanin.planpaz.dto.AchievementProgressDTO;
import com.humanin.planpaz.service.AchievementService;

@RestController
@RequestMapping({"/api/garden", "/api/user-plants", "/api/plants"})
public class GardenPlantController {
	private final GardenPlantService gardenPlantService;
	private final WeatherService weatherService;
	private final WateringService wateringService;
	private final AchievementService achievementService;

	public GardenPlantController(GardenPlantService gardenPlantService, WeatherService weatherService,
			WateringService wateringService, AchievementService achievementService) {
		this.gardenPlantService = gardenPlantService;
		this.weatherService = weatherService;
		this.wateringService = wateringService;
		this.achievementService = achievementService;
	}

	private User getAuthenticatedUser(Authentication authentication) {
		return (User) authentication.getPrincipal();
	}

	// =========================
	// ADICIONAR UMA PLANTA AO MEU JARDIM
	// =========================

	@PostMapping("/add")
	public ResponseEntity<ApiResponseDTO> addGardenPlant(@RequestBody GardenPlant gardenPlant, Authentication authentication) {
		User user = getAuthenticatedUser(authentication);
		gardenPlant.setOwner(user);
		gardenPlantService.adicionarPlanta(gardenPlant);
		List<AchievementProgressDTO> unlockedList = achievementService.checkAndGrantAll(user.getId());
		return ResponseEntity.ok(ApiResponseDTO.ok("Planta adicionada com sucesso ao seu jardim!", unlockedList));
	}

	// =========================
	// LISTAR TODAS AS PLANTAS DO MEU JARDIM
	// =========================

	@GetMapping
	public ResponseEntity<List<GardenPlant>> getAllGardenPlants(Authentication authentication) {
		User user = getAuthenticatedUser(authentication);
		List<GardenPlant> plantas = gardenPlantService.listarPorUsuario(user.getId());
		return ResponseEntity.ok(plantas);
	}

	// =========================
	// RETORNAR PLANTA ÚNICA DO MEU JARDIM
	// =========================

	@GetMapping("/{id}")
	public ResponseEntity<GardenPlant> getGardenPlant(@PathVariable UUID id, Authentication authentication) {
		User user = getAuthenticatedUser(authentication);
		GardenPlant planta = gardenPlantService.buscarPorIdEUsuario(id, user.getId());
		return ResponseEntity.ok(planta);
	}

	// =========================
	// ESTATÍSTICAS DA PLANTA (CO₂ + ECOSCORE)
	// =========================

	@GetMapping("/{id}/stats")
	public ResponseEntity<PlantStatsDTO> getPlantStats(@PathVariable UUID id, Authentication authentication) {
		User user = getAuthenticatedUser(authentication);
		PlantStatsDTO stats = gardenPlantService.getPlantStats(id, user.getId());
		return ResponseEntity.ok(stats);
	}

	// =========================
	// EDITAR PLANTA DO MEU JARDIM
	// =========================

	@PutMapping("/{id}")
	public ResponseEntity<ApiResponseDTO> editGardenPlant(@PathVariable UUID id, @RequestBody GardenPlant gardenPlant,
			Authentication authentication) {
		User user = getAuthenticatedUser(authentication);
		gardenPlantService.editarPlanta(id, user.getId(), gardenPlant);
		return ResponseEntity.ok(ApiResponseDTO.ok("Planta atualizada com sucesso."));
	}

	// =========================
	// EXCLUIR PLANTA DO MEU JARDIM
	// =========================

	@DeleteMapping("/{id}")
	public ResponseEntity<ApiResponseDTO> deleteGardenPlant(@PathVariable UUID id, Authentication authentication) {
		User user = getAuthenticatedUser(authentication);
		gardenPlantService.excluirPlanta(id, user.getId());
		return ResponseEntity.ok(ApiResponseDTO.ok("Planta excluída do seu jardim com sucesso."));
	}

	// =========================
	// RETORNAR A PRÓXIMA REGA
	// =========================

	@GetMapping("/next-watering/{id}")
	public ResponseEntity<WateringReminderDTO> getNextWatering(@PathVariable UUID id, Authentication authentication) {
		User user = getAuthenticatedUser(authentication);
		WateringReminderDTO reminder = wateringService.getNextWatering(id, user.getId());
		return ResponseEntity.ok(reminder);
	}

	// =========================
	// RETORNAR AS PRÓXIMAS 4 REGAS
	// =========================
	
	@GetMapping("/next-waterings/{id}")
	public ResponseEntity<List<WateringReminderDTO>> getNextWaterings(@PathVariable UUID id, Authentication authentication) {
		User user = getAuthenticatedUser(authentication);
		List<WateringReminderDTO> reminder = wateringService.getNextWaterings(id, user.getId());
		return ResponseEntity.ok(reminder);
	}

	// =========================
	// REGAR PLANTA DO MEU JARDIM
	// =========================

	@PostMapping({"/watering/{id}", "/{id}/care/water"})
	public ResponseEntity<ApiResponseDTO> waterGardenPlant(@PathVariable UUID id, Authentication authentication) {
		User user = getAuthenticatedUser(authentication);
		wateringService.registrarRega(id, user.getId());
		List<AchievementProgressDTO> unlockedList = achievementService.checkAndGrantAll(user.getId());
		return ResponseEntity.ok(ApiResponseDTO.ok("Planta regada com sucesso!", unlockedList));
	}

	// =========================
	// ADUBAR PLANTA DO MEU JARDIM
	// =========================

	@PostMapping({"/fertilize/{id}", "/{id}/care/fertilize"})
	public ResponseEntity<ApiResponseDTO> fertilizeGardenPlant(@PathVariable UUID id, Authentication authentication) {
		User user = getAuthenticatedUser(authentication);
		gardenPlantService.adubarPlanta(id, user.getId());
		List<AchievementProgressDTO> unlockedList = achievementService.checkAndGrantAll(user.getId());
		return ResponseEntity.ok(ApiResponseDTO.ok("Planta adubada com sucesso!", unlockedList));
	}

	// =========================
	// PODAR PLANTA DO MEU JARDIM
	// =========================

	@PostMapping({"/prune/{id}", "/{id}/care/prune"})
	public ResponseEntity<ApiResponseDTO> pruneGardenPlant(@PathVariable UUID id, Authentication authentication) {
		User user = getAuthenticatedUser(authentication);
		gardenPlantService.podarPlanta(id, user.getId());
		List<AchievementProgressDTO> unlockedList = achievementService.checkAndGrantAll(user.getId());
		return ResponseEntity.ok(ApiResponseDTO.ok("Planta podada com sucesso!", unlockedList));
	}

	// =========================
	// CONSULTAR STATUS DE REGA + CLIMA
	// =========================

	@GetMapping("/watering-status/{id}")
	public ResponseEntity<?> verifyGardenPlantWateringStatus(@PathVariable UUID id, @RequestParam String cidade,
			Authentication authentication) {

		User user = getAuthenticatedUser(authentication);

		// Busca a planta garantindo pertencimento ao usuário autenticado
		GardenPlant planta = gardenPlantService.buscarPorIdEUsuario(id, user.getId());

		// Busca o clima da cidade informada
		WeatherResponseDTO clima = weatherService.getCurrentWeather(cidade);

		// Processa a recomendação com base nas regras do sistema
		String recomendacao = wateringService.analisarClima(planta, clima);

		WateringStatusDTO resposta = new WateringStatusDTO(planta.getId(), planta.getNickname(), cidade, clima.getTemperatura(),
				clima.getUmidade(), clima.isChovendo(), recomendacao);

		return ResponseEntity.ok(resposta);
	}
}