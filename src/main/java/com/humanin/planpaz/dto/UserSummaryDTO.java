package com.humanin.planpaz.dto;

import java.util.UUID;

public record UserSummaryDTO(UUID id, String name, String username, String email) {
}