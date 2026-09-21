package com.humanin.planpaz.dto;

import java.util.UUID;

import com.humanin.planpaz.model.enums.ReportContentType;
import com.humanin.planpaz.model.enums.ReportReason;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReportCreateDTO {

	@NotNull(message = "O tipo de conteúdo é obrigatório (POST ou COMMENT).")
	private ReportContentType contentType;

	@NotNull(message = "O ID do conteúdo denunciado é obrigatório.")
	private UUID contentId;

	@NotNull(message = "O motivo/tipo de denúncia é obrigatório.")
	private ReportReason reason;

	private String message;

	private UUID reporterId;
}
