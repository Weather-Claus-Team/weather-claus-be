package com.weatherclaus.be.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class EmailRequest {



    @NotBlank(message = "이메일은 필수항목입니다.")
    @Email(message = "올바른 이메일 주소를 입력해주세요.")
    @Size(min = 4, message = "올바른 이메일 주소를 입력해주세요.")
    private String email;

}