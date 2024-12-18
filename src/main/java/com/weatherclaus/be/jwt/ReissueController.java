package com.weatherclaus.be.jwt;

import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.TimeUnit;

@RestController
@RequiredArgsConstructor
public class ReissueController {

    private final JWTUtil jwtUtil;
    private final RedisTemplate<String, Object> redisTemplate;


    @PostMapping("/reissue")
    public ResponseEntity<?> reissue(HttpServletRequest request, HttpServletResponse response) {

        //get refresh token
        String refresh = null;
        Cookie[] cookies = request.getCookies();
        for (Cookie cookie : cookies) {

            if (cookie.getName().equals("refresh")) {

                refresh = cookie.getValue();
            }
        }

        if (refresh == null) {

            //response status code
            return new ResponseEntity<>("refresh token null", HttpStatus.BAD_REQUEST);
        }

        //expired check
        try {
            jwtUtil.isExpired(refresh);
        } catch (ExpiredJwtException e) {

            //response status code
            return new ResponseEntity<>("refresh token expired", HttpStatus.BAD_REQUEST);
        }

        // 토큰이 refresh인지 확인 (발급시 페이로드에 명시)
        String category = jwtUtil.getCategory(refresh);

        if (!category.equals("refresh")) {

            //response status code
            return new ResponseEntity<>("invalid refresh token", HttpStatus.BAD_REQUEST);
        }

        String username = jwtUtil.getUsername(refresh);
        String role = jwtUtil.getRole(refresh);

        // 레디스에 존재하는지 체크.
        String storedRefreshToken = (String) redisTemplate.opsForValue().get(username);
        if (storedRefreshToken == null || !storedRefreshToken.equals(refresh)) {
            return new ResponseEntity<>("Invalid refresh token", HttpStatus.BAD_REQUEST);
        }




        //make new JWT
        String newAccess = jwtUtil.createAccessJwt(username, role);
        String newRefresh = jwtUtil.createRefreshJwt(username, role);



        // 레디스에 덮어쓰기.
        redisTemplate.opsForValue().set(username, newRefresh, jwtUtil.getRefreshTokenExpiredMs(), TimeUnit.MILLISECONDS);


        //response
        response.setHeader("Authorization", "Bearer "+ newAccess); // "Bearer "+

        // 만료 시간 (초 단위)
        int maxAge = (int)(86400000 / 1000);

        // Set-Cookie 헤더를 사용하여 쿠키 속성 설정
        String cookieHeader = "refresh=" + newRefresh
                + "; Max-Age=" + maxAge
                + "; Path=/"
//                + "; Domain=api.mungwithme.com"  // **백엔드 도메인으로 설정**
                + "; HttpOnly"                   // 클라이언트에서 접근 불가 (보안)
                + "; Secure"                     // HTTPS에서만 쿠키 전송
                + "; SameSite=None";             // 크로스 도메인에서 쿠키 전송 허용
        response.addHeader("Set-Cookie", cookieHeader);

//        response.addCookie(jwtUtil.createCookie("refresh", newRefresh));
//        response.addHeader("Set-Cookie", "refresh=" + newRefresh + "; Path=/; HttpOnly; Secure; SameSite=None");

        return new ResponseEntity<>(HttpStatus.OK);
    }

}
