package com.weatherclaus.be.user.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EmailRequest {


    @NotBlank(message = "이메일을 입력해 주세요")
    @Email(message = "이메일 형식이어야 합니다.")
    private String email;
}
