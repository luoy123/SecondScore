package com.secondscore.common.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Component
@RequiredArgsConstructor
public class JwtTokenProvider {

    private final JwtProperties jwtProperties;
    private SecretKey secretKey;

    @PostConstruct
    public void init() {
        this.secretKey = Keys.hmacShaKeyFor(jwtProperties.getSecret().getBytes(StandardCharsets.UTF_8));
    }

    public String generateToken(Long userId, String username, List<String> roleCodes) {
        Instant now = Instant.now();
        Instant expireAt = now.plusSeconds(jwtProperties.getExpirationMinutes() * 60);
        return Jwts.builder()
                .subject(username)
                .claim("uid", userId)
                .claim("roles", roleCodes)
                .issuedAt(Date.from(now))
                .expiration(Date.from(expireAt))
                .signWith(secretKey)
                .compact();
    }

    public Claims parseClaims(String token) {
        return Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    public boolean isTokenValid(String token) {
        try {
            parseClaims(token);
            return true;
        } catch (Exception ex) {
            return false;
        }
    }

    public Long getUserId(String token) {
        Object uid = parseClaims(token).get("uid");
        if (uid instanceof Integer value) {
            return value.longValue();
        }
        if (uid instanceof Long value) {
            return value;
        }
        return Long.valueOf(uid.toString());
    }

    public String getUsername(String token) {
        return parseClaims(token).getSubject();
    }

    @SuppressWarnings("unchecked")
    public List<String> getRoleCodes(String token) {
        Object rolesObj = parseClaims(token).get("roles");
        if (!(rolesObj instanceof List<?> list)) {
            return List.of();
        }
        List<String> result = new ArrayList<>();
        for (Object obj : list) {
            result.add(String.valueOf(obj));
        }
        return result;
    }
}

