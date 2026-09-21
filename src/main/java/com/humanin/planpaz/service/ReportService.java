package com.humanin.planpaz.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.humanin.planpaz.dto.ReportCreateDTO;
import com.humanin.planpaz.dto.ReportResponseDTO;
import com.humanin.planpaz.infra.exception.ResourceNotFoundException;
import com.humanin.planpaz.model.Comment;
import com.humanin.planpaz.model.Post;
import com.humanin.planpaz.model.Report;
import com.humanin.planpaz.model.User;
import com.humanin.planpaz.model.enums.ReportContentType;
import com.humanin.planpaz.model.enums.ReportStatus;
import com.humanin.planpaz.repositories.CommentRepository;
import com.humanin.planpaz.repositories.PostRepository;
import com.humanin.planpaz.repositories.ReportRepository;
import com.humanin.planpaz.repositories.UserRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class ReportService {

	private final ReportRepository reportRepository;
	private final PostRepository postRepository;
	private final CommentRepository commentRepository;
	private final UserRepository userRepository;
	private final EmailService emailService;

	@Value("${spring.mail.moderation-email:ofchumanin@gmail.com}")
	private String moderationEmail;

	@Transactional
	public ReportResponseDTO createReport(ReportCreateDTO dto, User authenticatedUser) {
		User reporter = authenticatedUser;
		if (reporter == null && dto.getReporterId() != null) {
			reporter = userRepository.findById(dto.getReporterId()).orElse(null);
		}

		if (reporter == null) {
			throw new ResourceNotFoundException("Usuário denunciante não especificado.");
		}

		String authorUsername = "Desconhecido";
		String contentText = "";

		if (dto.getContentType() == ReportContentType.POST) {
			Post post = postRepository.findById(dto.getContentId()).orElseThrow(
					() -> new ResourceNotFoundException("Publicação não encontrada com o ID: " + dto.getContentId()));
			if (post.getAuthor() != null) {
				authorUsername = post.getAuthor().getUsername();
			}
			String title = post.getTitle() != null ? post.getTitle() : "";
			String body = post.getContent() != null ? post.getContent() : "";
			contentText = (title + " - " + body).trim();
		} else if (dto.getContentType() == ReportContentType.COMMENT) {
			Comment comment = commentRepository.findById(dto.getContentId()).orElseThrow(
					() -> new ResourceNotFoundException("Comentário não encontrado com o ID: " + dto.getContentId()));
			if (comment.getAuthor() != null) {
				authorUsername = comment.getAuthor().getUsername();
			}
			contentText = comment.getContent() != null ? comment.getContent() : "";
		}

		Report report = new Report();
		report.setContentType(dto.getContentType());
		report.setContentId(dto.getContentId());
		report.setReporter(reporter);
		report.setReason(dto.getReason());
		report.setMessage(dto.getMessage());
		report.setStatus(ReportStatus.PENDING);

		Report savedReport = reportRepository.save(report);

		log.info("[REPORT] Denúncia registrada com sucesso ID: {}, Tipo: {}, Status: PENDENTE", savedReport.getId(),
				savedReport.getContentType());

		// Dispara e-mail de notificação para a equipe de moderação
		String reasonText = dto.getReason() != null ? dto.getReason().getDescription() : dto.getReason().name();
		emailService.enviarEmailDeDenuncia(moderationEmail, dto.getContentType().name(), dto.getContentId().toString(),
				authorUsername, reporter.getUsername(), reasonText, dto.getMessage(), contentText);

		return ReportResponseDTO.fromEntity(savedReport);
	}
}
