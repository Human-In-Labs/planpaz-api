package com.humanin.planpaz.dto;

import java.time.LocalDateTime;
import java.util.UUID;

public record RecentActivityDTO(
		String id,
		UUID userId,
		String userName,
		String userUsername,
		String userAvatarUrl,
		String activityType,
		String activityText,
		String timeText,
		String imageUrl,
		LocalDateTime createdAt
) {}
