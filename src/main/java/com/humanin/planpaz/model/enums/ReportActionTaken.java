package com.humanin.planpaz.model.enums;

import lombok.Getter;

@Getter
public enum ReportActionTaken {
	NONE("Nenhuma ação"),
	DELETE_CONTENT("Excluir conteúdo (RF50)"),
	WARN_USER("Notificar/Advertir usuário (RF51)"),
	BAN_USER("Banir usuário (RF52)");

	private final String description;

	ReportActionTaken(String description) {
		this.description = description;
	}
}
