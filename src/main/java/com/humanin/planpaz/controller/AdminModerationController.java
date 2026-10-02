package com.humanin.planpaz.controller;

import java.util.List;
import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.humanin.planpaz.dto.AdminLoginRequestDTO;
import com.humanin.planpaz.dto.AdminLoginResponseDTO;
import com.humanin.planpaz.dto.AdminReportDetailDTO;
import com.humanin.planpaz.dto.AdminReportReviewDTO;
import com.humanin.planpaz.dto.AdminUserManagementDTO;
import com.humanin.planpaz.dto.ModerationStatsDTO;
import com.humanin.planpaz.infra.exception.BusinessException;
import com.humanin.planpaz.model.Admin;
import com.humanin.planpaz.model.enums.ReportContentType;
import com.humanin.planpaz.model.enums.ReportStatus;
import com.humanin.planpaz.service.ModerationService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/moderation")
@RequiredArgsConstructor
public class AdminModerationController {

	private final ModerationService moderationService;

	@PostMapping("/login")
	public ResponseEntity<AdminLoginResponseDTO> login(@Valid @RequestBody AdminLoginRequestDTO dto) {
		AdminLoginResponseDTO response = moderationService.login(dto);
		return ResponseEntity.ok(response);
	}

	@PostMapping("/admins")
	public ResponseEntity<com.humanin.planpaz.dto.AdminResponseDTO> registerAdmin(@Valid @RequestBody com.humanin.planpaz.dto.CreateAdminRequestDTO dto) {
		com.humanin.planpaz.dto.AdminResponseDTO response = moderationService.registerAdmin(dto);
		return ResponseEntity.status(org.springframework.http.HttpStatus.CREATED).body(response);
	}

	@GetMapping("/me")
	public ResponseEntity<Admin> getMe(Authentication authentication) {
		Admin admin = extractAdmin(authentication);
		return ResponseEntity.ok(admin);
	}

	@GetMapping("/reports")
	public ResponseEntity<List<AdminReportDetailDTO>> getReports(
			@RequestParam(required = false) ReportStatus status,
			@RequestParam(required = false) ReportContentType contentType) {
		List<AdminReportDetailDTO> reports = moderationService.getReports(status, contentType);
		return ResponseEntity.ok(reports);
	}

	@GetMapping("/reports/{id}")
	public ResponseEntity<AdminReportDetailDTO> getReportById(@PathVariable UUID id) {
		AdminReportDetailDTO report = moderationService.getReportById(id);
		return ResponseEntity.ok(report);
	}

	@PostMapping("/reports/{id}/review")
	public ResponseEntity<AdminReportDetailDTO> reviewReport(
			@PathVariable UUID id,
			@Valid @RequestBody AdminReportReviewDTO dto,
			Authentication authentication) {
		Admin admin = extractAdmin(authentication);
		AdminReportDetailDTO response = moderationService.reviewReport(id, dto, admin);
		return ResponseEntity.ok(response);
	}

	@GetMapping("/stats")
	public ResponseEntity<ModerationStatsDTO> getStats() {
		ModerationStatsDTO stats = moderationService.getStats();
		return ResponseEntity.ok(stats);
	}

	@GetMapping("/users")
	public ResponseEntity<List<AdminUserManagementDTO>> getUsers(
			@RequestParam(required = false) String search,
			@RequestParam(required = false) String status) {
		List<AdminUserManagementDTO> users = moderationService.getUsers(search, status);
		return ResponseEntity.ok(users);
	}

	@PostMapping("/users/{id}/ban")
	public ResponseEntity<AdminUserManagementDTO> banUser(@PathVariable UUID id) {
		AdminUserManagementDTO user = moderationService.banUser(id);
		return ResponseEntity.ok(user);
	}

	@PostMapping("/users/{id}/unban")
	public ResponseEntity<AdminUserManagementDTO> unbanUser(@PathVariable UUID id) {
		AdminUserManagementDTO user = moderationService.unbanUser(id);
		return ResponseEntity.ok(user);
	}

	private Admin extractAdmin(Authentication authentication) {
		if (authentication != null && authentication.getPrincipal() instanceof Admin admin) {
			return admin;
		}
		throw new BusinessException("Acesso negado: Administrador não autenticado.");
	}
}
