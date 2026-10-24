package com.humanin.planpaz.dto;

import java.time.LocalDate;
import java.util.UUID;

import com.humanin.planpaz.model.enums.Room;

import jakarta.validation.constraints.Size;

public record GardenPlantUpdateDTO(
    @Size(max = 50, message = "O apelido deve ter no máximo 50 caracteres.")
    String nickname,
    StageRefDTO stage,
    UUID stageId,
    Room room,
    Boolean directRain,
    Boolean wateringNotification,
    LocalDate lastWatering,
    String imagePath
) {
    public UUID getResolvedStageId() {
        if (stageId != null) {
            return stageId;
        }
        return stage != null ? stage.id() : null;
    }
}
