package com.humanin.planpaz.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.UUID;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.humanin.planpaz.config.CacheConfig;
import com.humanin.planpaz.dto.AchievementProgressDTO;
import com.humanin.planpaz.dto.PublicUserProfileDTO;
import com.humanin.planpaz.dto.RecentActivityDTO;
import com.humanin.planpaz.dto.UserPreferencesDTO;
import com.humanin.planpaz.dto.UserSettingsDTO;
import com.humanin.planpaz.dto.UserStatsDTO;
import com.humanin.planpaz.dto.UserSummaryDTO;
import com.humanin.planpaz.infra.exception.BusinessException;
import com.humanin.planpaz.infra.exception.ResourceNotFoundException;
import com.humanin.planpaz.model.Follow;
import com.humanin.planpaz.model.GardenPlant;
import com.humanin.planpaz.model.User;
import com.humanin.planpaz.repositories.FollowRepository;
import com.humanin.planpaz.repositories.GardenPlantRepository;
import com.humanin.planpaz.repositories.PostRepository;
import com.humanin.planpaz.repositories.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserService {

	private final UserRepository userRepository;
	private final FollowRepository followersRepository;
	private final GardenPlantRepository gardenPlantRepository;
	private final PostRepository postRepository;
	private final AchievementService achievementService;
	private final FirebasePushNotificationService firebasePushNotificationService;
	private final StreakService streakService;

	@Transactional
	public UserStatsDTO getUserStats(UUID targetUserId) {
		User targetUser = findUserById(targetUserId);

		List<GardenPlant> gardenPlants = gardenPlantRepository.findByOwnerId(targetUserId);
		long totalPlants = gardenPlants.size();
		long totalPosts = postRepository.countByAuthorId(targetUserId);
		long followersCount = followersRepository.countByFollowed(targetUser);
		long followingCount = followersRepository.countByFollower(targetUser);

		long daysOnApp = 0;
		if (targetUser.getCreatedAt() != null) {
			daysOnApp = Math.max(0, ChronoUnit.DAYS.between(targetUser.getCreatedAt(), LocalDateTime.now()));
		}

		double totalCo2Grams = 0.0;
		for (GardenPlant planta : gardenPlants) {
			long cultivationDays = 0;
			if (planta.getPlantedAt() != null) {
				cultivationDays = Math.max(0,
						ChronoUnit.DAYS.between(planta.getPlantedAt().toLocalDate(), LocalDate.now()));
			}
			double co2PerYear = (planta.getPlant() != null
					&& planta.getPlant().getSize() == com.humanin.planpaz.model.enums.Size.LARGE) ? 1300.0 : 50.0;
			totalCo2Grams += cultivationDays * (co2PerYear / 365.0);
		}
		totalCo2Grams = java.math.BigDecimal.valueOf(totalCo2Grams).setScale(1, java.math.RoundingMode.HALF_UP)
				.doubleValue();

		int totalEcoScore = targetUser.getEcoscore() != null ? targetUser.getEcoscore() : 0;
		int streakCount = streakService.getActiveStreak(targetUser);

		// Sincroniza persistência caso a ofensiva tenha quebrado por inatividade
		if (targetUser.getStreakDays() != null && targetUser.getStreakDays() > 0 && streakCount == 0) {
			targetUser.setStreakDays(0);
			userRepository.save(targetUser);
		}

		return new UserStatsDTO(targetUser.getId(), totalCo2Grams, totalEcoScore, totalPlants,
				totalPosts, daysOnApp, streakCount, followersCount, followingCount);
	}

	@Transactional
	public PublicUserProfileDTO getPublicUserProfile(User currentUser, UUID targetUserId) {
		User targetUser = findUserById(targetUserId);

		boolean isFollowing = currentUser != null
				&& followersRepository.existsByFollowerAndFollowed(currentUser, targetUser);

		UserStatsDTO stats = getUserStats(targetUserId);

		List<AchievementProgressDTO> achievements = achievementService.obterConquistasProgressoDoUsuario(targetUserId);

		return new PublicUserProfileDTO(targetUser.getId(), targetUser.getName(), targetUser.getUsername(),
				targetUser.getEmail(), targetUser.getBio(), targetUser.getAvatarUrl(), stats.followersCount(), stats.followingCount(),
				isFollowing, stats.totalPlants(), stats.totalPosts(), stats.daysOnApp(),
				Math.round(stats.totalCo2Grams()), stats.totalEcoScore(), stats.streakCount(), achievements);
	}

	@Transactional(readOnly = true)
	public UserSettingsDTO getUserSettings(UUID userId) {
		User user = userRepository.findWithPreferencesById(userId)
				.orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado com ID: " + userId));
		if (user.getRoomLuminosity() != null) {
			user.getRoomLuminosity().size();
		}
		if (user.getSpaceAvailability() != null) {
			user.getSpaceAvailability().size();
		}
		return UserSettingsDTO.fromEntity(user);
	}

	@Transactional
	@CacheEvict(value = CacheConfig.AUTH_PRINCIPAL_CACHE, allEntries = true)
	public UserSettingsDTO updatePreferences(UUID userId, UserPreferencesDTO preferences) {
		User user = userRepository.findWithPreferencesById(userId)
				.orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado com ID: " + userId));

		if (preferences.bio() != null)
			user.setBio(preferences.bio());
		if (preferences.birthdate() != null)
			user.setBirthdate(preferences.birthdate());
		if (preferences.gender() != null)
			user.setGender(preferences.gender());
		if (preferences.mainGoal() != null)
			user.setMainGoal(preferences.mainGoal());
		if (preferences.roomLuminosity() != null)
			user.setRoomLuminosity(preferences.roomLuminosity());
		if (preferences.spaceAvailability() != null)
			user.setSpaceAvailability(preferences.spaceAvailability());
		if (preferences.experienceLevel() != null)
			user.setExperienceLevel(preferences.experienceLevel());
		if (preferences.timeAvailability() != null)
			user.setTimeAvailability(preferences.timeAvailability());
		if (preferences.wateringTime() != null)
			user.setWateringTime(preferences.wateringTime());
		if (preferences.cityName() != null)
			user.setCityName(preferences.cityName());
		if (preferences.latitude() != null)
			user.setLatitude(preferences.latitude());
		if (preferences.longitude() != null)
			user.setLongitude(preferences.longitude());
		if (preferences.fcmToken() != null)
			user.setFcmToken(preferences.fcmToken().isBlank() ? null : preferences.fcmToken().trim());

		User saved = userRepository.save(user);
		if (saved.getRoomLuminosity() != null) {
			saved.getRoomLuminosity().size();
		}
		if (saved.getSpaceAvailability() != null) {
			saved.getSpaceAvailability().size();
		}
		return UserSettingsDTO.fromEntity(saved);
	}

	@Transactional
	@CacheEvict(value = CacheConfig.AUTH_PRINCIPAL_CACHE, allEntries = true)
	public UserSettingsDTO updateSettings(UUID userId, UserSettingsDTO settings) {
		User user = userRepository.findWithPreferencesById(userId)
				.orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado com ID: " + userId));

		if (settings.email() != null && !settings.email().equalsIgnoreCase(user.getEmail())) {
			if (userRepository.existsByEmail(settings.email())) {
				throw new BusinessException("Já existe um usuário cadastrado com este e-mail.");
			}
			user.setEmail(settings.email().trim().toLowerCase());
		}

		if (settings.username() != null && !settings.username().equalsIgnoreCase(user.getUsername())) {
			if (userRepository.existsByUsername(settings.username())) {
				throw new BusinessException("Já existe um usuário cadastrado com este nome de usuário.");
			}
			user.setUsername(settings.username().trim());
		}

		if (settings.name() != null && !settings.name().isBlank()) {
			user.setName(settings.name().trim());
		}

		if (settings.bio() != null)
			user.setBio(settings.bio());
		if (settings.birthdate() != null)
			user.setBirthdate(settings.birthdate());
		if (settings.gender() != null)
			user.setGender(settings.gender());
		if (settings.mainGoal() != null)
			user.setMainGoal(settings.mainGoal());
		if (settings.roomLuminosity() != null)
			user.setRoomLuminosity(settings.roomLuminosity());
		if (settings.spaceAvailability() != null)
			user.setSpaceAvailability(settings.spaceAvailability());
		if (settings.experienceLevel() != null)
			user.setExperienceLevel(settings.experienceLevel());
		if (settings.timeAvailability() != null)
			user.setTimeAvailability(settings.timeAvailability());
		if (settings.wateringTime() != null)
			user.setWateringTime(settings.wateringTime());
		if (settings.cityName() != null)
			user.setCityName(settings.cityName());
		if (settings.latitude() != null)
			user.setLatitude(settings.latitude());
		if (settings.longitude() != null)
			user.setLongitude(settings.longitude());
		if (settings.fcmToken() != null)
			user.setFcmToken(settings.fcmToken().isBlank() ? null : settings.fcmToken().trim());
		if (settings.avatarUrl() != null)
			user.setAvatarUrl(settings.avatarUrl());

		User saved = userRepository.save(user);
		if (saved.getRoomLuminosity() != null) {
			saved.getRoomLuminosity().size();
		}
		if (saved.getSpaceAvailability() != null) {
			saved.getSpaceAvailability().size();
		}
		return UserSettingsDTO.fromEntity(saved);
	}

	@Transactional(readOnly = true)
	public boolean existsByUsername(String username) {
		if (username == null || username.isBlank())
			return false;
		String clean = username.replace("@", "").trim();
		return userRepository.existsByUsername(clean);
	}

	public List<UserSummaryDTO> searchByUsername(String query) {
		String cleanQuery = query.replace("@", "");
		return userRepository.findByUsernameContainingIgnoreCase(cleanQuery).stream()
				.map(u -> new UserSummaryDTO(u.getId(), u.getName(), u.getUsername(), u.getEmail(), u.getAvatarUrl())).toList();
	}

	@Transactional
	public void followUser(User currentUser, UUID targetUserId) {
		if (currentUser.getId().equals(targetUserId)) {
			throw new BusinessException("Você não pode seguir a si mesmo.");
		}

		User targetUser = findUserById(targetUserId);

		if (!followersRepository.existsByFollowerAndFollowed(currentUser, targetUser)) {
			Follow relation = new Follow(currentUser, targetUser);
			followersRepository.save(relation);

			firebasePushNotificationService.sendPushNotification(
					targetUser,
					"Novo seguidor! 🌿",
					currentUser.getName() + " (@" + currentUser.getUsername() + ") começou a seguir você.",
					"NEW_FOLLOWER"
			);
		}
	}

	@Transactional
	public void unfollowUser(User currentUser, UUID targetUserId) {
		User targetUser = findUserById(targetUserId);
		followersRepository.deleteByFollowerAndFollowed(currentUser, targetUser);
	}

	@Transactional(readOnly = true)
	public List<UserSummaryDTO> getFollowers(UUID userId) {
		User user = findUserById(userId);

		return followersRepository.findByFollowed(user).stream().map(f -> new UserSummaryDTO(f.getFollower().getId(),
				f.getFollower().getName(), f.getFollower().getUsername(), f.getFollower().getEmail(), f.getFollower().getAvatarUrl())).toList();
	}

	@Transactional(readOnly = true)
	public List<UserSummaryDTO> getFollowing(UUID userId) {
		User user = findUserById(userId);

		return followersRepository.findByFollower(user).stream().map(f -> new UserSummaryDTO(f.getFollowed().getId(),
				f.getFollowed().getName(), f.getFollowed().getUsername(), f.getFollowed().getEmail(), f.getFollowed().getAvatarUrl())).toList();
	}

	@Transactional
	public void removeFollower(User currentUser, UUID followerId) {
		User followerUser = findUserById(followerId);
		followersRepository.deleteByFollowerAndFollowed(followerUser, currentUser);
	}

	@Transactional(readOnly = true)
	public List<RecentActivityDTO> getFollowingActivities(UUID currentUserId) {
		List<GardenPlant> recentPlants = gardenPlantRepository.findRecentByFollowedUsers(
				currentUserId,
				PageRequest.of(0, 10)
		);

		return recentPlants.stream().map(p -> {
			User targetUser = p.getOwner();
			String speciesName = (p.getPlant() != null && p.getPlant().getName() != null)
					? p.getPlant().getName()
					: p.getNickname();
			String text = "Cultivando " + (p.getNickname() != null ? p.getNickname() : speciesName);
			String img = p.getImagePath() != null
					? p.getImagePath()
					: (p.getPlant() != null ? p.getPlant().getImagePath() : null);
			LocalDateTime date = p.getPlantedAt() != null ? p.getPlantedAt() : LocalDateTime.now();

			return new RecentActivityDTO(
					"plant-" + p.getId(),
					targetUser.getId(),
					targetUser.getName(),
					targetUser.getUsername(),
					targetUser.getAvatarUrl(),
					"NEW_PLANT",
					text,
					"Plantado recentemente",
					img,
					date
			);
		}).toList();
	}

	private User findUserById(UUID id) {
		return userRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado com ID: " + id));
	}
}
