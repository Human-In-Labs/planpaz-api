package com.humanin.planpaz.service;

import java.util.Comparator;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.humanin.planpaz.config.CacheConfig;
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

	@Autowired(required = false)
	private CacheManager cacheManager;

	@Transactional
	@Caching(evict = {
			@CacheEvict(value = CacheConfig.PLANT_CATALOG_CACHE, allEntries = true),
			@CacheEvict(value = CacheConfig.PLANT_STAGES_CACHE, allEntries = true),
			@CacheEvict(value = CacheConfig.PLANT_CARE_CACHE, allEntries = true)
	})
	public PlantResponseDTO adicionarPlanta(com.humanin.planpaz.dto.PlantCreateDTO dto) {
		if (plantRepository.existsByNameIgnoreCase(dto.name())) {
			throw new BusinessException("Já existe uma planta com esse nome.");
		}
		Plant saved = plantRepository.save(dto.toEntity());
		return PlantResponseDTO.fromEntity(saved);
	}

	@Caching(evict = {
			@CacheEvict(value = CacheConfig.PLANT_CATALOG_CACHE, allEntries = true),
			@CacheEvict(value = CacheConfig.PLANT_STAGES_CACHE, allEntries = true),
			@CacheEvict(value = CacheConfig.PLANT_CARE_CACHE, allEntries = true)
	})
	public Plant adicionarPlanta(Plant planta) {
		if (plantRepository.existsByNameIgnoreCase(planta.getName())) {
			throw new BusinessException("Já existe uma planta com esse nome.");
		}

		return plantRepository.save(planta);
	}

	@Cacheable(value = CacheConfig.PLANT_CATALOG_CACHE, key = "'all'", unless = "#result == null || #result.isEmpty()")
	@SuppressWarnings("unchecked")
	public List<Plant> listarPlantas() {
		Cache cache = cacheManager != null ? cacheManager.getCache(CacheConfig.PLANT_CATALOG_CACHE) : null;
		if (cache != null) {
			List<Plant> cached = cache.get("all", List.class);
			if (cached != null && !cached.isEmpty()) {
				return cached;
			}
		}
		List<Plant> all = plantRepository.findAll();
		if (cache != null && !all.isEmpty()) {
			cache.put("all", all);
		}
		return all;
	}

	@Transactional(readOnly = true)
	public List<PlantResponseDTO> listarECalcularRecomendacoes(User userParam, String search, String typeStr,
			String temperatureStr, String luminosityStr, String wateringStr, String sizeStr) {

		User user = null;
		if (userParam != null) {
			if (userParam.getId() != null) {
				user = userRepository.findWithPreferencesById(userParam.getId()).orElse(null);
			} else if (userParam.getEmail() != null) {
				user = userRepository.findWithPreferencesByEmail(userParam.getEmail()).orElse(null);
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

		List<Plant> plantas = listarPlantas();
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

	@Caching(evict = {
			@CacheEvict(value = CacheConfig.PLANT_CATALOG_CACHE, allEntries = true),
			@CacheEvict(value = CacheConfig.PLANT_STAGES_CACHE, allEntries = true),
			@CacheEvict(value = CacheConfig.PLANT_CARE_CACHE, allEntries = true)
	})
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

	@Transactional
	@Caching(evict = {
			@CacheEvict(value = CacheConfig.PLANT_CATALOG_CACHE, allEntries = true),
			@CacheEvict(value = CacheConfig.PLANT_STAGES_CACHE, allEntries = true),
			@CacheEvict(value = CacheConfig.PLANT_CARE_CACHE, allEntries = true)
	})
	public PlantResponseDTO editarPlanta(UUID id, com.humanin.planpaz.dto.PlantUpdateDTO dto) {
		Plant novaPlanta = plantRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Planta não encontrada com ID: " + id));

		if (dto.name() != null && !dto.name().isBlank()) {
			if (plantRepository.existsByNameIgnoreCaseAndIdNot(dto.name(), id)) {
				throw new BusinessException("Já existe uma planta com esse nome.");
			}
			novaPlanta.setName(dto.name());
		}

		if (dto.scientificName() != null) novaPlanta.setScientificName(dto.scientificName());
		if (dto.description() != null) novaPlanta.setDescription(dto.description());
		if (dto.careGuide() != null) novaPlanta.setCareGuide(dto.careGuide());
		if (dto.imagePath() != null) novaPlanta.setImagePath(dto.imagePath());
		if (dto.luminosityLevel() != null) novaPlanta.setLuminosityLevel(dto.luminosityLevel());
		if (dto.size() != null) novaPlanta.setSize(dto.size());
		if (dto.temperatureLevel() != null) novaPlanta.setTemperatureLevel(dto.temperatureLevel());
		if (dto.type() != null) novaPlanta.setType(dto.type());
		if (dto.wateringLevel() != null) novaPlanta.setWateringLevel(dto.wateringLevel());

		Plant saved = plantRepository.save(novaPlanta);
		return PlantResponseDTO.fromEntity(saved);
	}

	@Caching(evict = {
			@CacheEvict(value = CacheConfig.PLANT_CATALOG_CACHE, allEntries = true),
			@CacheEvict(value = CacheConfig.PLANT_STAGES_CACHE, allEntries = true),
			@CacheEvict(value = CacheConfig.PLANT_CARE_CACHE, allEntries = true)
	})
	public void excluirPlanta(UUID idPlanta) {
		if (!plantRepository.existsById(idPlanta)) {
			throw new ResourceNotFoundException("Planta não encontrada com ID: " + idPlanta);
		}

		plantRepository.deleteById(idPlanta);
	}

	@Cacheable(value = CacheConfig.PLANT_CATALOG_CACHE, key = "'entity:' + #id", unless = "#result == null")
	public Plant buscarPorId(UUID id) {
		return plantRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Planta não encontrada com ID: " + id));
	}

	@Cacheable(value = CacheConfig.PLANT_CATALOG_CACHE, key = "'dto:' + #id", unless = "#result == null")
	public PlantResponseDTO buscarDTOPorId(UUID id) {
		return PlantResponseDTO.fromEntity(buscarPorId(id));
	}
}
