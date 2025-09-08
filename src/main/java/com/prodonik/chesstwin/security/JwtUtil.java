package com.prodonik.chesstwin.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

import java.security.Key;
import java.util.Date;
import java.util.Map;

public class JwtUtil {
    // TODO: MOVE PRIVATE VALUES INTO CONFIG!
    private static final String SECRET = "super_secret_long_key_for_jwt_chesstwin_2025_123";
    private static final long ACCESS_EXPIRATION = 1000 * 60 * 15;
    private static final long REFRESH_EXPIRATION = 1000L * 60 * 60 * 24 * 7;

    private static final Key key = Keys.hmacShaKeyFor(SECRET.getBytes());

    public static String generateAccessToken(String userId, String username) {
        return Jwts.builder()
                .setSubject(userId)
                .addClaims(Map.of("username", username))
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + ACCESS_EXPIRATION))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    public static String generateRefreshToken(String userId, String username) {
        return Jwts.builder()
                .setSubject(userId)
                .addClaims(Map.of("username", username))
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + REFRESH_EXPIRATION))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    public static Jws<Claims> parseToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token);
    }

    public static String getUserId(String token) {
        return parseToken(token).getBody().getSubject();
    }

    public static String getUsername(String token) {
        return parseToken(token).getBody().get("username", String.class);
    }
}
