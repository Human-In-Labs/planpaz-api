package com.humanin.planpaz.dto;

import java.time.LocalDateTime;
import java.util.UUID;
import com.humanin.planpaz.model.UserAchievement;

public record UserAchievementResponseDTO(
    UUID id,
    AchievementResponseDTO achievement,
    LocalDateTime unlockedAt,
    String description
) {
    public static UserAchievementResponseDTO fromEntity(UserAchievement ua) {
        if (ua == null) {
            return null;
        }
        return new UserAchievementResponseDTO(
            ua.getId(),
            ua.getAchievement() != null ? AchievementResponseDTO.fromEntity(ua.getAchievement()) : null,
            ua.getUnlockedAt(),
            ua.getDescription()
        );
    }
}
