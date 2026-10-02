package com.humanin.planpaz.dto;

import java.time.LocalDateTime;
import java.util.UUID;

import com.humanin.planpaz.model.Notification;

public record NotificationDTO(
		UUID id,
		String title,
		String description,
		String type,
		boolean isRead,
		LocalDateTime createdAt
) {
	public static NotificationDTO fromEntity(Notification entity) {
		return new NotificationDTO(
				entity.getId(),
				entity.getTitle(),
				entity.getDescription(),
				entity.getType(),
				Boolean.TRUE.equals(entity.getIsRead()),
				entity.getCreatedAt()
		);
	}
}
