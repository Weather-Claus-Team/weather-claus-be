package com.weatherclaus.be.websocket.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.weatherclaus.be.common.ResponseDto;
import com.weatherclaus.be.jwt.JWTUtil;
import com.weatherclaus.be.user.service.UserService;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.support.HttpSessionHandshakeInterceptor;

import java.util.Map;

@Slf4j
public class JwtHandshakeInterceptor extends HttpSessionHandshakeInterceptor {


    private final JWTUtil jwtUtil;

    public JwtHandshakeInterceptor(JWTUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    @Override
    public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response,
                                   WebSocketHandler wsHandler, Map<String, Object> attributes) throws Exception {



        if (request instanceof ServletServerHttpRequest) {


            HttpServletRequest servletRequest = ((ServletServerHttpRequest) request).getServletRequest();

            String token = servletRequest.getParameter("Second");

//            String bearerToken = servletRequest.getHeader(HEADER_STRING);  // Authorization 헤더에서 값을 가져옴

            if (token == null) {
                log.info("No token found. Allowing guest access.");
                attributes.put("username", "guest");  // 비회원 사용자로 설정
                return true;

//                response.setStatusCode(HttpStatus.BAD_REQUEST);
//                return false;  // JWT 토큰이 없거나 잘못된 경우 바로 연결 중단
            }

            token = servletRequest.getParameter("Second").split(" ")[1];


            try {
                jwtUtil.isExpired(token);
            }catch (ExpiredJwtException e) {
//                // 공통 응답 객체 생성
//                ResponseDto<?> responseDto = new ResponseDto<>("fail", "Invalid request", null,
//                        new ResponseDto.ErrorDetails("Bad Request", "Access token expired"), 401);
//
//                // ObjectMapper로 JSON 변환
//                ObjectMapper objectMapper = new ObjectMapper();
//                String jsonResponse = objectMapper.writeValueAsString(responseDto);
//
//                // 응답 설정
                response.setStatusCode(HttpStatus.BAD_REQUEST);
//                response.setContentType("application/json");
//                response.setCharacterEncoding("UTF-8");
//                response.getWriter().write(jsonResponse);

                //            프론트에서 재발급 로직해야함
                // 예외 처리 후 즉시 반환하여 필터 체인 종료
//                return false;
                attributes.put("username", "guest");  // 비회원 사용자로 설정
                return true;

            }


            // 토큰 검증
            if ((token != null) && jwtUtil.getCategory(token).equals("second")) {
                String username = jwtUtil.getUsername(token);



//                // JWT로부터 사용자 권한(ROLE)도 추출할 수 있음 (여기서는 예시로 ROLE_USER로 고정)
//                SimpleGrantedAuthority authority = new SimpleGrantedAuthority("ROLE_USER");
//
//                Authentication auth =  new UsernamePasswordAuthenticationToken(username, null, Collections.singletonList(authority));
//
//                SecurityContextHolder.getContext().setAuthentication(auth);  // Spring Security 인증 정보 설정

                attributes.put("username", username);  // 세션에 사용자 정보 저장
                return true;  // 인증 성공
            } else {
                response.setStatusCode(HttpStatus.BAD_REQUEST);
                attributes.put("username", "guest");  // 비회원 사용자로 설정

                return true;  // 인증 실패
            }
        }
        return super.beforeHandshake(request, response, wsHandler, attributes);
    }
}
