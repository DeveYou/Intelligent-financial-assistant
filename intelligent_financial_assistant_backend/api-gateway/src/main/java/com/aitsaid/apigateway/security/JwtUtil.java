package com.aitsaid.apigateway.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.io.DecodingException;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Utilitaire JWT pour le Gateway - Validation uniquement (pas de génération)
 */
@Component
public class JwtUtil {

    @Value("${jwt.secret}")
    private String secret;

    private final AtomicReference<SecretKey> cachedKey = new AtomicReference<>();

    private boolean looksLikeBase64(String value) {
        if (value == null) return false;
        String v = value.trim();
        return v.length() % 4 == 0 && v.matches("[A-Za-z0-9+/=]+");
    }

    private SecretKey getSigningKey() {
        SecretKey existing = cachedKey.get();
        if (existing != null) return existing;

        String trimmed = secret == null ? "" : secret.trim();
        byte[] keyBytes;
        try {
            if (looksLikeBase64(trimmed)) {
                keyBytes = Decoders.BASE64.decode(trimmed);
            } else {
                keyBytes = trimmed.getBytes(StandardCharsets.UTF_8);
            }
        } catch (DecodingException | IllegalArgumentException e) {
            keyBytes = trimmed.getBytes(StandardCharsets.UTF_8);
        }
        if (keyBytes.length < 32) {
            throw new IllegalStateException("JWT secret must be at least 256 bits (32 bytes). Current length=" + keyBytes.length);
        }
        SecretKey key = Keys.hmacShaKeyFor(keyBytes);
        cachedKey.compareAndSet(null, key);
        return key;
    }

    public Claims extractAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    public String extractUsername(String token) {
        return extractAllClaims(token).getSubject();
    }

    public List<String> extractRoles(String token) {
        Claims claims = extractAllClaims(token);
        return claims.get("roles", List.class);
    }

    public boolean isTokenExpired(String token) {
        return extractAllClaims(token).getExpiration().before(new Date());
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(getSigningKey())
                    .build()
                    .parseClaimsJws(token);
            return !isTokenExpired(token);
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }
}