package com.sumit.taskmanager.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;

@Component
public class JwtUtil {

    // 🔐 Use a strong base64 encoded secret (at least 32+ chars)
    private static final String SECRET_KEY =
            "VGhpc0lzQVNlY3JldEtleUZvckpXVFRva2VuMTIzNDU2"; // example

    // ⏳ Token validity (e.g., 1 day)
    private static final long JWT_EXPIRATION = 1000 * 60 * 60 * 24;

    // ===============================
    // 🔹 GENERATE TOKEN
    // ===============================
    public String generateToken(UserDetails userDetails) {
        return Jwts.builder()
                .setSubject(userDetails.getUsername())
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + JWT_EXPIRATION))
                .signWith(getSignKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    // ===============================
    // 🔹 EXTRACT USERNAME
    // ===============================
    public String extractUsername(String token) {
        return extractAllClaims(token).getSubject();
    }

    // ===============================
    // 🔹 VALIDATE TOKEN
    // ===============================
    public boolean validateToken(String token, UserDetails userDetails) {
        final String username = extractUsername(token);

        return (username.equals(userDetails.getUsername())
                && !isTokenExpired(token));
    }

    // ===============================
    // 🔹 CHECK EXPIRATION
    // ===============================
    public boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    // ===============================
    // 🔹 EXTRACT EXPIRATION
    // ===============================
    public Date extractExpiration(String token) {
        return extractAllClaims(token).getExpiration();
    }

    // ===============================
    // 🔹 EXTRACT ALL CLAIMS
    // ===============================
    private Claims extractAllClaims(String token) {
        return Jwts
                .parserBuilder()
                .setSigningKey(getSignKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    // ===============================
    // 🔹 SECRET KEY
    // ===============================
    private Key getSignKey() {
        byte[] keyBytes = Decoders.BASE64.decode(SECRET_KEY);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}