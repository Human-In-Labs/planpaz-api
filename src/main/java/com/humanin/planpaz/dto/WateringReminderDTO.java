package com.humanin.planpaz.dto;

import java.util.UUID;

public record WateringReminderDTO(
    Integer order,
    UUID gardenPlantId,
    String plantNickname,
    String plantImage,
    String date,
    String time,
    String status
) {
}
