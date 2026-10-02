package com.humanin.planpaz.controller;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.humanin.planpaz.dto.NotificationDTO;
import com.humanin.planpaz.model.User;
import com.humanin.planpaz.service.NotificationService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
public class NotificationController {

	private final NotificationService notificationService;

	@GetMapping
	public ResponseEntity<List<NotificationDTO>> getMyNotifications(@AuthenticationPrincipal User user) {
		List<NotificationDTO> list = notificationService.getNotificationsForUser(user.getId());
		return ResponseEntity.ok(list);
	}

	@GetMapping("/unread-count")
	public ResponseEntity<Map<String, Long>> getUnreadCount(@AuthenticationPrincipal User user) {
		long count = notificationService.countUnread(user.getId());
		return ResponseEntity.ok(Map.of("unreadCount", count));
	}

	@PutMapping("/read")
	public ResponseEntity<Map<String, String>> markAsRead(
			@AuthenticationPrincipal User user,
			@RequestBody Map<String, List<UUID>> payload
	) {
		List<UUID> ids = payload.get("ids");
		notificationService.markAsRead(user.getId(), ids);
		return ResponseEntity.ok(Map.of("message", "Notificações marcadas como lidas com sucesso."));
	}

	@PutMapping("/read-all")
	public ResponseEntity<Map<String, String>> markAllAsRead(@AuthenticationPrincipal User user) {
		notificationService.markAllAsRead(user.getId());
		return ResponseEntity.ok(Map.of("message", "Todas as notificações foram marcadas como lidas."));
	}
}
