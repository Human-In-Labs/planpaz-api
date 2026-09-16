package com.humanin.planpaz.service;

import java.time.LocalDateTime;
import java.util.UUID;

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

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {

	private final UserRepository userRepository;
	private final PasswordEncoder passwordEncoder;
	private final TokenService tokenService;
	// private final EmailService emailService; // TEMPORARIAMENTE DESATIVADO

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

		// TEMPORARIAMENTE MODIFICADO: Conta já é criada como verificada para testes locais
		newUser.setEmailVerified(true);
		newUser.setVerificationToken(null);
		newUser.setVerificationTokenExpiresAt(null);

		/* 
		newUser.setEmailVerified(false);
		newUser.setVerificationToken(UUID.randomUUID().toString());
		newUser.setVerificationTokenExpiresAt(LocalDateTime.now().plusHours(VERIFICATION_TOKEN_HOURS));
		*/

		userRepository.save(newUser);

		// enviarEmailDeVerificacao(newUser); // TEMPORARIAMENTE DESATIVADO

		log.info("Novo usuário registrado: {}", newUser.getUsername());

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

	private void enviarEmailDeVerificacao(User user) {
		String link = baseUrl + "/api/auth/verify-email?token=" + user.getVerificationToken();
		emailService.enviarEmailDeVerificacao(user.getEmail(), user.getName(), link);
	}
	*/
}