package com.humanin.planpaz.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CommentCreateDTO {

	@NotNull(message = "O ID do autor é obrigatório")
	private UUID authorId;

	@NotBlank(message = "O conteúdo do comentário não pode estar vazio")
	private String content;

	private UUID parentCommentId; // Nulo para comentários principais; preenchido ao responder outro comentário
}