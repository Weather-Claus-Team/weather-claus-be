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
import java.util.List;

@Slf4j
public class JWTFilter extends OncePerRequestFilter {

    private final JWTUtil jwtUtil;

    public JWTFilter(JWTUtil jwtUtil) {

        this.jwtUtil = jwtUtil;
    }


//    securityConfig의 petmit과 조금 다르게 . startswith여서 /경로 포함안되어야하고 조금 더 신경써야함.
    private final List<String> permitAllUrls = List.of(
            "/api/weather/forecast",
            "/api/users",
            "/api/chatList",
            "/login",
            "/health",
            "/reissue",
            "/swagger-ui",
            "/v3/api-docs",
            "/ws",
            "/api/st"
    );



    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {



        // 토큰 검증을 우회할 경로들 ( permitAll 경로 토큰 들어왔을 때 무시하기 위한 방어로직 및 필터체킹 )
        String uri = request.getRequestURI();
        if (permitAllUrls.stream().anyMatch(uri::startsWith)) {
            log.info( uri+"  =>  토큰 검증X 경로 지나갔습니다.");
            // 검증 없이 필터 체인 통과
            filterChain.doFilter(request, response);

            return;
        }

        // 헤더에서 access키에 담긴 토큰을 꺼냄
        String accessToken = request.getHeader("Authorization");

        // 토큰이 없다면 다음 필터로 넘김
        if (accessToken == null || !accessToken.startsWith("Bearer ")) {

            filterChain.doFilter(request, response);

            return;
        }

        accessToken = accessToken.substring(7);

        // 토큰 만료 여부 확인, 만료시 다음 필터로 넘기지 않음
        try {
            jwtUtil.isExpired(accessToken);
        } catch (ExpiredJwtException e) {

            // 공통 응답 객체 생성
            ResponseDto<?> responseDto = new ResponseDto<>("fail", "Expired access Token", null,
                    new ResponseDto.ErrorDetails("Expired Token Request", "Access token expired"), 401);

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


        // 토큰이 access인지 확인 (발급시 페이로드에 명시)
        String category = jwtUtil.getCategory(accessToken);

        if (!category.equals("access")) {

            response.setStatus(HttpStatus.BAD_REQUEST.value());

            return;

        }


        // username, role 값을 획득
        String username = jwtUtil.getUsername(accessToken);


        String role = jwtUtil.getRole(accessToken);
        Role enumRole = role.equals("ROLE_USER") ? Role.USER : Role.ADMIN;

        //userEntity를 생성하여 값 set
        User user = User.builder()
                .username(username)
                .role(enumRole)
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
