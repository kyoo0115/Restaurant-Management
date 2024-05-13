package project.restaurantmanagement.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import project.restaurantmanagement.model.type.UserType;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

/**
 * JWT를 생성하고 검증하는 클래스입니다.
 */

@Slf4j
@Component
@RequiredArgsConstructor
public class TokenProvider {

    @Value("${spring.jwt.secret}")
    private String secretKey;
    private static final long TOKEN_EXPIRE_TIME = 1000 * 60 * 60; //토큰 만료 시간 (1시간)
    private static final String TOKEN_PREFIX = "Bearer ";

    /**
     * 새로운 토큰을 생성합니다.
     * @param id 사용자 ID
     * @param email 사용자 이메일
     * @param userType 사용자 유형
     * @return 생성된 JWT 토큰
     */
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

    /**
     * 제공된 토큰이 유효한지 검증합니다.
     * @param token 검증할 JWT 토큰
     * @return 토큰이 유효하면 true, 그렇지 않으면 false
     */
    public boolean validateToken(String token) {
        try {

            Claims claims = parseClaims(token);

            return !claims.getExpiration().before(new Date());
        } catch (JwtException e) {
            log.error("Token validation error: {}", e.getMessage());
            return false;
        }
    }

    /**
     * 토큰 서명을 위한 키를 생성합니다.
     * @return 생성된 SecretKey
     */
    private SecretKey getSignKey(String secretKey) {

        byte[] keyBytes = secretKey.getBytes(StandardCharsets.UTF_8);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    /**
     * JWT에서 클레임을 파싱합니다.
     * @param token 파싱할 JWT 토큰
     * @return 추출된 클레임
     */
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

    /**
     * JWT에서 사용자 이메일(주제)을 추출합니다.
     * @param jwt 파싱할 JWT 토큰
     * @return 사용자 이메일
     */
    public String getUsername(String jwt) {
        return parseClaims(jwt).getSubject();
    }

    /**
     * HTTP 헤더에서 JWT 토큰을 추출합니다.
     * @param header HTTP 요청 헤더
     * @return 추출된 토큰
     */
    public String getToken(String header) {

        return header.substring(TOKEN_PREFIX.length());
    }

    /**
     * JWT에서 사용자 ID를 추출합니다.
     * @param token 파싱할 JWT 토큰
     * @return 사용자 ID
     */
    public Long getId(String token) {

        Claims claims = parseClaims(token);
        return Long.parseLong(claims.get("id").toString());
    }

    public UserType getUserType(String token) {
        Claims claims = this.parseClaims(token);

        return UserType.valueOf(claims.get("roles", String.class));
    }
}
