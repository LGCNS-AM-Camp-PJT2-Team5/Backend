package com.team9.jobbotdari.common.jwt;

import java.util.Date;
import java.util.stream.Collectors;

import javax.crypto.SecretKey;

import com.team9.jobbotdari.entity.User;
import com.team9.jobbotdari.security.CustomUserDetails;
import org.springframework.core.env.Environment;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class JwtUtils {
    private SecretKey hmacKey;
    private Long expirationTime;
    private String issuer;

    public JwtUtils(Environment env) {
        this.hmacKey = Keys.hmacShaKeyFor(env.getProperty("jwt.secret-key").getBytes());
        this.expirationTime = Long.parseLong(env.getProperty("jwt.access_expiration"));
        this.issuer = env.getProperty("jwt.issuer");
    }

    public String generateToken(UserDetails userDetails) {
        Date now = new Date();

        // userId 가져오기 (CustomUserDetails로 캐스팅)
        Long userId = null;
        if (userDetails instanceof CustomUserDetails) {
            userId = ((CustomUserDetails) userDetails).getUser().getId();
        }

        String jwtToken = Jwts.builder()
                .claim("userId", userId)
                .claim("username", userDetails.getUsername())
                .claim("role", userDetails.getAuthorities().stream()
                        .map(GrantedAuthority::getAuthority)
                        .collect(Collectors.toList()))
                .subject(userDetails.getUsername()) // 토큰의 subject(사용자 정보)
                .id(String.valueOf(userDetails.hashCode())) // 토큰 ID
                .issuedAt(now) // 토큰 발급 시간
                .expiration(new Date(now.getTime() + this.expirationTime)) // 만료 시간 설정
                .issuer(this.issuer) // Issuer(발급자)
                .signWith(this.hmacKey, Jwts.SIG.HS256) // HMAC 서명
                .compact(); // 토큰 생성
        log.debug(jwtToken);

        return jwtToken;
    }

    private Claims getAllClaimsFromToken(String token) {
        Jws<Claims> jwt = Jwts.parser()
                .verifyWith(this.hmacKey)
                .build()
                .parseSignedClaims(token);
        return jwt.getPayload();
    }

    private Date getExpirationDateFromToken(String token) {
        Claims claims = getAllClaimsFromToken(token);
        return claims.getExpiration();
    }

    private boolean isTokenExpired(String token) {
        Date expiration = getExpirationDateFromToken(token);
        return expiration.before(new Date());
    }

    public String getSubjectFromToken(String token) {
        Claims claims = getAllClaimsFromToken(token);
        return claims.getSubject();
    }

    public boolean validateToken(String token, User user) {
        // 토큰 유효기간 체크
        if (isTokenExpired(token)) {
            return false;
        }

        // 토큰 내용을 검증
        String subject = getSubjectFromToken(token);
        String username = user.getUsername();

        return subject != null && username != null && subject.equals(username);
    }
}