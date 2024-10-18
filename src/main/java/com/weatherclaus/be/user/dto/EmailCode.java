package com.weatherclaus.be.user.dto;


import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EmailCode {

    @Email
    @NotBlank
    private String email;


    @NotBlank
    @Size(min = 1)
    private String code;

}
