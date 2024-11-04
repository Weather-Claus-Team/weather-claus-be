package com.weatherclaus.be.user.dto.request;


import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EmailCodeRequest {

    @Email
    @NotBlank
    private String email;


    @NotBlank
    @Size(min = 1)
    private String code;

}
