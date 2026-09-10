package com.humanin.planpaz.dto;

import java.util.UUID;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PostCreateDTO {

	@NotNull(message = "O ID do autor é obrigatório")
	private UUID authorId;

	@NotBlank(message = "O título é obrigatório")
	@Size(max = 100, message = "O título deve ter no máximo 100 caracteres")
	private String title;

	private String content;

	private String media;
}