package com.weatherclaus.be.user.controller;

import com.weatherclaus.be.user.dto.EmailRequest;
import com.weatherclaus.be.user.service.EmailService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@Slf4j
@RestController
@RequiredArgsConstructor
public class EmailController {

    private final EmailService emailService;

    //    인증번호 보내기
    @PostMapping("/sendEmail")
    public ResponseEntity<?> sendEmail(@Valid @RequestBody EmailRequest request) {

        emailService.sendContactEmail(request.getEmail());
        return ResponseEntity.ok("Email sent successfully");
    }


    //    인증번호 검증
    @PostMapping("/verify")
    public ResponseEntity<String> checkVerificationCode(@RequestBody Map<String, String> request) {
        String email = request.get("email");
        String code = request.get("code");

        // 서비스에서 검증 처리
        String result = emailService.verifyCode(email, code);

        if (result.equals("Verification successful!")) {
            return ResponseEntity.ok(result);
        } else {
            return ResponseEntity.badRequest().body(result);
        }
    }
}

