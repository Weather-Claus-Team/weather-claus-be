package com.weatherclaus.be.user.controller;

import com.weatherclaus.be.user.dto.JoinDTO;
import com.weatherclaus.be.user.service.JoinService;
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

    // 회원가입
    @PostMapping("/join")
    public ResponseEntity<ResponseDto<?>> joinProcess(@RequestBody JoinDTO joinDTO) {

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
