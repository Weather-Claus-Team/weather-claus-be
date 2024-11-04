package com.weatherclaus.be.user.controller;

import com.weatherclaus.be.user.dto.request.*;
import com.weatherclaus.be.user.dto.validation.EmailCodeRequestValidator;
import com.weatherclaus.be.user.dto.validation.JoinRequestValidator;
import com.weatherclaus.be.user.service.UserService;
import com.weatherclaus.be.common.ResponseDto;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/users")
@Slf4j
public class UserController {

    private final UserService userService;

    private final JoinRequestValidator joinRequestValidator;

    private final EmailCodeRequestValidator emailCodeRequestValidator;



    @InitBinder("joinRequest")
    protected void initBinderForJoinRequest(WebDataBinder binder) {
        binder.addValidators(joinRequestValidator);
    }

    @InitBinder("emailCodeRequest")
    protected void initBinderForEmailCode(WebDataBinder binder) {
        binder.addValidators(emailCodeRequestValidator);
    }




    // 회원가입
    @PostMapping
    public ResponseEntity<ResponseDto<?>> joinProcess(@Valid @RequestBody JoinRequest joinRequest) {


        userService.registerUser(joinRequest);

        return new ResponseEntity<>(
                new ResponseDto<>("success", "Join Success", null, null, 200),
                HttpStatus.OK
        );

    }


    // 아이디(username) 체크
    @PostMapping("/username")
    public ResponseEntity<ResponseDto<?>> duplicateCheck(@Valid @RequestBody UsernameRequest usernameDTO) {

        userService.usernameDuplicateCheck(usernameDTO.getUsername());

        return new ResponseEntity<>(
                new ResponseDto<>("success", "username valid", null, null, 200),
                HttpStatus.OK);
    }


    // 이메일 인증번호 발송
    @PostMapping("/email")
    public ResponseEntity<ResponseDto<?>> sendEmail(@Valid @RequestBody EmailRequest emailDTO) {
        userService.sendEmailVerification(emailDTO.getEmail());

        return new ResponseEntity<>(
                new ResponseDto<>("success", "Email sent successfully", null, null, 200),
                HttpStatus.OK);
    }


    // 이메일 인증번호 확인
    @PostMapping("/email-code")
    public ResponseEntity<ResponseDto<?>> checkVerificationCode(@Valid @RequestBody EmailCodeRequest emailCodeRequest) {

        userService.verifyEmailCode(emailCodeRequest);

        return new ResponseEntity<>(
                new ResponseDto<>("success", "email-code Success", null, null, 200),
                HttpStatus.OK);

    }

    @PostMapping("/email-find")
    public ResponseEntity<ResponseDto<?>> emailSendUsername(@Valid @RequestBody EmailRequest emailRequest) {

        userService.sendUsername(emailRequest);

        return new ResponseEntity<>(
                new ResponseDto<>("success", "email-send Success", null, null, 200),
                HttpStatus.OK);
    }






}