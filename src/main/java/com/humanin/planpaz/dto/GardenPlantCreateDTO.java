package com.humanin.planpaz.dto;

import java.time.LocalDateTime;
import java.util.UUID;

import com.humanin.planpaz.model.enums.Room;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record GardenPlantCreateDTO(
    PlantRefDTO plant,
    UUID plantId,
    @NotBlank(message = "O apelido da planta é obrigatório.")
    @Size(max = 50, message = "O apelido deve ter no máximo 50 caracteres.")
    String nickname,
    StageRefDTO stage,
    UUID stageId,
    Room room,
    Boolean directRain,
    Boolean wateringNotification,
    LocalDateTime plantedAt,
    String imagePath
) {
    public UUID getResolvedPlantId() {
        if (plantId != null) {
            return plantId;
        }
        return plant != null ? plant.id() : null;
    }

    public UUID getResolvedStageId() {
        if (stageId != null) {
            return stageId;
        }
        return stage != null ? stage.id() : null;
    }
}
