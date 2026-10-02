package com.humanin.planpaz.infra.security;

import java.io.IOException;
import java.util.Collections;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.humanin.planpaz.model.Admin;
import com.humanin.planpaz.model.User;
import com.humanin.planpaz.repositories.AdminRepository;
import com.humanin.planpaz.repositories.UserRepository;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class SecurityFilter extends OncePerRequestFilter {

	private final TokenService tokenService;
	private final UserRepository userRepository;
	private final AdminRepository adminRepository;

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {

		var token = this.recoverToken(request);

		if (token != null) {
			var login = tokenService.validateToken(token);
			if (login != null && !login.isEmpty()) {
				// 1. Tenta autenticar como Administrador
				Admin admin = adminRepository.findByEmail(login).orElse(null);
				if (admin != null) {
					var authorities = Collections.singletonList(new SimpleGrantedAuthority("ROLE_ADMIN"));
					var authentication = new UsernamePasswordAuthenticationToken(admin, null, authorities);
					SecurityContextHolder.getContext().setAuthentication(authentication);
				} else {
					// 2. Tenta autenticar como Usuário Comum
					User user = userRepository.findByEmail(login).orElse(null);
					if (user != null) {
						if (Boolean.TRUE.equals(user.getBanned())) {
							log.warn("[SECURITY] Sessão encerrada via JWT para usuário banido: {}", user.getEmail());
							response.setStatus(HttpServletResponse.SC_FORBIDDEN);
							response.setContentType("application/json");
							response.setCharacterEncoding("UTF-8");
							response.getWriter().write("{\"status\":403,\"error\":\"Forbidden\",\"message\":\"Sua conta foi suspensa por violar os termos de uso da plataforma. Entre em contato com o suporte para mais informações.\",\"path\":\"" + request.getRequestURI() + "\"}");
							return;
						}

						var authorities = Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER"));
						var authentication = new UsernamePasswordAuthenticationToken(user, null, authorities);
						SecurityContextHolder.getContext().setAuthentication(authentication);
					}
				}
			}
		}

		filterChain.doFilter(request, response);
	}

	private String recoverToken(HttpServletRequest request) {
		var authHeader = request.getHeader("Authorization");
		if (authHeader == null || !authHeader.startsWith("Bearer ")) {
			return null;
		}
		return authHeader.replace("Bearer ", "").trim();
	}
}