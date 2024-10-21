package com.weatherclaus.be.jwt;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.weatherclaus.be.common.ResponseDto;
import com.weatherclaus.be.user.entity.Role;
import com.weatherclaus.be.user.entity.User;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Slf4j
public class JWTFilter extends OncePerRequestFilter {

    private final JWTUtil jwtUtil;

    public JWTFilter(JWTUtil jwtUtil) {

        this.jwtUtil = jwtUtil;
    }

//    @Override
//    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
//        // 예외 처리할 경로들 설정
//        String path = request.getRequestURI();
//        String method = request.getMethod();
//
//
//        // 여기에 permitAll 경로 추가
//        return path.equals("/login")
//                || path.equals("/logout")
//                || path.equals("/reissue")
//                || path.startsWith("/api/users")
//                || path.startsWith("/api/weather/forecast")
//                || path.startsWith("/health");
//
//    }



    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        // 헤더에서 access키에 담긴 토큰을 꺼냄
        String accessToken = request.getHeader("Authorization");

        // 토큰이 없다면 다음 필터로 넘김
        if (accessToken == null || !accessToken.startsWith("Bearer ")) {

            filterChain.doFilter(request, response);

            return;
        }

        accessToken = accessToken.substring(7);


        // 토큰이 access인지 확인 (발급시 페이로드에 명시)
        String category = jwtUtil.getCategory(accessToken);

        if (!category.equals("access")) {

            response.setStatus(HttpStatus.BAD_REQUEST.value());

            return;

        }


        // 토큰 만료 여부 확인, 만료시 다음 필터로 넘기지 않음
        try {
            jwtUtil.isExpired(accessToken);
        } catch (ExpiredJwtException e) {
            log.info("access token expired");

            // 공통 응답 객체 생성
            ResponseDto<?> responseDto = new ResponseDto<>("fail", "Invalid request", null,
                    new ResponseDto.ErrorDetails("Bad Request", "Access token expired"), 401);

            // ObjectMapper로 JSON 변환
            ObjectMapper objectMapper = new ObjectMapper();
            String jsonResponse = objectMapper.writeValueAsString(responseDto);

            // 응답 설정
            response.setStatus(HttpStatus.UNAUTHORIZED.value());
            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");
            response.getWriter().write(jsonResponse);

            //            프론트에서 재발급 로직해야함
            // 예외 처리 후 즉시 반환하여 필터 체인 종료
            return;

        }




        // username, role 값을 획득
        String username = jwtUtil.getUsername(accessToken);
        String role = jwtUtil.getRole(accessToken);

        //userEntity를 생성하여 값 set
        User user = User.builder()
                .username(username)
                .role(Role.USER)
                .password("temp").build();


        //UserDetails에 회원 정보 객체 담기
        CustomUserDetails customUserDetails = new CustomUserDetails(user);

        //스프링 시큐리티 인증 토큰 생성
        Authentication authToken = new UsernamePasswordAuthenticationToken(customUserDetails, null, customUserDetails.getAuthorities());
        //세션에 사용자 등록
        SecurityContextHolder.getContext().setAuthentication(authToken);

        filterChain.doFilter(request, response);
    }
}
