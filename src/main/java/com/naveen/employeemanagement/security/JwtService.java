
package com.naveen.employeemanagement.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.function.Function;

@Service
public class JwtService {

    @Value("${app.jwt.secret}")
    private String secret;

    @Value("${app.jwt.expiration}")
    private long expiration;

    private static final long PASSWORD_RESET_EXPIRATION = 5 * 60 * 1000;

    private SecretKey getSigningKey() {
        byte[] keyBytes = Decoders.BASE64.decode(secret);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    // Normal Login Access Token
    public String generateToken(String email, String role) {

        return Jwts.builder()
                .subject(email)
                .claim("role", role)
                .claim("tokenType", "ACCESS")
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(getSigningKey())
                .compact();
    }

    // Password Reset Token
    public String generatePasswordResetToken(String email) {

        return Jwts.builder()
                .subject(email)
                .claim("tokenType", "PASSWORD_RESET")
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis()
                        + PASSWORD_RESET_EXPIRATION))
                .signWith(getSigningKey())
                .compact();
    }

    public String extractEmail(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public String extractRole(String token) {
        return extractAllClaims(token).get("role", String.class);
    }

    // Validate normal Access Token only
    public boolean isTokenValid(String token, String email) {

        try {
            Claims claims = extractAllClaims(token);

            String tokenEmail = claims.getSubject();
            String tokenType = claims.get("tokenType", String.class);

            return email.equals(tokenEmail)
                    && "ACCESS".equals(tokenType)
                    && claims.getExpiration().after(new Date());

        } catch (JwtException | IllegalArgumentException exception) {
            return false;
        }
    }

    // Validate Password Reset Token only
    public boolean isPasswordResetTokenValid(String token, String email) {

        try {
            Claims claims = extractAllClaims(token);

            String tokenEmail = claims.getSubject();
            String tokenType = claims.get("tokenType", String.class);

            return email.equals(tokenEmail)
                    && "PASSWORD_RESET".equals(tokenType)
                    && claims.getExpiration().after(new Date());

        } catch (JwtException | IllegalArgumentException exception) {
            return false;
        }
    }

    private <T> T extractClaim(
            String token,
            Function<Claims, T> claimsResolver) {

        Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    private Claims extractAllClaims(String token) {

        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}
