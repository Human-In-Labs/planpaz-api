package com.humanin.planpaz.infra.security;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.humanin.planpaz.model.Admin;
import com.humanin.planpaz.model.User;

@Service
public class TokenService {

	@Value("${api.security.token.secret}")
	private String secret;

	public String generateToken(User user) {
		try {
			Algorithm algorithm = Algorithm.HMAC256(secret);

			return JWT.create().withIssuer("planpaz-api").withSubject(user.getEmail())
					.withClaim("role", "USER")
					.withExpiresAt(this.generateExpirationDate()).sign(algorithm);

		} catch (JWTCreationException e) {
			throw new RuntimeException("Error while authenticating.", e);
		}
	}

	public String generateAdminToken(Admin admin) {
		try {
			Algorithm algorithm = Algorithm.HMAC256(secret);

			return JWT.create().withIssuer("planpaz-api").withSubject(admin.getEmail())
					.withClaim("role", "ADMIN")
					.withClaim("adminId", admin.getId().toString())
					.withExpiresAt(this.generateExpirationDate()).sign(algorithm);

		} catch (JWTCreationException e) {
			throw new RuntimeException("Error while authenticating admin.", e);
		}
	}

	public String validateToken(String token) {
		try {
			Algorithm algorithm = Algorithm.HMAC256(secret);
			return JWT.require(algorithm).withIssuer("planpaz-api").build().verify(token).getSubject();

		} catch (JWTVerificationException e) {
			return null;
		}
	}

	private Instant generateExpirationDate() {
		return Instant.now().plus(24, ChronoUnit.HOURS);
	}
}