package project.restaurantmanagement.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import project.restaurantmanagement.model.Constants.UserType;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;


@Slf4j
@Component
@RequiredArgsConstructor
public class TokenProvider {

    @Value("${spring.jwt.secret}")
    private String secretKey;
    private static final long TOKEN_EXPIRE_TIME = 1000 * 60 * 60; // 1 hour
    private static final String TOKEN_PREFIX = "Bearer ";

    public String generateToken(Long id, String email, UserType userType) {
        Date now = new Date();
        Date expirationDate = new Date(now.getTime() + TOKEN_EXPIRE_TIME);

        return Jwts.builder()
                .claim("id", id.toString())
                .claim("roles", userType)
                .subject(email)
                .issuedAt(now)
                .expiration(expirationDate)
                .signWith(getSignKey(secretKey), Jwts.SIG.HS512)
                .compact();
    }

    public boolean validateToken(String token) {
        try {

            Claims claims = parseClaims(token);

            return !claims.getExpiration().before(new Date());
        } catch (JwtException e) {
            log.error("Token validation error: {}", e.getMessage());
            return false;
        }
    }

    private SecretKey getSignKey(String secretKey) {

        byte[] keyBytes = secretKey.getBytes(StandardCharsets.UTF_8);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    public Claims parseClaims(String token) {
        try {
            return Jwts.parser()
                    .verifyWith(getSignKey(secretKey))
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
        } catch (JwtException e) {
            log.error("Error parsing claims from token: {}", e.getMessage());
            return null;
        }
    }

    public String getUsername(String jwt) {
        return parseClaims(jwt).getSubject();
    }

    public String getToken(String header) {

        return header.substring(TOKEN_PREFIX.length());
    }

    public Long getId(String token) {

        Claims claims = parseClaims(token);
        return Long.parseLong(claims.get("id").toString());
    }
}
