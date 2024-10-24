package com.weatherclaus.be.websocket.config;

import com.weatherclaus.be.jwt.JWTUtil;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.support.HttpSessionHandshakeInterceptor;

import java.util.Collections;
import java.util.Map;

@Slf4j
public class JwtHandshakeInterceptor extends HttpSessionHandshakeInterceptor {


    private final String HEADER_STRING = "Authorization";  // 헤더에서 토큰이 담겨있는 필드
    private final String TOKEN_PREFIX = "Bearer ";         // 토큰 앞에 붙는 접두사 "Bearer "
    private final JWTUtil jwtUtil;

    public JwtHandshakeInterceptor(JWTUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    @Override
    public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response,
                                   WebSocketHandler wsHandler, Map<String, Object> attributes) throws Exception {




        String token = null;
        // HTTP 헤더에서 Authorization 헤더를 통해 JWT 토큰을 추출
        if (request instanceof ServletServerHttpRequest) {
            HttpServletRequest servletRequest = ((ServletServerHttpRequest) request).getServletRequest();

            String bearerToken = servletRequest.getHeader(HEADER_STRING);  // Authorization 헤더에서 값을 가져옴

            if (bearerToken == null || !bearerToken.startsWith(TOKEN_PREFIX)) {
                response.setStatusCode(HttpStatus.UNAUTHORIZED);
                return false;  // JWT 토큰이 없거나 잘못된 경우 바로 연결 중단
            }

                token = bearerToken.substring(TOKEN_PREFIX.length());  // "Bearer " 접두사를 제거하고 토큰만 반환


            // 토큰 검증
            if ((token != null && !jwtUtil.isExpired(token)) && jwtUtil.getCategory(token).equals("access")) {
                String username = jwtUtil.getUsername(token);



                // JWT로부터 사용자 권한(ROLE)도 추출할 수 있음 (여기서는 예시로 ROLE_USER로 고정)
                SimpleGrantedAuthority authority = new SimpleGrantedAuthority("ROLE_USER");

                Authentication auth =  new UsernamePasswordAuthenticationToken(username, null, Collections.singletonList(authority));

                SecurityContextHolder.getContext().setAuthentication(auth);  // Spring Security 인증 정보 설정

                attributes.put("username", username);  // 세션에 사용자 정보 저장
                return true;  // 인증 성공
            } else {
                response.setStatusCode(HttpStatus.UNAUTHORIZED);
                return false;  // 인증 실패
            }
        }
        return super.beforeHandshake(request, response, wsHandler, attributes);
    }
}
