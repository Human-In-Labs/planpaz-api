package com.humanin.planpaz.model.enums;

import lombok.Getter;

@Getter
public enum ReportReason {
	HATE_SPEECH("Discurso de ódio"), UNAUTHORIZED_DISCLOSURE("Divulgação indevida"), SPAM("Spam"),
	INAPPROPRIATE_CONTENT("Conteúdo inapropriado");

	private final String description;

	ReportReason(String description) {
		this.description = description;
	}
}
