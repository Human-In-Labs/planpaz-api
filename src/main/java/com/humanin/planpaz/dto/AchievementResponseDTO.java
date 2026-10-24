package com.humanin.planpaz.dto;

import java.util.UUID;
import com.humanin.planpaz.model.Achievement;

public record AchievementResponseDTO(
    UUID id,
    String name,
    String description
) {
    public static AchievementResponseDTO fromEntity(Achievement a) {
        if (a == null) {
            return null;
        }
        return new AchievementResponseDTO(a.getId(), a.getName(), a.getDescription());
    }
}
