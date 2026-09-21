package com.humanin.planpaz.dto;

import java.time.LocalDateTime;
import java.util.UUID;

import com.humanin.planpaz.model.Report;
import com.humanin.planpaz.model.enums.ReportContentType;
import com.humanin.planpaz.model.enums.ReportReason;
import com.humanin.planpaz.model.enums.ReportStatus;

public record ReportResponseDTO(UUID id, ReportContentType contentType, UUID contentId, UUID reporterId,
		String reporterUsername, ReportReason reason, String reasonDescription, String message, ReportStatus status,
		LocalDateTime createdAt) {
	public static ReportResponseDTO fromEntity(Report report) {
		return new ReportResponseDTO(report.getId(), report.getContentType(), report.getContentId(),
				report.getReporter() != null ? report.getReporter().getId() : null,
				report.getReporter() != null ? report.getReporter().getUsername() : null, report.getReason(),
				report.getReason() != null ? report.getReason().getDescription() : null, report.getMessage(),
				report.getStatus(), report.getCreatedAt());
	}
}
