package com.humanin.planpaz.service;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.humanin.planpaz.dto.UserSummaryDTO;
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

	public List<UserSummaryDTO> searchByUsername(String query) {
		String cleanQuery = query.replace("@", "");
		return userRepository.findByUsernameContainingIgnoreCase(cleanQuery).stream()
				.map(u -> new UserSummaryDTO(u.getId(), u.getName(), u.getUsername(), u.getEmail())).toList();
	}

	@Transactional
	public void followUser(User currentUser, UUID targetUserId) {
		if (currentUser.getId().equals(targetUserId)) {
			throw new IllegalArgumentException("Você não pode seguir a si mesmo.");
		}

		User targetUser = userRepository.findById(targetUserId)
				.orElseThrow(() -> new RuntimeException("Usuário não encontrado."));

		if (!followersRepository.existsByFollowerAndFollowed(currentUser, targetUser)) {
			Followers relation = new Followers(currentUser, targetUser);
			followersRepository.save(relation);
		}
	}

	@Transactional
	public void unfollowUser(User currentUser, UUID targetUserId) {
		User targetUser = userRepository.findById(targetUserId)
				.orElseThrow(() -> new RuntimeException("Usuário não encontrado."));

		followersRepository.deleteByFollowerAndFollowed(currentUser, targetUser);
	}

	public List<UserSummaryDTO> getFollowers(UUID userId) {
		User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("Usuário não encontrado."));

		return followersRepository.findByFollowed(user).stream().map(f -> new UserSummaryDTO(f.getFollower().getId(),
				f.getFollower().getName(), f.getFollower().getUsername(), f.getFollower().getEmail())).toList();
	}

	public List<UserSummaryDTO> getFollowing(UUID userId) {
		User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("Usuário não encontrado."));

		return followersRepository.findByFollower(user).stream().map(f -> new UserSummaryDTO(f.getFollowed().getId(),
				f.getFollowed().getName(), f.getFollowed().getUsername(), f.getFollowed().getEmail())).toList();
	}

	@Transactional
	public void removeFollower(User currentUser, UUID followerId) {
		User followerUser = userRepository.findById(followerId)
				.orElseThrow(() -> new RuntimeException("Usuário não encontrado."));

		followersRepository.deleteByFollowerAndFollowed(followerUser, currentUser);
	}
}