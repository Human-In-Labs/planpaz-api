package com.humanin.planpaz.service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.humanin.planpaz.model.GardenPlant;
import com.humanin.planpaz.repositories.GardenPlantRepository;

@Service
public class GardenPlantService {

	@Autowired
	private GardenPlantRepository gardenPlantRepository;

	@Autowired
	private EmailService emailService;

	// email
	public String verificarEEnviarStatusRega(UUID gardenPlantId, String cidade) {
		// 1. Buscar a planta cadastrada na horta do usuário
		GardenPlant gardenPlant = gardenPlantRepository.findById(gardenPlantId)
				.orElseThrow(() -> new RuntimeException("Planta da horta não encontrada com o ID: " + gardenPlantId));

		// 2. Lógica para determinar se precisa regar (consulta à API de clima/status)
		String statusRega = calcularStatusRega(gardenPlant, cidade);

		// 3. Pegar o e-mail do dono da horta
		String emailUsuario = gardenPlant.getOwner().getEmail();
		String nomePlanta = gardenPlant.getNickName();

		// 4. Disparar o e-mail
		emailService.enviarAlertaRega(emailUsuario, nomePlanta, statusRega);

		return statusRega;
	}

	private String calcularStatusRega(GardenPlant gardenPlant, String cidade) {
		// Sua regra de negócio para calcular a rega com base no clima e última rega
		return "Sua planta precisa ser regada hoje por conta do clima seco em " + cidade + "!";
	}

	// ADICIONAR

	public boolean adicionarPlanta(GardenPlant gardenPlant) {

		gardenPlant.setPlantedAt(LocalDate.now());
		gardenPlant.setLastWatering(LocalDate.now());

		if (gardenPlantRepository.existsByOwnerIdAndNickNameIgnoreCase(gardenPlant.getOwner().getId(),
				gardenPlant.getNickName())) {

			return false;
		}

		gardenPlantRepository.save(gardenPlant);

		return true;
	}

	// LISTAR

	public List<GardenPlant> listarPorUsuario(UUID ownerId) {

		return gardenPlantRepository.findByOwnerId(ownerId);
	}

	// BUSCAR POR ID E USUÁRIO

	public GardenPlant buscarPorIdEUsuario(UUID id, UUID ownerId) {
		return gardenPlantRepository.findByIdAndOwnerId(id, ownerId).orElse(null);
	}

	// EDITAR

	public boolean editar(UUID id, UUID ownerId, GardenPlant gardenPlant) {

		Optional<GardenPlant> optional = gardenPlantRepository.findById(id);

		if (optional.isEmpty()) {
			return false;
		}

		GardenPlant planta = optional.get();

		// Impede editar planta de outro usuário
		if (!planta.getOwner().getId().equals(ownerId)) {
			return false;
		}

		// Verifica apelido duplicado
		if (gardenPlantRepository.existsByOwnerIdAndNickNameIgnoreCaseAndIdNot(ownerId, gardenPlant.getNickName(),
				id)) {

			return false;
		}

		planta.setNickName(gardenPlant.getNickName());
		planta.setStage(gardenPlant.getStage());

		if (gardenPlant.getLastWatering() != null) {
			planta.setLastWatering(gardenPlant.getLastWatering());
		}

		gardenPlantRepository.save(planta);

		return true;
	}

	// EXCLUIR

	public boolean excluirPlanta(UUID id, UUID ownerId) {

		Optional<GardenPlant> optional = gardenPlantRepository.findById(id);

		if (optional.isEmpty()) {
			return false;
		}

		GardenPlant planta = optional.get();

		// Não permite excluir planta de outro usuário
		if (!planta.getOwner().getId().equals(ownerId)) {
			return false;
		}

		gardenPlantRepository.delete(planta);

		return true;
	}

	// REGAR

	public boolean registrarRega(UUID id, UUID ownerId) {

		Optional<GardenPlant> optional = gardenPlantRepository.findById(id);

		if (optional.isEmpty()) {
			return false;
		}

		GardenPlant planta = optional.get();

		// Não permite regar planta de outro usuário
		if (!planta.getOwner().getId().equals(ownerId)) {
			return false;
		}

		planta.setLastWatering(LocalDate.now());

		gardenPlantRepository.save(planta);

		return true;
	}
}