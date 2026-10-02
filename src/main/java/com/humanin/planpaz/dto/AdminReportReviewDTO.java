package com.humanin.planpaz.dto;

import com.humanin.planpaz.model.enums.ReportActionTaken;
import com.humanin.planpaz.model.enums.ReportStatus;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AdminReportReviewDTO {

	@NotNull(message = "A decisão é obrigatória (ACCEPTED ou REJECTED).")
	private ReportStatus decision;

	private ReportActionTaken actionTaken = ReportActionTaken.NONE;

	private Boolean deleteContent = false; // RF50
	private Boolean warnUser = false;      // RF51
	private Boolean banUser = false;       // RF52

	private String note;
}
