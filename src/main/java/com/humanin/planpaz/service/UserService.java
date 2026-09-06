package com.humanin.planpaz.service;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.humanin.planpaz.dto.UserPreferencesDTO;
import com.humanin.planpaz.dto.UserSettingsDTO;
import com.humanin.planpaz.dto.UserSummaryDTO;
import com.humanin.planpaz.infra.exception.BusinessException;
import com.humanin.planpaz.infra.exception.ResourceNotFoundException;
import com.humanin.planpaz.model.Followers;
import com.humanin.planpaz.model.User;
import com.humanin.planpaz.repositories.FollowersRepository;
import com.humanin.planpaz.repositories.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserService {

	private final UserRepository userRepository;
	private final FollowersRepository followersRepository;

	@Transactional(readOnly = true)
	public UserSettingsDTO getUserSettings(UUID userId) {
		User user = findUserById(userId);
		return UserSettingsDTO.fromEntity(user);
	}

	@Transactional
	public UserSettingsDTO updatePreferences(UUID userId, UserPreferencesDTO preferences) {
		User user = findUserById(userId);

		if (preferences.bio() != null) user.setBio(preferences.bio());
		if (preferences.birthdate() != null) user.setBirthdate(preferences.birthdate());
		if (preferences.gender() != null) user.setGender(preferences.gender());
		if (preferences.mainGoal() != null) user.setMainGoal(preferences.mainGoal());
		if (preferences.roomLuminosity() != null) user.setRoomLuminosity(preferences.roomLuminosity());
		if (preferences.spaceAvailability() != null) user.setSpaceAvailability(preferences.spaceAvailability());
		if (preferences.experienceLevel() != null) user.setExperienceLevel(preferences.experienceLevel());
		if (preferences.timeAvailability() != null) user.setTimeAvailability(preferences.timeAvailability());
		if (preferences.wateringTime() != null) user.setWateringTime(preferences.wateringTime());
		if (preferences.city() != null) user.setCity(preferences.city());
		if (preferences.fcmToken() != null) user.setFcmToken(preferences.fcmToken());

		User saved = userRepository.save(user);
		return UserSettingsDTO.fromEntity(saved);
	}

	@Transactional
	public UserSettingsDTO updateSettings(UUID userId, UserSettingsDTO settings) {
		User user = findUserById(userId);

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

		if (settings.bio() != null) user.setBio(settings.bio());
		if (settings.birthdate() != null) user.setBirthdate(settings.birthdate());
		if (settings.gender() != null) user.setGender(settings.gender());
		if (settings.mainGoal() != null) user.setMainGoal(settings.mainGoal());
		if (settings.roomLuminosity() != null) user.setRoomLuminosity(settings.roomLuminosity());
		if (settings.spaceAvailability() != null) user.setSpaceAvailability(settings.spaceAvailability());
		if (settings.experienceLevel() != null) user.setExperienceLevel(settings.experienceLevel());
		if (settings.timeAvailability() != null) user.setTimeAvailability(settings.timeAvailability());
		if (settings.wateringTime() != null) user.setWateringTime(settings.wateringTime());
		if (settings.city() != null) user.setCity(settings.city());
		if (settings.fcmToken() != null) user.setFcmToken(settings.fcmToken());

		User saved = userRepository.save(user);
		return UserSettingsDTO.fromEntity(saved);
	}

	public List<UserSummaryDTO> searchByUsername(String query) {
		String cleanQuery = query.replace("@", "");
		return userRepository.findByUsernameContainingIgnoreCase(cleanQuery).stream()
				.map(u -> new UserSummaryDTO(u.getId(), u.getName(), u.getUsername(), u.getEmail())).toList();
	}

	@Transactional
	public void followUser(User currentUser, UUID targetUserId) {
		if (currentUser.getId().equals(targetUserId)) {
			throw new BusinessException("Você não pode seguir a si mesmo.");
		}

		User targetUser = findUserById(targetUserId);

		if (!followersRepository.existsByFollowerAndFollowed(currentUser, targetUser)) {
			Followers relation = new Followers(currentUser, targetUser);
			followersRepository.save(relation);
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
				f.getFollower().getName(), f.getFollower().getUsername(), f.getFollower().getEmail())).toList();
	}

	@Transactional(readOnly = true)
	public List<UserSummaryDTO> getFollowing(UUID userId) {
		User user = findUserById(userId);

		return followersRepository.findByFollower(user).stream().map(f -> new UserSummaryDTO(f.getFollowed().getId(),
				f.getFollowed().getName(), f.getFollowed().getUsername(), f.getFollowed().getEmail())).toList();
	}

	@Transactional
	public void removeFollower(User currentUser, UUID followerId) {
		User followerUser = findUserById(followerId);
		followersRepository.deleteByFollowerAndFollowed(followerUser, currentUser);
	}

	private User findUserById(UUID id) {
		return userRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado com ID: " + id));
	}
}