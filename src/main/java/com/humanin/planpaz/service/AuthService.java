package com.humanin.planpaz.service;

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

	@Transactional(readOnly = true)
	public ResponseDTO login(LoginRequestDTO body) {
		log.info("Tentativa de login para o e-mail: {}", body.email());

		User user = userRepository.findByEmail(body.email())
				.orElseThrow(() -> new BadCredentialsException("Credenciais inválidas."));

		if (!passwordEncoder.matches(body.password(), user.getPassword())) {
			log.warn("Falha de autenticação (senha incorreta) para o e-mail: {}", body.email());
			throw new BadCredentialsException("Credenciais inválidas.");
		}

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

		userRepository.save(newUser);

		String token = tokenService.generateToken(newUser);
		log.info("Novo usuário registrado com sucesso: {}", newUser.getUsername());

		return new ResponseDTO(newUser.getName(), newUser.getUsername(), token);
	}
}
