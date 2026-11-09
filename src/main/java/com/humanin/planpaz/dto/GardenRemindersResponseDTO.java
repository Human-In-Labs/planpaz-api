package com.humanin.planpaz.dto;

import java.util.List;

public record GardenRemindersResponseDTO(
		int totalGardenPlants,
		List<WateringReminderDTO> reminders
) {}
