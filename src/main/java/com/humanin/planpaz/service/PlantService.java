package com.humanin.planpaz.service;

import java.util.Comparator;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.humanin.planpaz.dto.PlantResponseDTO;
import com.humanin.planpaz.infra.exception.BusinessException;
import com.humanin.planpaz.infra.exception.ResourceNotFoundException;
import com.humanin.planpaz.model.Plant;
import com.humanin.planpaz.model.User;
import com.humanin.planpaz.model.enums.LuminosityLevel;
import com.humanin.planpaz.model.enums.Size;
import com.humanin.planpaz.model.enums.TemperatureLevel;
import com.humanin.planpaz.model.enums.Type;
import com.humanin.planpaz.model.enums.WateringLevel;
import com.humanin.planpaz.repositories.PlantRepository;
import com.humanin.planpaz.repositories.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PlantService {
	public final PlantRepository plantRepository;
	public final UserRepository userRepository;

	public Plant adicionarPlanta(Plant planta) {
		if (plantRepository.existsByNameIgnoreCase(planta.getName())) {
			throw new BusinessException("Já existe uma planta com esse nome.");
		}

		return plantRepository.save(planta);
	}

	public List<Plant> listarPlantas() {
		return plantRepository.findAll();
	}

	@Transactional(readOnly = true)
	public List<PlantResponseDTO> listarECalcularRecomendacoes(User userParam, String search, String typeStr,
			String temperatureStr, String luminosityStr, String wateringStr, String sizeStr) {

		User user = null;
		if (userParam != null) {
			if (userParam.getId() != null) {
				user = userRepository.findById(userParam.getId()).orElse(null);
			} else if (userParam.getEmail() != null) {
				user = userRepository.findByEmail(userParam.getEmail()).orElse(null);
			}
		}

		if (user != null) {
			if (user.getRoomLuminosity() != null) {
				user.getRoomLuminosity().size();
			}
			if (user.getSpaceAvailability() != null) {
				user.getSpaceAvailability().size();
			}
		}

		Type type = parseEnum(Type.class, typeStr);
		LuminosityLevel luminosity = parseEnum(LuminosityLevel.class, luminosityStr);
		WateringLevel watering = parseEnum(WateringLevel.class, wateringStr);
		Size size = parseEnum(Size.class, sizeStr);
		TemperatureLevel temperature = parseEnum(TemperatureLevel.class, temperatureStr);

		List<Plant> plantas = plantRepository.findAll();
		final User finalUser = user;

		// Verifica se o usuário tem algum perfil configurado
		boolean hasUserProfile = finalUser != null && (finalUser.getExperienceLevel() != null
				|| (finalUser.getRoomLuminosity() != null && !finalUser.getRoomLuminosity().isEmpty())
				|| finalUser.getTimeAvailability() != null
				|| (finalUser.getSpaceAvailability() != null && !finalUser.getSpaceAvailability().isEmpty()));

		return plantas.stream().filter(p -> {
			if (search != null && !search.isBlank()) {
				String q = search.trim().toLowerCase();
				boolean nameMatch = p.getName() != null && p.getName().toLowerCase().contains(q);
				boolean sciMatch = p.getScientificName() != null && p.getScientificName().toLowerCase().contains(q);
				if (!nameMatch && !sciMatch)
					return false;
			}
			if (type != null && p.getType() != type)
				return false;
			if (luminosity != null && p.getLuminosityLevel() != luminosity)
				return false;
			if (watering != null && p.getWateringLevel() != watering)
				return false;
			if (size != null && p.getSize() != size)
				return false;
			if (temperature != null && p.getTemperatureLevel() != temperature)
				return false;
			return true;
		}).map(p -> {
			int score = calcularScoreMatch(p, finalUser);
			boolean isRecommended;
			if (hasUserProfile) {
				isRecommended = (score >= 50);
			} else {
				// Fallback de recomendação inteligente para usuários sem preferências (plantas
				// fáceis/populares)
				isRecommended = (p.getWateringLevel() == WateringLevel.WEEKLY
						|| p.getWateringLevel() == WateringLevel.SPORADIC
						|| p.getLuminosityLevel() == LuminosityLevel.ANY);
			}
			return new ScoredPlant(p, score, isRecommended);
		}).sorted(Comparator.comparing(ScoredPlant::score).reversed())
				.map(sp -> new PlantResponseDTO(sp.plant().getId(), sp.plant().getName(),
						sp.plant().getScientificName(), sp.plant().getDescription(), sp.plant().getCareGuide(),
						sp.plant().getWateringLevel(), sp.plant().getLuminosityLevel(),
						sp.plant().getTemperatureLevel(), sp.plant().getSize(), sp.plant().getType(),
						sp.plant().getImagePath(), sp.isRecommended()))
				.collect(Collectors.toList());
	}

	private <T extends Enum<T>> T parseEnum(Class<T> enumClass, String value) {
		if (value == null || value.isBlank())
			return null;
		try {
			return Enum.valueOf(enumClass, value.trim().toUpperCase());
		} catch (Exception e) {
			return null;
		}
	}

	private record ScoredPlant(Plant plant, int score, boolean isRecommended) {
	}

	private int calcularScoreMatch(Plant plant, User user) {
		if (user == null)
			return 0;
		int score = 0;

		// 1. Experiência
		if (user.getExperienceLevel() != null && plant.getWateringLevel() != null) {
			String userExp = user.getExperienceLevel().name();
			if ("BEGINNER".equalsIgnoreCase(userExp)) {
				if (plant.getWateringLevel() == WateringLevel.SPORADIC
						|| plant.getWateringLevel() == WateringLevel.WEEKLY) {
					score += 30;
				}
			} else {
				score += 20;
			}
		}

		// 2. Iluminação
		if (user.getRoomLuminosity() != null && !user.getRoomLuminosity().isEmpty()
				&& plant.getLuminosityLevel() != null) {
			boolean matchLight = user.getRoomLuminosity().stream()
					.anyMatch(rl -> rl.name().equalsIgnoreCase(plant.getLuminosityLevel().name()));
			if (matchLight || plant.getLuminosityLevel() == LuminosityLevel.ANY) {
				score += 30;
			}
		}

		// 3. Tempo / Rega
		if (user.getTimeAvailability() != null && plant.getWateringLevel() != null) {
			if (user.getTimeAvailability().name().equalsIgnoreCase(plant.getWateringLevel().name())) {
				score += 30;
			} else if (user.getTimeAvailability().name().equalsIgnoreCase("SPORADIC")
					&& (plant.getWateringLevel() == WateringLevel.SPORADIC
							|| plant.getWateringLevel() == WateringLevel.WEEKLY)) {
				score += 30;
			}
		}

		// 4. Espaço
		if (user.getSpaceAvailability() != null && !user.getSpaceAvailability().isEmpty() && plant.getSize() != null) {
			boolean matchSpace = user.getSpaceAvailability().stream()
					.anyMatch(sa -> sa.name().equalsIgnoreCase(plant.getSize().name()));
			if (matchSpace) {
				score += 10;
			}
		}

		return score;
	}

	public Plant editarPlanta(Plant planta) {
		Plant novaPlanta = plantRepository.findById(planta.getId())
				.orElseThrow(() -> new ResourceNotFoundException("Planta não encontrada com ID: " + planta.getId()));

		if (plantRepository.existsByNameIgnoreCaseAndIdNot(planta.getName(), planta.getId())) {
			throw new BusinessException("Já existe uma planta com esse nome.");
		}

		novaPlanta.setName(planta.getName());
		novaPlanta.setDescription(planta.getDescription());
		novaPlanta.setCareGuide(planta.getCareGuide());
		novaPlanta.setImagePath(planta.getImagePath());
		novaPlanta.setLuminosityLevel(planta.getLuminosityLevel());
		novaPlanta.setScientificName(planta.getScientificName());
		novaPlanta.setSize(planta.getSize());
		novaPlanta.setTemperatureLevel(planta.getTemperatureLevel());
		novaPlanta.setType(planta.getType());
		novaPlanta.setWateringLevel(planta.getWateringLevel());

		return plantRepository.save(novaPlanta);
	}

	public void excluirPlanta(UUID idPlanta) {
		if (!plantRepository.existsById(idPlanta)) {
			throw new ResourceNotFoundException("Planta não encontrada com ID: " + idPlanta);
		}

		plantRepository.deleteById(idPlanta);
	}

	public Plant buscarPorId(UUID id) {
		return plantRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Planta não encontrada com ID: " + id));
	}
}