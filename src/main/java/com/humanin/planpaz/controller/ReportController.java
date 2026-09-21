package com.humanin.planpaz.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.humanin.planpaz.dto.ReportCreateDTO;
import com.humanin.planpaz.dto.ReportResponseDTO;
import com.humanin.planpaz.model.User;
import com.humanin.planpaz.service.ReportService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/reports")
@RequiredArgsConstructor
public class ReportController {

	private final ReportService reportService;

	@PostMapping
	public ResponseEntity<ReportResponseDTO> createReport(@Valid @RequestBody ReportCreateDTO dto,
			Authentication authentication) {
		User authenticatedUser = null;
		if (authentication != null && authentication.getPrincipal() instanceof User user) {
			authenticatedUser = user;
		}

		ReportResponseDTO response = reportService.createReport(dto, authenticatedUser);
		return ResponseEntity.status(HttpStatus.CREATED).body(response);
	}
}
