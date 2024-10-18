package com.weatherclaus.be.user.controller;

import com.weatherclaus.be.user.dto.EmailCode;
import com.weatherclaus.be.user.dto.JoinDTO;
import com.weatherclaus.be.user.dto.UsernameDTO;
import com.weatherclaus.be.user.service.EmailService;
import com.weatherclaus.be.user.service.JoinService;
import com.weatherclaus.be.common.ResponseDto;
import com.weatherclaus.be.user.dto.EmailDTO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/users")
@Slf4j
public class JoinController {

    private final JoinService joinService;


    private final EmailService emailService;


    // 회원가입
    @PostMapping
    public ResponseEntity<ResponseDto<?>> joinProcess(@Valid @RequestBody JoinDTO joinDTO) {



        joinService.registerUser(joinDTO);

        return new ResponseEntity<>(
                new ResponseDto<>("success", "Join Success", null, null, 200),
                HttpStatus.OK
        );

    }







    // 아이디(username) 체크
    @PostMapping("/username")
    public ResponseEntity<ResponseDto<?>> duplicateCheck(@Valid @RequestBody UsernameDTO usernameDTO) {

        joinService.usernameDuplicateCheck(usernameDTO.getUsername());

        return new ResponseEntity<>(
                new ResponseDto<>("success", "username valid", null, null, 200),
                HttpStatus.OK);
    }


    // 이메일 인증번호 발송
    @PostMapping("/email")
    public ResponseEntity<ResponseDto<?>> sendEmail(@Valid @RequestBody EmailDTO emailDTO) {

        emailService.sendContactEmail(emailDTO.getEmail());

        return new ResponseEntity<>(
                new ResponseDto<>("success", "Email sent successfully", null, null, 200),
                HttpStatus.OK);
    }


    // 이메일 인증번호 확인
    @PostMapping("/email-code")
    public ResponseEntity<ResponseDto<?>> checkVerificationCode(@Valid @RequestBody EmailCode emailCode) {

        emailService.verifyCode(emailCode);

        return new ResponseEntity<>(
                new ResponseDto<>("success", "email-code Success", null, null, 200),
                HttpStatus.OK);

    }


}