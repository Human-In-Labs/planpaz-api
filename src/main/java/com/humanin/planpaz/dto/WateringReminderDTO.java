package com.humanin.planpaz.dto;

import java.util.UUID;

public record WateringReminderDTO(
    Integer order,
    UUID gardenPlantId,
    String plantNickname,
    String plantImage,
    String date,
    String time,
    String status,
    Boolean isOptional,
    String message
) {
    public WateringReminderDTO(
        Integer order,
        UUID gardenPlantId,
        String plantNickname,
        String plantImage,
        String date,
        String time,
        String status
    ) {
        this(order, gardenPlantId, plantNickname, plantImage, date, time, status, false, null);
    }
}
