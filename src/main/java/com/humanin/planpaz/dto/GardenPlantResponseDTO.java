package com.humanin.planpaz.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

import com.humanin.planpaz.model.GardenPlant;
import com.humanin.planpaz.model.enums.Room;

public record GardenPlantResponseDTO(
    UUID id,
    PlantResponseDTO plant,
    String nickname,
    LocalDateTime plantedAt,
    LocalDate lastWatering,
    LocalDate lastFertilizing,
    LocalDate lastPruning,
    Integer streakDays,
    Integer ecoscore,
    Boolean wateringNotification,
    PlantStageResponseDTO stage,
    Boolean directRain,
    Room room,
    String imagePath
) {
    public static GardenPlantResponseDTO fromEntity(GardenPlant gp) {
        if (gp == null) {
            return null;
        }
        return new GardenPlantResponseDTO(
            gp.getId(),
            gp.getPlant() != null ? PlantResponseDTO.fromEntity(gp.getPlant()) : null,
            gp.getNickname(),
            gp.getPlantedAt(),
            gp.getLastWatering(),
            gp.getLastFertilizing(),
            gp.getLastPruning(),
            gp.getStreakDays(),
            gp.getEcoscore(),
            gp.getWateringNotification(),
            gp.getStage() != null ? PlantStageResponseDTO.fromEntity(gp.getStage()) : null,
            gp.getDirectRain(),
            gp.getRoom(),
            gp.getImagePath()
        );
    }
}
