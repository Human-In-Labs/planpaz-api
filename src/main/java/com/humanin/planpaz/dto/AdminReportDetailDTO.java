package com.humanin.planpaz.dto;

import java.util.UUID;

import com.humanin.planpaz.model.enums.ReportActionTaken;
import com.humanin.planpaz.model.enums.ReportContentType;
import com.humanin.planpaz.model.enums.ReportReason;
import com.humanin.planpaz.model.enums.ReportStatus;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdminReportDetailDTO {

	private UUID id;

	// Tipo de Conteúdo Denunciado (POST ou COMMENT)
	private ReportContentType contentType;
	private UUID contentId;

	// Detalhes do Conteúdo Denunciado (Post ou Comentário)
	private String targetTitle;
	private String targetText;
	private String targetImageUrl;
	private String postedAt; // Data/Hora em que o conteúdo foi publicado

	// Autor do Conteúdo Denunciado
	private UUID authorId;
	private String authorName;
	private String authorUsername;
	private String authorEmail;
	private String authorAvatarUrl;
	private Boolean authorBanned;
	private Boolean isDeleted;

	// Usuário Denunciante (Reporter)
	private UUID reporterId;
	private String reporterName;
	private String reporterUsername;
	private String reporterEmail;

	// Motivo e Detalhes da Denúncia
	private ReportReason reason;
	private String reasonDescription;
	private String message;

	// Status e Decisão da Moderação
	private ReportStatus status;
	private String statusDescription;
	private ReportActionTaken actionTaken;
	private String actionTakenDescription;

	// Moderador que revisou (ReviewedBy)
	private UUID reviewedById;
	private String reviewedByName;
	private String reviewedByEmail;

	private String createdAt;
	private String updatedAt;
}
