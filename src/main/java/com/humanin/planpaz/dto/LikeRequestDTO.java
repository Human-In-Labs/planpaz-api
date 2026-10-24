package com.humanin.planpaz.dto;

import java.util.UUID;
import jakarta.validation.constraints.NotNull;

public record LikeRequestDTO(
    @NotNull(message = "O campo 'authorId' é obrigatório no corpo da requisição.")
    UUID authorId
) {}
