package com.royalhotels.backend;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

/**
 * JwtTokenService - Generates and validates cryptographic JWT tokens for Admin authentication.
 *
 * OOP Concept: Service layer encapsulation. All token generation, signing,
 * parsing, and claim extraction are encapsulated in this service.
 */
@Service
public class JwtTokenService {

    private final SecretKey signingKey;
    private final long expirationMs;

    public JwtTokenService(
            @Value("${jwt.secret:RoyalHotelsSuperSecure256BitSecretKeyForAdminAuthJwtTokens2026!}") String secret,
            @Value("${jwt.expiration-ms:86400000}") long expirationMs) {
        
        // Ensure secret is at least 256 bits (32 bytes)
        byte[] keyBytes = secret.getBytes(StandardCharsets.UTF_8);
        if (keyBytes.length < 32) {
            byte[] padded = new byte[32];
            System.arraycopy(keyBytes, 0, padded, 0, Math.min(keyBytes.length, 32));
            this.signingKey = Keys.hmacShaKeyFor(padded);
        } else {
            this.signingKey = Keys.hmacShaKeyFor(keyBytes);
        }
        this.expirationMs = expirationMs;
    }

    /**
     * Generates a signed JWT token for the authenticated admin.
     */
    public String generateToken(String username, String fullName, String role) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + expirationMs);

        return Jwts.builder()
                .subject(username)
                .claim("fullName", fullName)
                .claim("role", role)
                .issuedAt(now)
                .expiration(expiryDate)
                .signWith(signingKey)
                .compact();
    }

    /**
     * Validates token signature and expiration.
     */
    public boolean validateToken(String token) {
        try {
            Jwts.parser()
                    .verifyWith(signingKey)
                    .build()
                    .parseSignedClaims(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }

    /**
     * Extracts the subject (username) from the token.
     */
    public String extractUsername(String token) {
        return extractClaims(token).getSubject();
    }

    /**
     * Extracts the role claim from the token.
     */
    public String extractRole(String token) {
        Object role = extractClaims(token).get("role");
        return role != null ? role.toString() : "ROLE_ADMIN";
    }

    /**
     * Extracts the full name claim from the token.
     */
    public String extractFullName(String token) {
        Object fullName = extractClaims(token).get("fullName");
        return fullName != null ? fullName.toString() : "";
    }

    public long getExpirationMs() {
        return expirationMs;
    }

    private Claims extractClaims(String token) {
        return Jwts.parser()
                .verifyWith(signingKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}
