package com.aitsaid.apigateway.filter;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.io.DecodingException;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Component
public class JwtUtil {

    @Value("${jwt.secret}")
    private String secret;

    private boolean looksLikeBase64(String value) {
        if (value == null) return false;
        String v = value.trim();
        return v.length() % 4 == 0 && v.matches("[A-Za-z0-9+/=]+");
    }

    private SecretKey getSigningKey() {
        byte[] keyBytes;
        String trimmed = secret == null ? "" : secret.trim();
        try {
            if (looksLikeBase64(trimmed)) {
                keyBytes = Decoders.BASE64.decode(trimmed);
            } else {
                keyBytes = trimmed.getBytes(StandardCharsets.UTF_8);
            }
        } catch (IllegalArgumentException | DecodingException e) {
            keyBytes = trimmed.getBytes(StandardCharsets.UTF_8);
        }
        return Keys.hmacShaKeyFor(keyBytes);
    }

    public Claims extractAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    public boolean isTokenValid(String token) {
        try {
            return !isTokenExpired(token);
        } catch (Exception e) {
            System.out.println("DEBUG JWT: Token validation failed: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    private boolean isTokenExpired(String token) {
        return extractAllClaims(token).getExpiration().before(new Date());
    }
}
