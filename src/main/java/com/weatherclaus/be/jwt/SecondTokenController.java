package com.weatherclaus.be.jwt;

import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class SecondTokenController {

    private final JWTUtil jwtUtil;

    @PostMapping("/api/st")
    public ResponseEntity<?> secondIssue(HttpServletRequest request, HttpServletResponse response) {

        String accessToken = request.getHeader("Authorization");


        // 토큰이 없다면 다음 필터로 넘김
        if (accessToken == null || !accessToken.startsWith("Bearer ")) {

            return new ResponseEntity<>("access token null", HttpStatus.BAD_REQUEST);
        }

        accessToken = accessToken.substring(7);


        //expired check
        try {
            jwtUtil.isExpired(accessToken);
        } catch (ExpiredJwtException e) {

            //response status code
            return new ResponseEntity<>("access token expired", HttpStatus.UNAUTHORIZED);
        }

        // 토큰이 refresh인지 확인 (발급시 페이로드에 명시)
        String category = jwtUtil.getCategory(accessToken);

        if (!category.equals("access")) {

            //response status code
            return new ResponseEntity<>("invalid access token", HttpStatus.BAD_REQUEST);
        }

        String username = jwtUtil.getUsername(accessToken);

        String secondToken = jwtUtil.createSecondJwt(username);

        //response
        response.setHeader("Second", "Bearer "+ secondToken); // "Bearer "+
        return new ResponseEntity<>(HttpStatus.OK);



    }

}
