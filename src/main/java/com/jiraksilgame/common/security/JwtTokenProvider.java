package com.jiraksilgame.common.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;

/**
 * JWT 토큰 생성·검증 컴포넌트
 */
@Component
public class JwtTokenProvider {

    private final Key key;
    private final long jwtExpirationInMs;

    /**
     * 비밀키와 만료시간으로 초기화
     * <p>HS256 서명에 사용할 HMAC 키를 구성하며 secret 바이트 길이가 충분해야 함</p>
     * 
     * @param secret 서명 비밀
     * @param jwtExpirationInMs 만료 시간(ms)
     */
    public JwtTokenProvider(
        @Value("${jwt.secret}") String secret,
        @Value("${jwt.expiration}") long jwtExpirationInMs
    ) {
        this.key = Keys.hmacShaKeyFor(secret.getBytes());
        this.jwtExpirationInMs = jwtExpirationInMs;
    }

    /**
     * 사용자 ID로 액세스 토큰 생성
     * <p>subject에 userId를 저장하고 HS256으로 서명</p>
     * 
     * @param userId 사용자 식별자
     * @return 생성된 JWT
     */
    public String generateToken(String userId) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + jwtExpirationInMs);

        return Jwts.builder()
                .setSubject(userId)
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    /**
     * 토큰에서 사용자 ID 추출
     * 
     * @param token JWT
     * @return subject로 저장된 사용자 ID
     * @throws JwtException 서명/형식/만료 오류 시
     * @throws IllegalArgumentException 토큰이 null/빈 문자열인 경우
     */
    public String getUserIdFromToken(String token) {
        return parseClaims(token).getSubject();
    }

    /**
     * 토큰 유효성 검증
     * <p>서명·형식·만료를 검증하며 예외 발생 시 false 반환</p>
     * 
     * @param token JWT
     * @return 유효하면 true
     */
    public boolean validateToken(String token) {
        try {
            parseClaims(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }

    /**
     * 토큰의 클레임 파싱
     * 
     * @param token JWT
     * @return 파싱된 Claims
     * @throws JwtException 서명/형식/만료 오류 시
     * @throws IllegalArgumentException 토큰이 null/빈 문자열인 경우
     */
    private Claims parseClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
}
