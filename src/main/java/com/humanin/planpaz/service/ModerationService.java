package com.humanin.planpaz.service;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.humanin.planpaz.dto.AdminLoginRequestDTO;
import com.humanin.planpaz.dto.AdminLoginResponseDTO;
import com.humanin.planpaz.dto.AdminReportDetailDTO;
import com.humanin.planpaz.dto.AdminReportReviewDTO;
import com.humanin.planpaz.dto.AdminResponseDTO;
import com.humanin.planpaz.dto.AdminUserManagementDTO;
import com.humanin.planpaz.dto.CreateAdminRequestDTO;
import com.humanin.planpaz.dto.ModerationStatsDTO;
import com.humanin.planpaz.infra.exception.BusinessException;
import com.humanin.planpaz.infra.exception.ResourceNotFoundException;
import com.humanin.planpaz.infra.security.TokenService;
import com.humanin.planpaz.model.Admin;
import com.humanin.planpaz.model.Comment;
import com.humanin.planpaz.model.Notification;
import com.humanin.planpaz.model.Post;
import com.humanin.planpaz.model.Report;
import com.humanin.planpaz.model.User;
import com.humanin.planpaz.model.enums.ReportActionTaken;
import com.humanin.planpaz.model.enums.ReportContentType;
import com.humanin.planpaz.model.enums.ReportStatus;
import com.humanin.planpaz.repositories.AdminRepository;
import com.humanin.planpaz.repositories.CommentRepository;
import com.humanin.planpaz.repositories.LikeRepository;
import com.humanin.planpaz.repositories.NotificationRepository;
import com.humanin.planpaz.repositories.PostRepository;
import com.humanin.planpaz.repositories.ReportRepository;
import com.humanin.planpaz.repositories.UserRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class ModerationService {

	private final AdminRepository adminRepository;
	private final ReportRepository reportRepository;
	private final PostRepository postRepository;
	private final CommentRepository commentRepository;
	private final LikeRepository likeRepository;
	private final UserRepository userRepository;
	private final NotificationRepository notificationRepository;
	private final EmailService emailService;
	private final TokenService tokenService;
	private final PasswordEncoder passwordEncoder;

	private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

	@Transactional(readOnly = true)
	public AdminLoginResponseDTO login(AdminLoginRequestDTO dto) {
		Admin admin = adminRepository.findByEmail(dto.getEmail())
				.orElseThrow(() -> new BusinessException("E-mail ou senha de administrador inválidos."));

		if (!passwordEncoder.matches(dto.getPassword(), admin.getPassword())) {
			throw new BusinessException("E-mail ou senha de administrador inválidos.");
		}

		String token = tokenService.generateAdminToken(admin);
		log.info("[MODERATION] Admin autenticado com sucesso: {}", admin.getEmail());

		return new AdminLoginResponseDTO(token, admin.getId(), admin.getName(), admin.getEmail());
	}

	@Transactional
	public AdminResponseDTO registerAdmin(CreateAdminRequestDTO dto) {
		if (adminRepository.existsByEmail(dto.getEmail())) {
			throw new BusinessException("E-mail de administrador já cadastrado.");
		}

		Admin admin = new Admin();
		admin.setName(dto.getName());
		admin.setEmail(dto.getEmail());
		admin.setPassword(passwordEncoder.encode(dto.getPassword()));

		Admin saved = adminRepository.save(admin);
		log.info("[MODERATION] Novo administrador cadastrado com sucesso: {}", saved.getEmail());

		String createdAtFormatted = saved.getCreatedAt() != null ? saved.getCreatedAt().format(DATE_FORMATTER) : "";
		return new AdminResponseDTO(saved.getId(), saved.getName(), saved.getEmail(), createdAtFormatted);
	}

	@Transactional(readOnly = true)
	public List<AdminReportDetailDTO> getReports(ReportStatus status, ReportContentType contentType) {
		List<Report> reports;

		if (status != null && contentType != null) {
			reports = reportRepository.findByStatusAndContentTypeOrderByCreatedAtDesc(status, contentType);
		} else if (status != null) {
			reports = reportRepository.findByStatusOrderByCreatedAtDesc(status);
		} else if (contentType != null) {
			reports = reportRepository.findByContentTypeOrderByCreatedAtDesc(contentType);
		} else {
			reports = reportRepository.findAllByOrderByCreatedAtDesc();
		}

		List<AdminReportDetailDTO> dtos = new ArrayList<>();
		for (Report report : reports) {
			dtos.add(buildDetailDTO(report));
		}
		return dtos;
	}

	@Transactional(readOnly = true)
	public AdminReportDetailDTO getReportById(UUID reportId) {
		Report report = reportRepository.findById(reportId)
				.orElseThrow(() -> new ResourceNotFoundException("Denúncia não encontrada com ID: " + reportId));
		return buildDetailDTO(report);
	}

	@Transactional
	public AdminReportDetailDTO reviewReport(UUID reportId, AdminReportReviewDTO dto, Admin reviewer) {
		Report report = reportRepository.findById(reportId)
				.orElseThrow(() -> new ResourceNotFoundException("Denúncia não encontrada com ID: " + reportId));

		ReportStatus decision = dto.getDecision();
		if (decision == null) {
			throw new BusinessException("A decisão da moderação é obrigatória (ACCEPTED ou REJECTED).");
		}

		report.setStatus(decision);
		report.setReviewedBy(reviewer);

		// 1. REJEIÇÃO: Nenhuma ação é executada no conteúdo ou no usuário
		if (decision == ReportStatus.REJECTED) {
			report.setActionTaken(ReportActionTaken.NONE);
			Report savedReport = reportRepository.save(report);
			log.info("[MODERATION] Denúncia ID: {} REJEITADA por admin {}. Conteúdo e usuário mantidos intactos.",
					savedReport.getId(), reviewer.getEmail());
			return buildDetailDTO(savedReport);
		}

		// 2. ACEITAÇÃO (ACCEPTED / RESOLVED): Determina as providências ativas
		ReportActionTaken actionTaken = ReportActionTaken.NONE;
		if (dto.getActionTaken() != null && dto.getActionTaken() != ReportActionTaken.NONE) {
			actionTaken = dto.getActionTaken();
		} else if (Boolean.TRUE.equals(dto.getBanUser())) {
			actionTaken = ReportActionTaken.BAN_USER;
		} else if (Boolean.TRUE.equals(dto.getDeleteContent())) {
			actionTaken = ReportActionTaken.DELETE_CONTENT;
		} else if (Boolean.TRUE.equals(dto.getWarnUser())) {
			actionTaken = ReportActionTaken.WARN_USER;
		}

		report.setActionTaken(actionTaken);

		boolean shouldDelete = Boolean.TRUE.equals(dto.getDeleteContent())
				|| actionTaken == ReportActionTaken.DELETE_CONTENT || actionTaken == ReportActionTaken.BAN_USER;

		boolean shouldBan = Boolean.TRUE.equals(dto.getBanUser()) || actionTaken == ReportActionTaken.BAN_USER;

		boolean shouldWarn = Boolean.TRUE.equals(dto.getWarnUser()) || actionTaken != ReportActionTaken.NONE;

		User contentAuthor = null;
		Post postToDelete = null;
		Comment commentToDelete = null;

		// PASSO A: Carrega o autor (User) diretamente do repositório ANTES de deletar
		// qualquer conteúdo
		if (report.getContentType() == ReportContentType.POST) {
			postToDelete = postRepository.findById(report.getContentId()).orElse(null);
			if (postToDelete != null && postToDelete.getAuthor() != null) {
				contentAuthor = userRepository.findById(postToDelete.getAuthor().getId()).orElse(null);
			}
		} else if (report.getContentType() == ReportContentType.COMMENT) {
			commentToDelete = commentRepository.findById(report.getContentId()).orElse(null);
			if (commentToDelete != null && commentToDelete.getAuthor() != null) {
				contentAuthor = userRepository.findById(commentToDelete.getAuthor().getId()).orElse(null);
			}
		}

		// Fallback: Tenta carregar o autor diretamente pelo contentId caso o
		// post/comentário já tenha sido removido
		if (contentAuthor == null && report.getContentId() != null) {
			contentAuthor = userRepository.findById(report.getContentId()).orElse(null);
		}

		// PASSO B: Providência de Banimento do Usuário (RF52)
		if (shouldBan && contentAuthor != null) {
			contentAuthor.setBanned(true);
			userRepository.save(contentAuthor);
			log.info("[MODERATION_RF52] Usuário banido da plataforma: {}", contentAuthor.getEmail());

			try {
				emailService.enviarEmailBanimento(contentAuthor.getEmail(), contentAuthor.getName(),
						report.getReason() != null ? report.getReason().getDescription() : "", dto.getNote());
				log.info("[MODERATION_EMAIL] E-mail de banimento enviado com sucesso para: {}",
						contentAuthor.getEmail());
			} catch (Exception e) {
				log.error("[MODERATION_EMAIL] Erro ao enviar e-mail de banimento: {}", e.getMessage());
			}
		}

		// PASSO C: Notificação Interna e por E-mail de Advertência (RF51)
		if (shouldWarn && contentAuthor != null) {
			String noticeText = buildNotificationMessage(report.getContentType(), actionTaken, dto.getNote());

			Notification notification = new Notification();
			notification.setUser(contentAuthor);
			notification.setTitle("⚠️ Alerta de Moderação - PlanPaz");
			notification.setDescription(noticeText);
			notificationRepository.save(notification);

			if (!shouldBan) {
				try {
					emailService.enviarEmailAdvertencia(contentAuthor.getEmail(), contentAuthor.getName(),
							report.getContentType() != null ? report.getContentType().name() : "POST",
							report.getReason() != null ? report.getReason().getDescription() : "", dto.getNote());
					log.info("[MODERATION_EMAIL] E-mail de advertência enviado com sucesso para: {}",
							contentAuthor.getEmail());
				} catch (Exception e) {
					log.error("[MODERATION_EMAIL] Erro ao enviar e-mail de advertência: {}", e.getMessage());
				}
			}
		}

		// PASSO D: Exclusão de Conteúdo POR ÚLTIMO (RF50)
		if (shouldDelete) {
			if (postToDelete != null) {
				likeRepository.deleteByPostId(postToDelete.getId());
				commentRepository.deleteByPostId(postToDelete.getId());
				postRepository.delete(postToDelete);
				log.info("[MODERATION_RF50] Post ID: {} excluído com sucesso sem erros de FK.", postToDelete.getId());
			} else if (commentToDelete != null) {
				commentRepository.deleteByParentCommentId(commentToDelete.getId());
				commentRepository.delete(commentToDelete);
				log.info("[MODERATION_RF50] Comentário ID: {} excluído com sucesso sem erros de FK.",
						commentToDelete.getId());
			}
		}

		Report savedReport = reportRepository.save(report);
		log.info("[MODERATION] Denúncia ID: {} ACEITA por admin {}. Decisão: {}, Ação: {}", savedReport.getId(),
				reviewer.getEmail(), decision, actionTaken);

		return buildDetailDTO(savedReport);
	}

	@Transactional(readOnly = true)
	public ModerationStatsDTO getStats() {
		long total = reportRepository.count();
		long pending = reportRepository.countByStatus(ReportStatus.PENDING);
		long accepted = reportRepository.countByStatus(ReportStatus.ACCEPTED)
				+ reportRepository.countByStatus(ReportStatus.RESOLVED);
		long rejected = reportRepository.countByStatus(ReportStatus.REJECTED);
		long deleted = reportRepository.countByActionTaken(ReportActionTaken.DELETE_CONTENT)
				+ reportRepository.countByActionTaken(ReportActionTaken.BAN_USER);
		long banned = userRepository.countByBanned(true);

		return ModerationStatsDTO.builder().totalReports(total).pendingReports(pending).acceptedReports(accepted)
				.rejectedReports(rejected).deletedContents(deleted).bannedUsers(banned).build();
	}

	@Transactional(readOnly = true)
	public List<AdminUserManagementDTO> getUsers(String search, String status) {
		Boolean bannedFilter = null;
		if ("BANNED".equalsIgnoreCase(status)) {
			bannedFilter = true;
		} else if ("ACTIVE".equalsIgnoreCase(status)) {
			bannedFilter = false;
		}

		List<User> users;
		if (search != null && !search.trim().isEmpty()) {
			String query = search.trim().toLowerCase();
			Boolean finalBannedFilter = bannedFilter;
			users = userRepository.findAllByOrderByCreatedAtDesc().stream()
					.filter(u -> (u.getName() != null && u.getName().toLowerCase().contains(query))
							|| (u.getEmail() != null && u.getEmail().toLowerCase().contains(query))
							|| (u.getUsername() != null && u.getUsername().toLowerCase().contains(query)))
					.filter(u -> finalBannedFilter == null || Boolean.TRUE.equals(u.getBanned()) == finalBannedFilter)
					.toList();
		} else if (bannedFilter != null) {
			users = userRepository.findByBannedOrderByCreatedAtDesc(bannedFilter);
		} else {
			users = userRepository.findAllByOrderByCreatedAtDesc();
		}

		return users.stream().map(this::buildUserDTO).toList();
	}

	@Transactional
	public AdminUserManagementDTO banUser(UUID userId) {
		User user = userRepository.findById(userId)
				.orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado com ID: " + userId));

		user.setBanned(true);
		User saved = userRepository.save(user);

		try {
			emailService.enviarEmailBanimento(saved.getEmail(), saved.getName(),
					"Decisão administrativa do painel de moderação", "Sua conta foi suspensa por um moderador.");
		} catch (Exception e) {
			log.error("[MODERATION_USER] Erro ao enviar e-mail de banimento: {}", e.getMessage());
		}

		log.info("[MODERATION_USER] Usuário banido via painel de moderação: ID {}, Email {}", saved.getId(),
				saved.getEmail());
		return buildUserDTO(saved);
	}

	@Transactional
	public AdminUserManagementDTO unbanUser(UUID userId) {
		User user = userRepository.findById(userId)
				.orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado com ID: " + userId));

		user.setBanned(false);
		User saved = userRepository.save(user);

		try {
			emailService.sendEmail(saved.getEmail(), "🌱 PlanPaz | Sua conta foi reativada!", "Olá, "
					+ (saved.getName() != null ? saved.getName() : "Cultivador")
					+ "! 🌿\n\nInformamos que sua conta no PlanPaz foi reativada com sucesso por nossa equipe de moderação. Você já pode acessar a plataforma normalmente.\n\nAtenciosamente,\nEquipe PlanPaz");
		} catch (Exception e) {
			log.error("[MODERATION_USER] Erro ao enviar e-mail de reativação: {}", e.getMessage());
		}

		log.info("[MODERATION_USER] Usuário reativado via painel de moderação: ID {}, Email {}", saved.getId(),
				saved.getEmail());
		return buildUserDTO(saved);
	}

	private AdminUserManagementDTO buildUserDTO(User user) {
		String createdAtFormatted = user.getCreatedAt() != null ? user.getCreatedAt().format(DATE_FORMATTER) : "";
		return AdminUserManagementDTO.builder().id(user.getId()).name(user.getName()).username(user.getUsername())
				.email(user.getEmail()).avatarUrl(user.getAvatarUrl()).banned(Boolean.TRUE.equals(user.getBanned()))
				.createdAt(createdAtFormatted).build();
	}

	private AdminReportDetailDTO buildDetailDTO(Report report) {
		AdminReportDetailDTO.AdminReportDetailDTOBuilder builder = AdminReportDetailDTO.builder().id(report.getId())
				.contentType(report.getContentType()).contentId(report.getContentId()).reason(report.getReason())
				.reasonDescription(report.getReason() != null ? report.getReason().getDescription() : "")
				.message(report.getMessage()).status(report.getStatus())
				.statusDescription(report.getStatus() != null ? report.getStatus().getDescription() : "")
				.actionTaken(report.getActionTaken())
				.actionTakenDescription(report.getActionTaken() != null ? report.getActionTaken().getDescription() : "")
				.createdAt(report.getCreatedAt() != null ? report.getCreatedAt().format(DATE_FORMATTER) : "")
				.updatedAt(report.getUpdatedAt() != null ? report.getUpdatedAt().format(DATE_FORMATTER) : "");

		// 1. Denunciante (Reporter)
		if (report.getReporter() != null) {
			User reporter = report.getReporter();
			builder.reporterId(reporter.getId()).reporterName(reporter.getName())
					.reporterUsername(reporter.getUsername()).reporterEmail(reporter.getEmail());
		}

		// 2. Moderador (ReviewedBy)
		if (report.getReviewedBy() != null) {
			Admin admin = report.getReviewedBy();
			builder.reviewedById(admin.getId()).reviewedByName(admin.getName()).reviewedByEmail(admin.getEmail());
		}

		// 3. Conteúdo Denunciado (Post ou Comment)
		if (report.getContentType() == ReportContentType.POST) {
			Post post = postRepository.findById(report.getContentId()).orElse(null);
			if (post != null) {
				String postText = post.getContent() != null ? post.getContent() : "";

				builder.targetTitle(post.getTitle() != null ? post.getTitle() : "Publicação da Comunidade")
						.targetText(postText).targetImageUrl(post.getMedia())
						.postedAt(post.getPostedAt() != null ? post.getPostedAt().format(DATE_FORMATTER) : "")
						.isDeleted(false);

				if (post.getAuthor() != null) {
					User author = post.getAuthor();
					builder.authorId(author.getId()).authorName(author.getName()).authorUsername(author.getUsername())
							.authorEmail(author.getEmail()).authorAvatarUrl(author.getAvatarUrl())
							.authorBanned(Boolean.TRUE.equals(author.getBanned()));
				}
			} else {
				builder.targetTitle("[Publicação Removida]").targetText("Esta publicação foi removida do sistema.")
						.isDeleted(true);
			}
		} else if (report.getContentType() == ReportContentType.COMMENT) {
			Comment comment = commentRepository.findById(report.getContentId()).orElse(null);
			if (comment != null) {
				String commentText = comment.getContent() != null ? comment.getContent() : "";

				builder.targetTitle("Comentário em Publicação").targetText(commentText)
						.postedAt(
								comment.getCommentedAt() != null ? comment.getCommentedAt().format(DATE_FORMATTER) : "")
						.isDeleted(false);

				if (comment.getAuthor() != null) {
					User author = comment.getAuthor();
					builder.authorId(author.getId()).authorName(author.getName()).authorUsername(author.getUsername())
							.authorEmail(author.getEmail()).authorAvatarUrl(author.getAvatarUrl())
							.authorBanned(Boolean.TRUE.equals(author.getBanned()));
				}
			} else {
				builder.targetTitle("[Comentário Removido]").targetText("Este comentário foi removido do sistema.")
						.isDeleted(true);
			}
		}

		return builder.build();
	}

	private String buildNotificationMessage(ReportContentType contentType, ReportActionTaken actionTaken, String note) {
		String typeName = contentType == ReportContentType.POST ? "Sua publicação" : "Seu comentário";
		String actionDesc = actionTaken != null ? actionTaken.getDescription() : "análise de moderação";

		StringBuilder sb = new StringBuilder();
		sb.append(typeName).append(" foi analisado pela equipe de moderação PlanPaz. Providência: ").append(actionDesc)
				.append(".");

		if (note != null && !note.trim().isEmpty()) {
			sb.append(" Observação da moderação: ").append(note.trim());
		} else {
			sb.append(" Por favor, certifique-se de cumprir os termos e diretrizes da comunidade PlanPaz.");
		}

		return sb.toString();
	}
}