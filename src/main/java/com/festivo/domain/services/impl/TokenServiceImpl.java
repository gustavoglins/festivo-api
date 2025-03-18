package com.festivo.domain.services.impl;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.festivo.domain.entities.User;
import com.festivo.domain.services.interfaces.TokenService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

@Service
public class TokenServiceImpl implements TokenService {

    private static final Logger log = LoggerFactory.getLogger(TokenServiceImpl.class);

    @Value("${api.security.token.secret}")
    private String secret;

    @Value("${spring.application.name}")
    private String issuer;

    @Override
    public String generateToken(User user) {
        log.info("Starting token generation for user: '{}'", user.getUsername());
        try {
            Algorithm algorithm = Algorithm.HMAC256(secret);
            String token = JWT.create()
                    .withIssuer(issuer)
                    .withSubject(user.getEmail())
                    .withExpiresAt(genExpirationDate())
                    .sign(algorithm);
            log.info("Token generated successfully for user: '{}'", user.getUsername());
            return token;
        } catch (JWTCreationException exception) {
            log.error("Error while generating token for user: '{}'", user.getUsername());
            throw new RuntimeException("Error whiling generating token: " + exception.getMessage());
        }
    }

    @Override
    public String validateToken(String token) {
        log.info("Starting token validation.");
        try {
            Algorithm algorithm = Algorithm.HMAC256(secret);
            String subject = JWT.require(algorithm)
                    .withIssuer(issuer)
                    .build()
                    .verify(token)
                    .getSubject();
            log.info("Token validate successfully for subject: {}", subject);
            return subject;
        } catch (JWTVerificationException exception) {
            log.error("Token validation failed. Error: {}", exception.getMessage());
            return "";
        }
    }

    private Instant genExpirationDate() {
        return LocalDateTime.now().plusHours(5).toInstant(ZoneOffset.of("-03:00"));
    }
}
