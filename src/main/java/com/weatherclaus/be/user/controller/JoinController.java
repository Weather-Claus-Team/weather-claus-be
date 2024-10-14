package com.weatherclaus.be.user.controller;

import com.weatherclaus.be.user.dto.JoinDTO;
import com.weatherclaus.be.user.service.JoinService;
import com.weatherclaus.be.user.service.RecaptchaService;
import com.weatherclaus.be.weather.dto.ResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/security")
public class JoinController {

    private final JoinService joinService;

    private final RecaptchaService recaptchaService;

    // 회원가입
    @PostMapping("/join")
    public ResponseEntity<ResponseDto<?>> joinProcess(@RequestBody JoinDTO joinDTO) {

        boolean isHuman = recaptchaService.verifyRecaptcha(joinDTO.getToken());

        if (!isHuman) {
//          일단은 이렇게 하는데 이부분은 오히려 서비스에서 처리하고 컨트롤러 깔끔하게 처리하는 로직으로 가져가자. refactoring 요소
            ResponseDto.ErrorDetails errorDetails = new ResponseDto.ErrorDetails("Invalid Argument", "invalid");

            return new ResponseEntity<>(
                    new ResponseDto<>("fail", "Invalid argument", null, errorDetails, 400),
                    HttpStatus.BAD_REQUEST);
        }

        joinService.joinProcess(joinDTO);

        return new ResponseEntity<>(
                new ResponseDto<>("success", "Join Success", null, null, 200),
                HttpStatus.OK
        );

    }







    // 아이디 체크 (예정)
    @GetMapping("/duplicateIdCheck")
    public String duplicateCheck(JoinDTO joinDTO) {

        String email = joinDTO.getEmail();



        return "duplicateCheck";
    }

    // 비밀번호 체크 (예정)
    @PostMapping("/passwordcheck")
    public String passwordCheck(@RequestBody JoinDTO joinDTO) {


        String password = joinDTO.getPassword();
        String checkpassword = joinDTO.getCheckpassword();



        return "passwordcheck";
    }

    // 이메일 인증번호 발송 (예정)
    @GetMapping("/verify")
    public String verify(@RequestBody JoinDTO joinDTO) {
        String email = joinDTO.getEmail();


        return "verify";
    }

    // 이메일 인증번호 확인 (예정)
    @GetMapping("/verify/{number}")
    public String virify2(@PathVariable Long number){

        return "verified";
    }


}
