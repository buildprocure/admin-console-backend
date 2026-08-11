package com.buildprocure.admin_console_backend.auth;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;

@Component
public class JwtService {

    // TODO: move this to an environment variable before deploying anywhere
    // real. For local dev, keeping it simple.
    private final SecretKey key = Keys.hmacShaKeyFor(
        "change-this-to-a-long-random-secret-at-least-32-characters".getBytes()
    );

    private final long expirationMillis = 1000 * 60 * 60 * 8; // 8 hours

    public String generateToken(String name, String email) {
        Date now = new Date();
        Date expiry = new Date(now.getTime() + expirationMillis);

        return Jwts.builder()
                .subject(email)
                .claim("name", name)
                .claim("email", email)
                .issuedAt(now)
                .expiration(expiry)
                .signWith(key)
                .compact();
    }

    public Claims validateAndGetClaims(String token) {
        return Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}
