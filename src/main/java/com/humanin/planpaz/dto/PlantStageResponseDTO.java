package com.humanin.planpaz.dto;

import java.util.UUID;
import com.humanin.planpaz.model.PlantStage;

public record PlantStageResponseDTO(
    UUID id,
    String name,
    Integer order,
    Integer days,
    String description,
    String imagePath
) {
    public static PlantStageResponseDTO fromEntity(PlantStage stage) {
        if (stage == null) {
            return null;
        }
        return new PlantStageResponseDTO(
            stage.getId(),
            stage.getName(),
            stage.getOrder(),
            stage.getDays(),
            stage.getDescription(),
            stage.getImagePath()
        );
    }
}
