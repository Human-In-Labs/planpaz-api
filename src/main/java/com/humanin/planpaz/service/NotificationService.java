package com.humanin.planpaz.service;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.humanin.planpaz.dto.NotificationDTO;
import com.humanin.planpaz.model.Notification;
import com.humanin.planpaz.model.User;
import com.humanin.planpaz.repositories.NotificationRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationService {

	private final NotificationRepository notificationRepository;

	@Transactional(readOnly = true)
	public List<NotificationDTO> getNotificationsForUser(UUID userId) {
		return notificationRepository.findByUserIdOrderByCreatedAtDesc(userId)
				.stream()
				.map(NotificationDTO::fromEntity)
				.toList();
	}

	@Transactional(readOnly = true)
	public long countUnread(UUID userId) {
		return notificationRepository.countByUserIdAndIsReadFalse(userId);
	}

	@Transactional
	public void markAsRead(UUID userId, List<UUID> ids) {
		if (ids == null || ids.isEmpty()) return;
		notificationRepository.markAsRead(userId, ids);
	}

	@Transactional
	public void markAllAsRead(UUID userId) {
		notificationRepository.markAllAsRead(userId);
	}

	@Transactional
	public Notification createNotification(User user, String title, String description, String type) {
		Notification notification = new Notification();
		notification.setUser(user);
		notification.setTitle(title);
		notification.setDescription(description);
		notification.setType(type != null ? type : "SYSTEM");
		notification.setIsRead(false);
		
		Notification saved = notificationRepository.save(notification);
		log.info("Notificação in-app criada para usuário {} (ID: {})", user.getUsername(), saved.getId());
		return saved;
	}
}
