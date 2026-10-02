package com.humanin.planpaz.model.enums;

import lombok.Getter;

@Getter
public enum ReportStatus {
	PENDING("Pendente"),
	ACCEPTED("Aceita"),
	REJECTED("Rejeitada"),
	RESOLVED("Resolvida");

	private final String description;

	ReportStatus(String description) {
		this.description = description;
	}
}
