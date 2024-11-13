package com.weatherclaus.be.jwt;

import io.jsonwebtoken.Jwts;
import jakarta.servlet.http.Cookie;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.concurrent.TimeUnit;

@Component
public class JWTUtil {

    @Value("${JWT_ACCESS_EXPIREDMS}")
    private Long accessTokenExpiredMs;

    @Value("${JWT_REFRESH_EXPIREDMS}")
    private Long refreshTokenExpiredMs;

    @Value("${JWT_SECOND_EXPIREDMS}")
    private Long secondTokenExpiredMs;

    private SecretKey secretKey;


    public JWTUtil(
            @Value("${SPRING_JWT_SECRET}") String secret
    ) {
        secretKey = new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), Jwts.SIG.HS256.key().build().getAlgorithm());
    }

    public Long getAccessTokenExpiredMs() {
        return accessTokenExpiredMs;
    }

    public Long getRefreshTokenExpiredMs() {
        return refreshTokenExpiredMs;
    }


    public String getUsername(String token) {

        return Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token).getPayload().get("username", String.class);
    }


    public String getRole(String token) {

        return Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token).getPayload().get("role", String.class);
    }


    public Boolean isExpired(String token) {

        return Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token).getPayload().getExpiration().before(new Date());
    }

    public String getCategory(String token) {

        return Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token).getPayload().get("category", String.class);
    }





    public String createAccessJwt(String username, String role) {

        return Jwts.builder()
                .claim("category", "access")
                .claim("username", username)
                .claim("role", role)
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + accessTokenExpiredMs))
                .signWith(secretKey)
                .compact();
    }

    public String createRefreshJwt(String username, String role) {

        return Jwts.builder()
                .claim("category", "refresh")
                .claim("username", username)
                .claim("role", role)
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + refreshTokenExpiredMs))
                .signWith(secretKey)
                .compact();
    }

    public String createSecondJwt(String username) {

        return Jwts.builder()
                .claim("category", "second")
                .claim("username", username)
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + secondTokenExpiredMs))
                .signWith(secretKey)
                .compact();
    }

    public Cookie createCookie(String key, String value) {
        Cookie cookie = new Cookie(key, value);
        cookie.setMaxAge((int)(refreshTokenExpiredMs / 1000));  // 밀리초를 초로 변환
        cookie.setPath("/");
        cookie.setHttpOnly(true);
        cookie.setSecure(true);

        return cookie;
    }


}