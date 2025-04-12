package com.triphub.auth.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;

@Component
public class JwtProvider {
    private final Key key;
    private final long accessTokenValidity;
    private final long refreshTokenValidity;
    public JwtProvider(@Value("${jwt.secret}") String secret,
                       @Value("${jwt.access-exp}") long accessTokenValidity,
                       @Value("${jwt.refresh-exp}") long refreshTokenValidity) {
        this.key = Keys.hmacShaKeyFor(secret.getBytes());;
        this.accessTokenValidity = accessTokenValidity;
        this.refreshTokenValidity = refreshTokenValidity;
    }

    public String generateAccessToken(String email, String role) {
        return generateToken(email, role, accessTokenValidity);
    }

    public String generateRefreshToken(String email) {
        return generateToken(email, null, refreshTokenValidity);
    }

    private String generateToken(String email, String role, long expireMs) {
        JwtBuilder builder = Jwts.builder()
                .setSubject(email)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + expireMs))
                .signWith(key, SignatureAlgorithm.HS256);

        if (role != null) {
            builder.claim("role", role);
        }

        return builder.compact();
    }

    public Claims parseToken(String token) {
        return Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token).getBody();
    }

    public boolean validateToken(String token) {
        try {
            parseToken(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }

    public long getExpirationFromToken(String token) {
        Claims claims = parseToken(token);
        return claims.getExpiration().getTime();
    }
}
