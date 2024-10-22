package com.weatherclaus.be.user.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdatePasswordRequest {


    @NotBlank
    @Size(min = 6)
    private String password;

    @NotBlank
    @Size(min = 6)
    private String password2;
}
