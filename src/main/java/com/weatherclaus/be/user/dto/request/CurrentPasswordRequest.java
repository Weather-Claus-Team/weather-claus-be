package com.weatherclaus.be.user.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CurrentPasswordRequest {


    @NotBlank
    private String password;

}
