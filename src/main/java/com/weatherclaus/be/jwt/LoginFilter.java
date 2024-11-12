package com.weatherclaus.be.jwt;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.io.IOException;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.TimeUnit;


@RequiredArgsConstructor
@Slf4j
public class LoginFilter extends UsernamePasswordAuthenticationFilter {

    private final AuthenticationManager authenticationManager;
    private final ObjectMapper objectMapper = new ObjectMapper();

    private final JWTUtil jwtUtil;

    private final RedisTemplate<String, Object> redisTemplate;




    // Post /login username,password가 기본 경로
    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {


        // JSON 요청 처리
        if ("application/json".equalsIgnoreCase(request.getContentType())) {
            try {
                // JSON으로부터 username과 password를 추출
                Map<String, String> authRequest = objectMapper.readValue(request.getReader(), Map.class);
                String username = authRequest.get("username");
                String password = authRequest.get("password");

                if (username == null || password == null) {
                    throw new AuthenticationException("Username or Password missing") {};
                }

                // 인증을 위한 토큰 생성 및 인증 시도
                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(username, password);
                return authenticationManager.authenticate(authToken);

            } catch (IOException e) {
                throw new AuthenticationException("Error parsing JSON request") {};
            }
        }

        // JSON이 아닌 경우에는 기본 폼 방식으로 처리
        return super.attemptAuthentication(request, response);
    }



    //로그인 성공시 실행하는 메소드 (여기서 JWT를 발급하면 됨)
    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authentication) {


        CustomUserDetails customUserDetails = (CustomUserDetails) authentication.getPrincipal();
        String username = customUserDetails.getUsername();
        String role = extractRole(authentication);


        // 토큰 생성
        String access = createAccessToken(username, role);
        String refresh = createRefreshToken(username, role);


        // 2. 서버의 Redis에 Refresh Token 저장 (username을 키로 사용)
        storeRefreshTokenInRedis(username, refresh);

        /**
         * Authorization: Bearer <jwtToken> → 엑세스 토큰을 헤더에 보낼떄
         */
        //응답 설정
        setResponseTokens(response, access, refresh);

    }

    private String extractRole(Authentication authentication) {
        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
        Iterator<? extends GrantedAuthority> iterator = authorities.iterator();
        GrantedAuthority auth = iterator.next();
        return auth.getAuthority();
    }

    private String createAccessToken(String username, String role) {
        return jwtUtil.createAccessJwt(username, role);
    }

    private String createRefreshToken(String username, String role) {
        return jwtUtil.createRefreshJwt(username, role);
    }

    private void storeRefreshTokenInRedis(String username, String refreshToken) {
        redisTemplate.opsForValue().set(username, refreshToken, jwtUtil.getRefreshTokenExpiredMs(), TimeUnit.MILLISECONDS);
    }

    private void setResponseTokens(HttpServletResponse response, String accessToken, String refreshToken) {
        response.setHeader("Authorization", "Bearer " + accessToken); // "Bearer " +


        // 만료 시간 (초 단위)
        int maxAge = (int)(86400000 / 1000);

        // Set-Cookie 헤더를 사용하여 쿠키 속성 설정
        String cookieHeader = "refresh=" + refreshToken
                + "; Max-Age=" + maxAge
                + "; Path=/"
                + "; Domain=api.mungwithme.com"  // **백엔드 도메인으로 설정**
                + "; HttpOnly"                   // 클라이언트에서 접근 불가 (보안)
                + "; Secure"                     // HTTPS에서만 쿠키 전송
                + "; SameSite=None";             // 크로스 도메인에서 쿠키 전송 허용
        
        response.addHeader("Set-Cookie", cookieHeader);

//        response.addCookie(jwtUtil.createCookie("refresh", refreshToken));
//        response.addHeader("Set-Cookie","SameSite=None");
        response.setStatus(HttpStatus.OK.value());
    }


    //로그인 실패시 실행하는 메소드
    @Override
    protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response, AuthenticationException failed) {

        //로그인 실패시 400 응답 코드 반환
        response.setStatus(400);
    }


}