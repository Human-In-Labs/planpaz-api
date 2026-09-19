package com.humanin.planpaz.dto;

import java.time.LocalDateTime;
import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AchievementProgressDTO {
	private UUID id;
	private String name;
	private String description;
	private String icon;
	private boolean unlocked;
	private LocalDateTime unlockedAt;
	private int progress;
	private int maxProgress;
}
