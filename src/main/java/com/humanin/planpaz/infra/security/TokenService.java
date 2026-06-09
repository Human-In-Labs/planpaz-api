package com.humanin.planpaz.infra.security;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.humanin.planpaz.model.User;

@Service
public class TokenService {
	// pega o valor dessa variável no application.properties
	@Value("${api.security.token.secret}")
	private String secret;
	
	public String generateToken(User user) {
		try {
			Algorithm algorithm = Algorithm.HMAC256(secret);
			
			String token = JWT.create()
					.withIssuer("planpaz-api")
					.withSubject(user.getEmail())
					.withExpiresAt(this.generateExpirationDate())
					.sign(algorithm);
			
			return token;
			
		} catch (JWTCreationException e) {
			throw new RuntimeException("Error while authenticating.");
		}
	}
	
	
	// função que valida o token gerado
	public String validateToken(String token) {
		try {
			Algorithm algorithm = Algorithm.HMAC256(secret);
			return JWT.require(algorithm)
					.withIssuer("planpaz-api")
					.build()
					.verify(token)
					.getSubject();  // pega o email
			
		} catch (JWTVerificationException e) {
			return null;
		}
	}
	
	
	// função que gera uma data de expiração para o token
	private Instant generateExpirationDate() {
		return LocalDateTime.now().plusHours(2).toInstant(ZoneOffset.of("-3"));
	}
}
