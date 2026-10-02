package com.humanin.planpaz.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.humanin.planpaz.dto.LoginRequestDTO;
import com.humanin.planpaz.dto.RegisterRequestDTO;
import com.humanin.planpaz.dto.ResponseDTO;
import com.humanin.planpaz.infra.exception.BusinessException;
import com.humanin.planpaz.infra.security.TokenService;
import com.humanin.planpaz.model.User;
import com.humanin.planpaz.repositories.UserRepository;

import com.humanin.planpaz.dto.ForgotPasswordRequestDTO;
import com.humanin.planpaz.dto.ResetPasswordRequestDTO;
import com.humanin.planpaz.dto.VerifyCodeRequestDTO;
import com.humanin.planpaz.model.PasswordResetToken;
import com.humanin.planpaz.repositories.PasswordResetTokenRepository;
import java.time.LocalDateTime;
import java.util.concurrent.ThreadLocalRandom;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {

	private final UserRepository userRepository;
	private final PasswordEncoder passwordEncoder;
	private final TokenService tokenService;
	private final AchievementService achievementService;
	private final EmailService emailService;
	private final PasswordResetTokenRepository passwordResetTokenRepository;

	@Value("${app.base-url}")
	private String baseUrl;

	// Tempo de validade do link de verificação de e-mail
	// private static final long VERIFICATION_TOKEN_HOURS = 24;

	@Transactional(readOnly = true)
	public ResponseDTO login(LoginRequestDTO body) {
		log.info("Tentativa de login para o e-mail: {}", body.email());

		User user = userRepository.findByEmail(body.email())
				.orElseThrow(() -> new BadCredentialsException("Credenciais inválidas."));

		if (!passwordEncoder.matches(body.password(), user.getPassword())) {
			log.warn("Falha de autenticação (senha incorreta) para o e-mail: {}", body.email());
			throw new BadCredentialsException("Credenciais inválidas.");
		}

		/* TEMPORARIAMENTE DESATIVADO: Validação de e-mail no login
		if (!Boolean.TRUE.equals(user.getEmailVerified())) {
			log.warn("Tentativa de login com e-mail não verificado: {}", body.email());
			throw new BusinessException(
					"Seu e-mail ainda não foi verificado. Confira sua caixa de entrada ou peça um novo link em /api/auth/resend-verification.");
		}
		*/

		String token = tokenService.generateToken(user);
		log.info("Login bem-sucedido para o usuário: {}", user.getUsername());

		return new ResponseDTO(user.getName(), user.getUsername(), token);
	}

	@Transactional
	public ResponseDTO register(RegisterRequestDTO body) {
		log.info("Tentativa de registro de novo usuário: {}", body.username());

		if (body.name() == null || body.name().isBlank()) {
			throw new BusinessException("O nome é obrigatório.");
		}
		if (body.username() == null || body.username().isBlank()) {
			throw new BusinessException("O nome de usuário é obrigatório.");
		}
		if (body.email() == null || body.email().isBlank()) {
			throw new BusinessException("O e-mail é obrigatório.");
		}
		if (body.password() == null || body.password().isBlank()) {
			throw new BusinessException("A senha é obrigatória.");
		}

		if (userRepository.existsByEmail(body.email())) {
			log.warn("Tentativa de registro com e-mail já cadastrado: {}", body.email());
			throw new BusinessException("Já existe um usuário cadastrado com este e-mail.");
		}

		if (userRepository.existsByUsername(body.username())) {
			log.warn("Tentativa de registro com username já cadastrado: {}", body.username());
			throw new BusinessException("Já existe um usuário cadastrado com este nome de usuário.");
		}

		User newUser = new User();
		newUser.setName(body.name().trim());
		newUser.setUsername(body.username().trim());
		newUser.setEmail(body.email().trim().toLowerCase());
		newUser.setPassword(passwordEncoder.encode(body.password()));

		// Conta começa não verificada, com um token de confirmação válido por 24h
		newUser.setEmailVerified(true);

		User savedUser = userRepository.save(newUser);

		try {
			achievementService.checkAndGrantAll(savedUser.getId());
		} catch (Exception e) {
			log.error("Erro ao conceder conquistas iniciais: {}", e.getMessage());
		}

		// enviarEmailDeVerificacao(newUser); // TEMPORARIAMENTE DESATIVADO

		log.info("Novo usuário registrado: {}", savedUser.getUsername());

		// Retorna o token JWT diretamente no registro para facilitar o uso sem precisar enviar e-mail
		String token = tokenService.generateToken(newUser);
		return new ResponseDTO(newUser.getName(), newUser.getUsername(), token, "Cadastro realizado com sucesso!");
	}

	// ==========================
	// CONFIRMA O E-MAIL A PARTIR DO LINK ENVIADO NO CADASTRO (DESATIVADO TEMPORARIAMENTE)
	// ==========================

	/*
	@Transactional
	public void verifyEmail(String token) {
		User user = userRepository.findByVerificationToken(token)
				.orElseThrow(() -> new BusinessException("Link de verificação inválido."));

		if (Boolean.TRUE.equals(user.getEmailVerified())) {
			return; // já verificado, clicar de novo no link não deve dar erro
		}

		if (user.getVerificationTokenExpiresAt() == null
				|| user.getVerificationTokenExpiresAt().isBefore(LocalDateTime.now())) {
			throw new BusinessException("Este link de verificação expirou. Solicite um novo cadastro ou reenvio.");
		}

		user.setEmailVerified(true);
		user.setVerificationToken(null);
		user.setVerificationTokenExpiresAt(null);
		userRepository.save(user);

		log.info("E-mail verificado com sucesso para o usuário: {}", user.getUsername());
	}
	*/

	// ==========================
	// REENVIA O E-MAIL DE VERIFICAÇÃO (LINK EXPIRADO OU PERDIDO) (DESATIVADO TEMPORARIAMENTE)
	// ==========================

	/*
	@Transactional
	public void resendVerification(String email) {
		User user = userRepository.findByEmail(email)
				.orElseThrow(() -> new BusinessException("Nenhum usuário encontrado com este e-mail."));

		if (Boolean.TRUE.equals(user.getEmailVerified())) {
			throw new BusinessException("Este e-mail já foi verificado. Você já pode fazer login.");
		}

		user.setVerificationToken(UUID.randomUUID().toString());
		user.setVerificationTokenExpiresAt(LocalDateTime.now().plusHours(VERIFICATION_TOKEN_HOURS));
		userRepository.save(user);

		enviarEmailDeVerificacao(user);

		log.info("Link de verificação reenviado para: {}", email);
	}

	/*
	private void enviarEmailDeVerificacao(User user) {
		String link = baseUrl + "/api/auth/verify-email?token=" + user.getVerificationToken();
		emailService.enviarEmailDeVerificacao(user.getEmail(), user.getName(), link);
	}
	*/

	// ==========================
	// RECUPERAÇÃO DE SENHA POR E-MAIL (RF03)
	// ==========================

	@Transactional
	public void requestPasswordReset(ForgotPasswordRequestDTO body) {
		String emailClean = body.email().trim().toLowerCase();
		log.info("Solicitação de redefinição de senha para o e-mail: {}", emailClean);

		User user = userRepository.findByEmail(emailClean).orElse(null);
		if (user == null) {
			// Por segurança, não informamos se o e-mail existe ou não
			log.warn("Solicitação de código para e-mail não encontrado: {}", emailClean);
			return;
		}

		// Trava de 15 minutos: verifica se já existe um token ativo enviado nos últimos 15 minutos
		passwordResetTokenRepository.findTopByEmailAndUsedFalseOrderByExpiresAtDesc(emailClean)
				.ifPresent(existingToken -> {
					if (existingToken.getExpiresAt().isAfter(LocalDateTime.now())) {
						throw new BusinessException(
								"RATE_LIMIT_EXCEEDED: Você já solicitou um código recentemente! Por favor, aguarde 15 minutos para enviar um novo e-mail e verifique sua caixa de entrada (e pasta de spam) para encontrar o código já enviado.");
					}
				});

		// Remove tokens antigos para este e-mail
		try {
			passwordResetTokenRepository.deleteByEmail(emailClean);
		} catch (Exception e) {
			log.warn("Erro ao limpar tokens antigos de reset: {}", e.getMessage());
		}

		// Gera um código de 6 dígitos aleatório
		String code = String.format("%06d", ThreadLocalRandom.current().nextInt(0, 1000000));
		LocalDateTime expiresAt = LocalDateTime.now().plusMinutes(15);

		PasswordResetToken resetToken = new PasswordResetToken();
		resetToken.setEmail(emailClean);
		resetToken.setCode(code);
		resetToken.setExpiresAt(expiresAt);
		resetToken.setUsed(false);

		passwordResetTokenRepository.save(resetToken);

		// Envia o e-mail com o código
		emailService.enviarCodigoRecuperacaoSenha(emailClean, user.getName(), code);
		log.info("Código de redefinição enviado com sucesso para: {}", emailClean);
	}

	@Transactional(readOnly = true)
	public void verifyResetCode(VerifyCodeRequestDTO body) {
		String emailClean = body.email().trim().toLowerCase();
		String codeClean = body.code().trim();

		PasswordResetToken token = passwordResetTokenRepository
				.findByEmailAndCodeAndUsedFalse(emailClean, codeClean)
				.orElseThrow(() -> new BusinessException("Código inválido ou já utilizado."));

		if (token.getExpiresAt().isBefore(LocalDateTime.now())) {
			throw new BusinessException("Este código de verificação expirou. Solicite um novo código.");
		}
	}

	@Transactional
	public void resetPassword(ResetPasswordRequestDTO body) {
		String emailClean = body.email().trim().toLowerCase();
		String codeClean = body.code().trim();

		PasswordResetToken token = passwordResetTokenRepository
				.findByEmailAndCodeAndUsedFalse(emailClean, codeClean)
				.orElseThrow(() -> new BusinessException("Código inválido ou já utilizado."));

		if (token.getExpiresAt().isBefore(LocalDateTime.now())) {
			throw new BusinessException("Este código de verificação expirou. Solicite um novo código.");
		}

		User user = userRepository.findByEmail(emailClean)
				.orElseThrow(() -> new BusinessException("Usuário não encontrado."));

		user.setPassword(passwordEncoder.encode(body.newPassword()));
		userRepository.save(user);

		// Invalida o token
		token.setUsed(true);
		passwordResetTokenRepository.save(token);

		log.info("Senha redefinida com sucesso para o e-mail: {}", emailClean);
	}
}