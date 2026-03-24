package com.example.practicaspringboot.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Base64;
import java.util.Date;

@Component
public class JwtUtil {

    private final SecretKey secretKey;

    public JwtUtil(@Value("${jwt.secret}") String secret) {
        byte[] keyBytes = Base64.getDecoder().decode(secret);
        this.secretKey = Keys.hmacShaKeyFor(keyBytes);
    }

    /** Generate a JWT whose expiration matches the mock endpoint's expiresAt. */
    public String generateToken(String subject, LocalDateTime expiresAt) {
        Date expDate = Date.from(expiresAt.atZone(ZoneId.systemDefault()).toInstant());
        return Jwts.builder()
                .subject(subject)
                .issuedAt(new Date())
                .expiration(expDate)
                .signWith(secretKey)
                .compact();
    }

    /** Validate a token and return its subject, or null if invalid/expired. */
    public String validateAndGetSubject(String token) {
        try {
            return Jwts.parser()
                    .verifyWith(secretKey)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload()
                    .getSubject();
        } catch (JwtException | IllegalArgumentException e) {
            return null;
        }
    }

    public boolean isValid(String token) {
        return validateAndGetSubject(token) != null;
    }
}
